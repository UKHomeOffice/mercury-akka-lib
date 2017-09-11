resolvers ++= Seq(
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
  "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/",
  "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
  "Kamon Repository" at "http://repo.kamon.io",
  "jitpack" at "https://jitpack.io",
  Resolver.bintrayRepo("hseeberger", "maven"),
  Resolver.bintrayRepo("findify", "maven")
)

transitiveClassifiers := Seq("sources")

val `akka-version` = "2.4.17"
val `akka-http-version` = "10.0.4"
val `akka-http-json-version` = "1.12.0"
val `mercury-io-lib-version` = "1.9.12"
val `mercury-test-lib-version` = "1.4.4"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % `akka-version`,
  "com.typesafe.akka" %% "akka-http" % `akka-http-version`,
  "de.heikoseeberger" %% "akka-http-json4s" % `akka-http-json-version`,
  "com.typesafe.akka" %% "akka-remote" % `akka-version`,
  "com.typesafe.akka" %% "akka-cluster-tools" % `akka-version`,
  "com.typesafe.akka" %% "akka-cluster-metrics" % `akka-version`,
  "com.typesafe.akka" %% "akka-stream" % `akka-version`,
  "com.typesafe.akka" %% "akka-slf4j" % `akka-version`,
  "de.flapdoodle.embed" % "de.flapdoodle.embed.mongo" % "1.50.2",
  "com.github.UKHomeOffice" %% "mercury-io-lib" % `mercury-io-lib-version`,
  "com.github.UKHomeOffice" %% "mercury-test-lib" % `mercury-test-lib-version`
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-testkit" % `akka-version` % Test,
  "com.typesafe.akka" %% "akka-http-testkit" % `akka-http-version` % Test,
  "com.github.UKHomeOffice" %% "mercury-io-lib" % `mercury-io-lib-version` % Test classifier "tests",
  "com.github.UKHomeOffice" %% "mercury-test-lib" % `mercury-test-lib-version` % Test classifier "tests"
)
