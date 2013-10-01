package com.numberfour.application

import spray.http.MediaTypes._
import spray.json.pimpAny
import spray.routing.Directive.pimpApply
import spray.routing.HttpService
import spray.routing.directives.CompletionMagnet.fromObject
import java.util.NoSuchElementException
import spray.http.StatusCodes
import spray.json._
import DefaultJsonProtocol._
import spray.httpx.SprayJsonSupport._
import spray.routing.directives.LoggingMagnet
import com.numberfour.domain._

trait TeamService extends HttpService {

  // some marshalling will be needed:

  object IncomingTeamJsonProtocol extends DefaultJsonProtocol {
    implicit val subteamFormat = jsonFormat1(SubTeam)
    implicit val teamFormat = jsonFormat3(Team)
  }
  import IncomingTeamJsonProtocol._

  val teamRoute =
    path("") { // direct access to the app context is forbidden
      respondWithStatus(StatusCodes.BadRequest) {
        complete {
          "Invalid request. Please check your URL."
        }
      }
    } ~ path("api" / "teams" / IntNumber) { id =>
      get {
        respondWithMediaType(`application/json`) {
          val team_ = TeamManager.findById(id)

          if (team_.isEmpty) {
            respondWithStatus(StatusCodes.NotFound) {
              complete { "TEAM NOT FOUND" }
            }
          } else {
            val team = team_.get
            val jsonTeam = team.toJson
            complete { jsonTeam.compactPrint }
          }
        }
      }
    } ~ path("api" / "teams") {
      post {
        entity(as[SubTeam]) { subteam =>

          // from this Team, only the name attribute is important, 
          // but a case object can't be created (which would be more elegant) 
          // because of the implicit transformations needed elsewhere
          val team = TeamManager.create(subteam.name)

          // at this point the team has been created and is persistent
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