package uk.gov.homeoffice.akka.http

import scala.concurrent.Future
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{RejectionHandler, Route, RouteConcatenation}
import akka.stream.ActorMaterializer
import grizzled.slf4j.Logging

/**
  * Boot your application with your required routings e.g.
  *
  * object ExampleBoot extends App with AkkaHttpBoot {
  *   boot(ExampleRouting1 ~ ExampleRouting2)
  * }
  *
  * "boot" defaults to using Akka Http defaults for the likes of failure handling.
  * In order to add customisations, provide "boot" a second implicit argument to configure the service.
  */
trait AkkaHttpBoot extends RouteConcatenation with Logging {
  this: App =>

  def boot(routings: Routing*)(implicit config: AkkaHttpConfig): Future[ServerBinding] = {
    require(routings.nonEmpty, "No routes declared")
    info(s"""Booting ${routings.size} ${if (routings.size > 1) "routes" else "route"} on port ${config.port}""")

    implicit val system = config.system
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher // Needed for the future flatMap/onComplete in shutdown hook
    implicit val rejectionHandler = config.rejectionHandler.getOrElse(RejectionHandler.default)

    val concatenatedRoutes = concat(routings.map(_.route): _*)

    val routes = config.exceptionHandler.fold(concatenatedRoutes) { exceptionHandler =>
      handleExceptions(exceptionHandler)(concatenatedRoutes)
    }

    bindAndHandle(routes)
  }

  protected def bindAndHandle(route: Route)(implicit config: AkkaHttpConfig): Future[ServerBinding] = {
    implicit val system = config.system
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher // Needed for the future flatMap/onComplete in shutdown hook
    implicit val rejectionHandler = config.rejectionHandler.getOrElse(RejectionHandler.default)

    val serverBinding = Http().bindAndHandle(route, "0.0.0.0", config.port)

    sys addShutdownHook {
      warn("System shutting down...")

      serverBinding
        .flatMap(_.unbind()) // Trigger unbinding from the port
        .onComplete { _ => system.terminate() }
    }

    serverBinding
  }
}