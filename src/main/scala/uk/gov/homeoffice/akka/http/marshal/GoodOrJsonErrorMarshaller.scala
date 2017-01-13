package uk.gov.homeoffice.akka.http.marshal

import scala.pickling.Defaults._
import scala.pickling.json._
import scala.util.Try
import akka.http.scaladsl.marshalling.Marshaller
import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.{HttpEntity, HttpResponse}
import org.json4s.JValue
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization.write
import org.scalactic.{Bad, Good, Or}
import uk.gov.homeoffice.json.{JsonError, JsonFormats}

/**
  * Implicit responses for JsonError are of type <anything> Or JsonError
  * i.e. if not using custom response handling code and expecting the implicit functionality of this trait to be used, a response must match one of the declared marshallers here.
  * So marshalling handles either a Good(<anything>) or a Bad(JsonError).
  */
object GoodOrJsonErrorMarshaller extends GoodOrJsonErrorMarshaller

trait GoodOrJsonErrorMarshaller extends JsonFormats {
  implicit val goodOrJsonErrorMarshaller = Marshaller.withFixedContentType[_ Or JsonError, HttpResponse](`application/json`) {
    case Good(j: JValue) =>
      HttpResponse(status = OK, entity = HttpEntity(`application/json`, compact(render(j))))

    case Good(a: AnyRef) => Try {
      HttpResponse(status = OK, entity = HttpEntity(`application/json`, write(a)))
    } getOrElse {
      HttpResponse(status = OK, entity = HttpEntity(`text/plain(UTF-8)`, a.pickle.toString))
    }

    case Good(a) =>
      HttpResponse(status = OK, entity = HttpEntity(`text/plain(UTF-8)`, a.toString))

    case Bad(jsonError) =>
      HttpResponse(status = UnprocessableEntity, entity = HttpEntity(`application/json`, compact(render(jsonError.toJson))))
  }
}