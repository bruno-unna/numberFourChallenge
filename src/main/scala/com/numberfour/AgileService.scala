package com.numberfour

import com.numberfour.domain.SubTeam
import com.numberfour.domain.Team

import akka.actor.Actor
import spray.http.MediaTypes._
import spray.httpx.SprayJsonSupport.sprayJsonUnmarshaller
import spray.json.DefaultJsonProtocol
import spray.json.pimpAny
import spray.routing.Directive.pimpApply
import spray.routing.HttpService
import spray.routing.directives.CompletionMagnet.fromObject

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor

class AgileServiceActor extends Actor with AgileService {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(route)
}

// this trait defines our service behavior independently from the service actor
trait AgileService extends HttpService {

  val route =
    path("api" / "teams" / IntNumber) { id =>
      get {
        respondWithMediaType(`text/html`) { // XML is marshalled to `text/xml` by default, so we simply override here
          complete {
            <html>
              <body>
                <h1>Say hello to <i>spray-routing</i> on <i>spray-can</i>!</h1>
              </body>
            </html>
          }
        }
      }
    } ~ path("api" / "teams") {
      post {
        import spray.json._
        import spray.httpx.SprayJsonSupport._

        object IncomingTeamJsonProtocol extends DefaultJsonProtocol {
          implicit val teamFormat = jsonFormat1(SubTeam)
        }
        import IncomingTeamJsonProtocol._

        entity(as[SubTeam]) { subteam =>

          object TeamJsonProtocol extends DefaultJsonProtocol {
            implicit val teamFormat = jsonFormat3(Team)
          }
          import TeamJsonProtocol._

          val team = Team(1, subteam.name, 0).create()
          val jsonTeam = team.toJson
          respondWithMediaType(`application/json`) {
            complete {
              jsonTeam.compactPrint
            }
          }
        }

      }
    }
}