package uk.gov.homeoffice.akka.http

import akka.http.scaladsl.model.MediaTypes._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.{ExceptionHandler, MethodRejection, RejectionHandler, Route}
import org.json4s._
import org.specs2.mutable.Specification

class ExampleRoutingSpec extends Specification with RouteSpecification with ExampleRouting {
  "Example routing" should {
    "be available" in {
      Get("/example") ~> route ~> check {
        status mustEqual OK
        contentType.mediaType mustEqual `application/json`
        (responseAs[JValue] \ "status").extract[String] mustEqual "Congratulations"
      }
    }

    "indicate when a required route is not recognised using default rejection" in {
      Get("/example/non-existing") ~> route ~> check {
        rejection must beAnInstanceOf[MethodRejection]
      }
    }

    "indicate when a required route is not recognised using default rejection" in {
      Get("/example/non-existing") ~> Route.seal(route) ~> check {
        status mustEqual MethodNotAllowed
        contentType.mediaType mustEqual `text/plain`
        responseAs[String] mustEqual "HTTP method not allowed, supported methods: POST"
      }
    }

    "indicate when a required route is not recognised using custom rejection" in {
      implicit val rejectionHandler = RejectionHandler.newBuilder()
        .handleAll[MethodRejection] { _ =>
          complete(MethodNotAllowed -> JObject("error" -> JString("Whoops")))
        }
        .result()

      Get("/example/non-existing") ~> Route.seal(route) ~> check {
        status mustEqual MethodNotAllowed
        contentType.mediaType mustEqual `application/json`
        (responseAs[JValue] \ "error").extract[String] mustEqual "Whoops"
      }
    }

    "use default exception handling" in {
      Get("/example/error") ~> route ~> check {
        status mustEqual InternalServerError
      }
    }

    "use custom exception handling" in {
      implicit val exceptionHandler = ExceptionHandler {
        case _: IllegalStateException =>
          extractUri { uri =>
            complete(UnprocessableEntity -> "I'm sorry but this does not work")
          }
      }

      Get("/example/error") ~> route ~> check {
        status mustEqual UnprocessableEntity
      }
    }
  }

  "Example router marshalling/unmarshalling" should {
    "marshal an entity" in {
      Get("/example/or/good") ~> route ~> check {
        status mustEqual OK
        responseAs[JValue] mustEqual JString("Very good")
      }
    }

    "marshal a JSON error" in {
      Get("/example/or/bad") ~> route ~> check {
        status mustEqual UnprocessableEntity

        responseAs[JValue] must beLike {
          case json: JValue =>
            json \ "json" mustEqual JObject("key" -> JString("value"))
            json \ "error" mustEqual JString("Error message")
        }
      }
    }
  }
}