package uk.gov.homeoffice.akka.http

import scala.concurrent.duration._
import akka.http.scaladsl.testkit.{RouteTest, RouteTestTimeout, TestFrameworkInterface}
import akka.testkit._
import org.specs2.execute.{Failure, FailureException}
import org.specs2.mutable.SpecificationLike
import org.specs2.specification.core.{Fragments, SpecificationStructure}
import org.specs2.specification.create.DefaultFragmentFactory
import uk.gov.homeoffice.akka.http.unmarshal.Unmarshallers

trait RouteSpecification extends RouteTest with Specs2Interface with Unmarshallers /*with JsonFormats with Json4sSupport*/ {
  this: SpecificationLike =>

  implicit def routeTestTimeout: RouteTestTimeout = RouteTestTimeout(5.seconds dilated)
}

/**
  * Use this mixin until Akka Http has its own Specs2 implementation of TestFrameworkInterface.
  * There is an outstanding FIXME in akka.http.scaladsl.testkit.RouteTest regarding this matter.
  */
trait Specs2Interface extends TestFrameworkInterface with SpecificationStructure {
  def failTest(msg: String) = {
    val trace = new Exception().getStackTrace.toList
    val fixedTrace = trace.drop(trace.indexWhere(_.getClassName.startsWith("org.specs2")) - 1)
    throw FailureException(Failure(msg, stackTrace = fixedTrace))
  }

  override def map(fs: => Fragments) = super.map(fs).append(DefaultFragmentFactory.step(cleanUp()))
}

trait NoAutoHtmlLinkFragments extends org.specs2.specification.dsl.ReferenceDsl {
  override def linkFragment(alias: String) = super.linkFragment(alias)

  override def seeFragment(alias: String) = super.seeFragment(alias)
}