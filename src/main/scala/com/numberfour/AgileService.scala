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
import com.numberfour.domain.Team
import java.util.NoSuchElementException
import spray.http.StatusCodes
import spray.json._
import spray.httpx.SprayJsonSupport._
import spray.routing.directives.LoggingMagnet

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
    path("") { // direct access to the app context is forbidden
      respondWithStatus(StatusCodes.BadRequest) {
        complete {
          "Invalid request. Please check your URL."
        }
      }
    } ~ path("api" / "teams" / IntNumber) { id =>
      get {
        respondWithMediaType(`application/json`) {
          object TeamJsonProtocol extends DefaultJsonProtocol {
            implicit val teamFormat = jsonFormat3(Team)
          }
          import TeamJsonProtocol._

          val team_ = Team(0, "", 0).findById(id)

          if (team_.isEmpty) {
            respondWithStatus(StatusCodes.NotFound) {
              complete { "TEAM NOT FOUND" }
            }
          } else {
            val team = team_.get
            val jsonTeam = team.toJson
            complete {
              jsonTeam.compactPrint
            }
          }
        }
      }
    } ~ path("api" / "teams") {
      post {
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
            respondWithStatus(StatusCodes.Created) {
              complete {
                jsonTeam.compactPrint
              }
            }
          }
        }
      }
    }
}