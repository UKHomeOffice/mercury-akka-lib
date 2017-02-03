package uk.gov.homeoffice.akka.http.unmarshal

import scala.concurrent.{ExecutionContext, Future}
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.unmarshalling._
import akka.stream.Materializer
import org.json4s.JValue
import org.json4s.jackson.parseJson

/**
  * Placeholder for generic unmarshalling
  */
object Unmarshallers extends Unmarshallers

trait Unmarshallers {
  implicit val `JSON from Response` = new FromResponseUnmarshaller[JValue] {
    override def apply(value: HttpResponse)(implicit ec: ExecutionContext, materializer: Materializer): Future[JValue] =
      Unmarshal(value).to[String].map(parseJson(_))
  }
}