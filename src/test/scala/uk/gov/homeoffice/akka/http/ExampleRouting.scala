package uk.gov.homeoffice.akka.http

import org.json4s.JsonAST.{JObject, JString}
import org.scalactic.{Bad, Good}
import uk.gov.homeoffice.akka.http.marshal.{GoodOrJsonErrorMarshaller, Json4sMarshaller}
import uk.gov.homeoffice.json.JsonError

object ExampleRouting extends ExampleRouting

trait ExampleRouting extends Routing with Json4sMarshaller with GoodOrJsonErrorMarshaller {
  val route =
    pathPrefix("example") {
      path("error") {
        get {
          throw new IllegalStateException("Oh dear")
        }
      } ~
      path("or" / "good") {
        get {
          complete(Good("Very good"))
        }
      } ~
      path("or" / "bad") {
        get {
          complete(Bad(JsonError(JObject("key" -> JString("value")), Some("Error message"), Some(new Exception("Exception message")))))
        }
      } ~
      pathEndOrSingleSlash {
        get {
          complete {
            JObject("status" -> JString("Congratulations"))
          }
        }
      } ~
      post {
        path("submission") {
          complete("blah")
        }
      }
    }
}