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

trait ProjectService extends HttpService {

  // some marshalling will be needed:

  object IncomingProjectJsonProtocol extends DefaultJsonProtocol {
    implicit val subprojectFormat = jsonFormat4(SubProject)
    implicit val projectFormat = jsonFormat8(Project)
  }
  import IncomingProjectJsonProtocol._

  val projectRoute =
    path("api" / "projects" / IntNumber) { id =>
      get {
        respondWithMediaType(`application/json`) {
          val project_ = ProjectManager.findById(id)

          if (project_.isEmpty) {
            respondWithStatus(StatusCodes.NotFound) {
              complete { "TEAM NOT FOUND" }
            }
          } else {
            val project = project_.get
            val jsonProject = project.toJson
            complete { jsonProject.compactPrint }
          }
        }
      }
    } ~ path("api" / "projects") {
      post {
        entity(as[SubProject]) { subproject =>

          // from this project, only a subset of attributes is relevant; 
          // see the corresponding comment in class TeamService 
          val project = ProjectManager.create(subproject.name, subproject.description, subproject.teamId)

          // at this point the project has been created, is persistent 
          // and is related to a github repository
          val jsonProject = project.toJson
          respondWithMediaType(`application/json`) {
            respondWithStatus(StatusCodes.Created) {
              complete {
                jsonProject.compactPrint
              }
            }
          }
        }
      }
    }

}