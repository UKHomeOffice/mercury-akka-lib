name := "akka-scala-lib"

organization := "uk.gov.homeoffice"

scalaVersion := "2.11.8"

scalacOptions ++= Seq(
  "-feature",
  "-language:implicitConversions",
  "-language:higherKinds",
  "-language:existentials",
  "-language:reflectiveCalls",
  "-language:postfixOps",
  "-Yrangepos",
  "-Yrepl-sync"
)

javaOptions in Test += "-Dconfig.resource=application.test.conf"

fork in run := true

fork in Test := true

fork in IT := true

publishArtifact in Test := true

enablePlugins(SiteScaladocPlugin)

lazy val IT = config("it") extend Test

lazy val root = project.in(file("."))
  .configs(IntegrationTest)
  .settings(Defaults.itSettings: _*)
  .configs(IT)
  .settings(inConfig(IT)(Defaults.testSettings) : _*)
  .settings(Revolver.settings)