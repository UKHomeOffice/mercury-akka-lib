package uk.gov.homeoffice.akka.http

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.{Directives, ExceptionHandler, RejectionHandler}
import org.json4s.JsonAST.{JObject, JString}
import uk.gov.homeoffice.akka.http.marshal.Json4sMarshaller
import uk.gov.homeoffice.akka.http.unmarshal.Unmarshallers

/**
  * Example of booting an Akka Http microservice
  */
object ExampleBoot extends App with AkkaHttpBoot {
  implicit val config = AkkaHttpConfig()

  boot(ExampleRouting1, ExampleRouting2)
}

/**
  * Example of booting an Akka Http microservice using custom failure handling
  */
object ExampleBootWithFailureHandling extends App with AkkaHttpBoot with Json4sMarshaller with Unmarshallers with Directives {
  val rejectionHandler = RejectionHandler.newBuilder()
    .handleNotFound { complete(NotFound -> JObject("error" -> JString("Whoops"))) }
    .result()

  val exceptionHandler = ExceptionHandler {
    case _: TestException =>
      extractUri { uri =>
        complete(UnprocessableEntity -> "I'm sorry but this does not work")
      }
  }

  implicit val akkaHttpConfig = AkkaHttpConfig(rejectionHandler = Some(rejectionHandler), exceptionHandler = Some(exceptionHandler))

  boot(ExampleRouting1, ExampleRouting2, ExampleRoutingExceptionHandler)
}

/**
  * Routing example 1
  * <pre>
  * curl http://localhost:9100/example1
  * </pre>
  */
object ExampleRouting1 extends ExampleRouting1

trait ExampleRouting1 extends Routing with Json4sMarshaller {
  val route =
    pathPrefix("example1") {
      pathEndOrSingleSlash {
        get {
          complete { OK -> JObject("status" -> JString("Congratulations 1")) }
        }
      }
    }
}

/**
  * Routing example 2
  * <pre>
  * curl http://localhost:9100/example2
  * </pre>
  */
object ExampleRouting2 extends ExampleRouting2

trait ExampleRouting2 extends Routing with Json4sMarshaller {
  val route =
    pathPrefix("example2") {
      pathEndOrSingleSlash {
        get {
          complete { JObject("status" -> JString("Congratulations 2")) }
        }
      }
    }
}

/**
  * Routing example to see failure handling
  * <pre>
  * curl http://localhost:9100/example-error
  * </pre>
  */
object ExampleRoutingExceptionHandler extends ExampleRoutingExceptionHandler

trait ExampleRoutingExceptionHandler extends Routing {
  val route =
    pathPrefix("example-error") {
      pathEndOrSingleSlash {
        get {
          complete { throw new TestException("This sounds daft, but your error was a success!") }
        }
      }
    }
}

class TestException(s: String) extends Exception(s)