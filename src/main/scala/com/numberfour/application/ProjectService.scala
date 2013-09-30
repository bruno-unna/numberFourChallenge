package com.numberfour.application

import spray.routing.Directives
import scala.concurrent.ExecutionContext
import akka.actor.ActorRef

class ProjectService(registration: ActorRef)
                         (implicit executionContext: ExecutionContext)
  extends Directives {

  val route =
    path("register") {
      post {
        complete {
          "OK"
        }
      }
    }

}
