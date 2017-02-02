package uk.gov.homeoffice.akka.http.marshal

import scala.concurrent.{ExecutionContext, Future}
import akka.http.scaladsl.marshalling.Marshaller
import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.{HttpEntity, HttpResponse, StatusCode}
import org.json4s.JValue
import org.json4s.jackson.JsonMethods._
import grizzled.slf4j.Logging
import uk.gov.homeoffice.json.JsonFormats

object Json4sMarshaller extends Json4sMarshaller

trait Json4sMarshaller extends JsonFormats with Logging {
  val jsonMarshal: (StatusCode, JValue) => HttpResponse = { case (statusCode, json) =>
    HttpResponse(status = statusCode, entity = HttpEntity(`application/json`, pretty(render(json))))
  }

  implicit val jsonDefaultMarshaller = Marshaller.withFixedContentType[JValue, HttpResponse](`application/json`) { json =>
    jsonMarshal(OK, json)
  }

  implicit val jsonMarshaller = Marshaller.withFixedContentType[(StatusCode, JValue), HttpResponse](`application/json`) { case (statusCode, json) =>
    jsonMarshal(statusCode, json)
  }

  implicit def jsonDefaultFutureMarshaller(implicit ec: ExecutionContext) = Marshaller.withFixedContentType[Future[JValue], Future[HttpResponse]](`application/json`) {
    _.map { json =>
      jsonMarshal(OK, json)
    }
  }

  implicit def jsonFutureMarshaller(implicit ec: ExecutionContext) = Marshaller.withFixedContentType[Future[(StatusCode, JValue)], Future[HttpResponse]](`application/json`) {
    _.map { case (statusCode, json) =>
      jsonMarshal(statusCode, json)
    }
  }
}