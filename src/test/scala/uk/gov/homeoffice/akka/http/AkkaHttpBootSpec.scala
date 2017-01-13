package uk.gov.homeoffice.akka.http

import scala.concurrent.Future
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Route
import org.specs2.matcher.Scope
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification

class AkkaHttpBootSpec extends Specification with Mockito {
  trait Context extends Scope with AkkaHttpBoot with App {
    implicit val config = AkkaHttpConfig()

    override protected def bindAndHandle(route: Route)(implicit config: AkkaHttpConfig): Future[ServerBinding] =
      Future successful mock[ServerBinding]
  }

  "Booting Akka Http" should {
    "be successful" in new Context {
      boot(ExampleRouting)
      ok
    }

    "fail because of not providing any routes" in new Context {
      boot() must throwAn[IllegalArgumentException](message = "requirement failed: No routes declared")
    }
  }
}