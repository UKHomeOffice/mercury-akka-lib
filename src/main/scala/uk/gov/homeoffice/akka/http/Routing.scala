package uk.gov.homeoffice.akka.http

import akka.http.scaladsl.server.{Directives, Route}

/**
 * Mix this trait into your "Routing" to define endpoints.
 * Note that you would probably want to mixin with this trait one or more Marshallers that provide some implicit functionality to handle responses to clients calling endpoints.
 * Even though custom code can be created to handle the responses, the implicits such as provided by GoodOrJsonErrorMarshaller can automatically handle JsonError responses, which is a good default for Routings that deal mainly with JSON.
 * See Marshallers ScalaDoc for more information.
 */
trait Routing extends Directives {
  def route: Route
}

object Routing {
  implicit class RoutingOps(routing: Routing) {
    def ~(routings: Seq[Routing]) = routing +: routings

    def ~(anotherRouting: Routing) = Seq(routing, anotherRouting)
  }

  implicit class SeqRoutingOps(routings: Seq[Routing]) {
    def ~(otherRoutings: List[Routing]) = routings ++ otherRoutings

    def ~(anotherRouting: Routing) = routings :+ anotherRouting
  }
}