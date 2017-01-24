Akka - Reusable functionality
=============================
Akka reusable functionality such as clustering, scheduling and booting Akka Http.

Project built with the following (main) technologies:

- Scala

- SBT

- Akka

- Specs2

Introduction
------------
Useful functionality related to Akka e.g.
- Clustering via ClusterActorSystem
- Schedulers
- Booting an Akka Http microservice

Build and Deploy
----------------
The project is built with SBT. On a Mac (sorry everyone else) do:
```
brew install sbt
```

It is also a good idea to install Typesafe Activator (which sits on top of SBT) for when you need to create new projects - it also has some SBT extras, so running an application with Activator instead of SBT can be useful. On Mac do:
```
brew install typesafe-activator
```

To compile:
```
sbt compile
```
or
```
activator compile
```

To run the specs:
```
sbt test
```

To run integration specs:
```
sbt it:test
```

To run integration specs:
```
sbt it:test 
```

Dependencies
------------
At the time of writing, this module uses [JitPack](https://jitpack.io/).
If you would like your module to depend on this module, add the following to your build sbt resolvers:
```scala
resolvers ++= Seq(
  ...
  "jitpack" at "https://jitpack.io",
  ...
)
```

and within your dependencies refer to this module via its Github repository including user name (in this case UKHomeOffice) i.e.
```scala
libraryDependencies ++= Seq(
  ...
  "com.github.UKHomeOffice" %% "akka-scala-lib" % "1.9.6",
  "com.github.UKHomeOffice" %% "akka-scala-lib" % "1.9.6" % Test classifier "tests",
  ...
)
```

SBT - Revolver (keep things going while developing/testing)
-----------------------------------------------------------
[sbt-revolver](https://github.com/spray/sbt-revolver) is a plugin for SBT enabling a super-fast development turnaround for your Scala applications:

For development, you can use ~re-start to go into "triggered restart" mode.
Your application starts up and SBT watches for changes in your source (or resource) files.
If a change is detected SBT recompiles the required classes and sbt-revolver automatically restarts your application. 
When you press &lt;ENTER&gt; SBT leaves "triggered restart" and returns to the normal prompt keeping your application running.

Example Usage
-------------
- Actor scheduling:
```scala
class SchedulerSpec extends Specification with ActorSystemSpecification {
  "Actor" should {
    "be scheduled to act as a poller" in new ActorSystemContext {
      val exampleSchedulerActor = system actorOf Props(new ExampleSchedulerActor)
      exampleSchedulerActor ! IsScheduled
      expectMsgType[Scheduled]
    }

    "not be scheduled to act as a poller" in new ActorSystemContext {
      val exampleSchedulerActor = system actorOf Props(new ExampleSchedulerActor with NoSchedule)
      exampleSchedulerActor ! IsScheduled
      expectMsg(NotScheduled)
    }
  }
}

class ExampleSchedulerActor extends Actor with Scheduler {
  lazy val schedule: Cancellable = context.system.scheduler.schedule(initialDelay = 1 second, interval = 5 seconds, receiver = self, message = Schedule)

  def receive = LoggingReceive {
    case Schedule => println("Hello World!")
  }
}
```

- Create Akka Http routings - HTTP contract/gateway to your microservice:
```scala
object ExampleRouting1 extends ExampleRouting1

trait ExampleRouting1 extends Routing with Json4sMarshaller {
  val route =
    pathPrefix("example1") {
      pathEndOrSingleSlash {
        get {
          complete { OK -> JObject("status" -> JString("Congratulations 1")) }
        }
      }
    }
}

object ExampleRouting2 extends ExampleRouting2

trait ExampleRouting2 extends Routing with Json4sMarshaller {
  val route =
    pathPrefix("example2") {
      pathEndOrSingleSlash {
        get {
          complete { JObject("status" -> JString("Congratulations 2")) }
        }
      }
    }
}
```

- Create your application (App) utilitising your routings (as well as anything else e.g. configuration and booting/wiring Akka actors):
```scala
object ExampleBoot extends App with AkkaHttpBoot with Json4sMarshaller with Unmarshallers with Directives {
  val rejectionHandler = RejectionHandler.newBuilder()
    .handleNotFound { complete(NotFound -> JObject("error" -> JString("Whoops"))) }
    .result()

  val exceptionHandler = ExceptionHandler {
    case _: TestException =>
      extractUri { uri =>
        complete(UnprocessableEntity -> "I'm sorry but this does not work")
      }
  }

  implicit val akkaHttpConfig = AkkaHttpConfig(rejectionHandler = Some(rejectionHandler), exceptionHandler = Some(exceptionHandler))

  boot(ExampleRouting1, ExampleRouting2, ExampleRoutingExceptionHandler)
}
```

To run ExampleBoot:
```
sbt test:run
```

Akka Clustering
---------------
Cluster Singleton:

Actors can be managed in a cluster to run as a singleton - an actor will be distributed on multiple nodes, but only one will be running.

Your application.conf for a Cluster Singleton, can use the following template:
```javascript
akka {
  actor {
    provider = "akka.cluster.ClusterActorRefProvider"
  }

  remote {
    enabled-transports = ["akka.remote.netty.tcp"]

    netty.tcp {
      hostname = "127.0.0.1"
      port = 0 # To be overridden in code for each running node in a cluster
    }
  }

  cluster {
    seed-nodes = [
      "akka.tcp://your-actor-system@127.0.0.1:2551",
      "akka.tcp://your-actor-system@127.0.0.1:2552",
      "akka.tcp://your-actor-system@127.0.0.1:2553"
    ]

    roles = ["your-service"]
    min-nr-of-members = 2
    auto-down-unreachable-after = 30 seconds
  }
}
```

Each node that starts up on the same box would need a different port e.g. 2551, 2552 etc.
In production, the nodes would be on different boxes and so can all have the same ports and said port could then also be declared for akka.actor.remote.netty.tcp.port.

There is an example app showing a makeshift cluster of 3 nodes:
```scala
object ClusterActorSystemExampleApp extends App with Network {
  withConfig {
    // Imagine we are starting up 3 nodes on 3 separate boxes (here we simply utilise 3 separately configured ports).
    val actorSystem1 = ClusterActorSystem(1)
    val actorSystem2 = ClusterActorSystem(2)
    val actorSystem3 = ClusterActorSystem(3)
    ...
  }
}    
```

Releasing
---------
Version control of this library can be achieved through the SBT Release plugin e.g.
```
sbt release
```

where the following default value will be chosen:
- Continue with snapshots dependencies: no
- Release Version: current version without the qualifier (eg. 1.2.0-SNAPSHOT -> 1.2.0)
- Next Version: increase the minor version segment of the current version and set the qualifier to '-SNAPSHOT' (eg. 1.2.1-SNAPSHOT -> 1.3.0-SNAPSHOT)
- VCS tag: abort if the tag already exists
- VCS push:
    - Abort if no remote tracking branch is set up.
    - Abort if remote tracking branch cannot be checked (eg. via git fetch).
    - Abort if the remote tracking branch has unmerged commits.
    - Set release version and next version as command arguments

You can set the release version using the argument release-version and next version with next-version.

Example (within sbt):
```
release release-version 1.0.99 next-version 1.2.0-SNAPSHOT
```

or
```
release with-defaults
```