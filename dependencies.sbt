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

val `akka-version` = "2.4.16"
val `akka-http-version` = "10.0.2"
val `akka-http-json-version` = "1.11.0"
val `io-scala-lib-version` = "1.9.7"
val `test-scala-lib-version` = "1.4.4"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % `akka-version` withSources(),
  "com.typesafe.akka" %% "akka-http" % `akka-http-version` withSources(),
  "de.heikoseeberger" %% "akka-http-json4s" % `akka-http-json-version` withSources(),
  "com.typesafe.akka" %% "akka-remote" % `akka-version` withSources(),
  "com.typesafe.akka" %% "akka-cluster-tools" % `akka-version` withSources(),
  "com.typesafe.akka" %% "akka-cluster-metrics" % `akka-version` withSources(),
  "com.typesafe.akka" %% "akka-stream" % `akka-version` withSources(),
  "com.typesafe.akka" %% "akka-slf4j" % `akka-version` withSources(),
  "de.flapdoodle.embed" % "de.flapdoodle.embed.mongo" % "1.50.2" withSources(),
  "com.github.UKHomeOffice" %% "io-scala-lib" % `io-scala-lib-version` withSources(),
  "com.github.UKHomeOffice" %% "test-scala-lib" % `test-scala-lib-version` withSources()
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-testkit" % `akka-version` % Test withSources(),
  "com.typesafe.akka" %% "akka-http-testkit" % `akka-http-version` % Test withSources(),
  "com.github.UKHomeOffice" %% "io-scala-lib" % `io-scala-lib-version` % Test classifier "tests" withSources(),
  "com.github.UKHomeOffice" %% "test-scala-lib" % `test-scala-lib-version` % Test classifier "tests" withSources()
)