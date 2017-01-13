package uk.gov.homeoffice.akka.http

import akka.actor.ActorSystem
import akka.http.scaladsl.server.{ExceptionHandler, RejectionHandler}

case class AkkaHttpConfig(port: Int = 9100,
                          system: ActorSystem = ActorSystem("akka-http-service"),
                          rejectionHandler: Option[RejectionHandler] = None,
                          exceptionHandler: Option[ExceptionHandler] = None)