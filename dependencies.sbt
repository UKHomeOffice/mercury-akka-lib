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
val `akka-http-version` = "10.0.3"
val `akka-http-json-version` = "1.12.0"
val `io-scala-lib-version` = "1.9.12"
val `test-scala-lib-version` = "1.4.4"

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
  "com.github.UKHomeOffice" %% "io-scala-lib" % `io-scala-lib-version`,
  "com.github.UKHomeOffice" %% "test-scala-lib" % `test-scala-lib-version`
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-testkit" % `akka-version` % Test,
  "com.typesafe.akka" %% "akka-http-testkit" % `akka-http-version` % Test,
  "com.github.UKHomeOffice" %% "io-scala-lib" % `io-scala-lib-version` % Test classifier "tests",
  "com.github.UKHomeOffice" %% "test-scala-lib" % `test-scala-lib-version` % Test classifier "tests"
)