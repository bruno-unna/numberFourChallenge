package com.numberfour

import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest
import spray.http._
import StatusCodes._
import com.numberfour.domain.Team
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.slf4j.Logger
import spray.http.MediaTypes._
import spray.httpx.marshalling._
import spray.httpx.unmarshalling._
import com.numberfour.domain.Team
import spray.json._
import DefaultJsonProtocol._
import com.mongodb.casbah.MongoClient
import com.mongodb.casbah.commons.MongoDBObject
import com.numberfour.application.AgileService
import com.numberfour.domain.Project

@RunWith(classOf[JUnitRunner])
class AgileServiceSpec extends Specification with Specs2RouteTest with AgileService {

  def actorRefFactory = system

  // in order to prepare the messages' payload, we are going to need a set of marshallers:  
  private implicit val teamMarshaller = Marshaller.of[Team](`application/json`) {
    (team, ct, ctx) => ctx.marshalTo(HttpEntity(ct, """{ "name": """" + team.name + """" }"""))
  }
  private implicit object teamUnmarshaller extends Unmarshaller[Team] {
    def apply(entity: HttpEntity): Deserialized[Team] = {
      object teamJsonProtocol extends DefaultJsonProtocol {
        implicit val teamFormat = jsonFormat3(Team)
      }
      import teamJsonProtocol._
      val jsonString = entity.asString
      val jsonAst = jsonString.asJson
      val result: Either[DeserializationError, Team] = try {
        Right(jsonAst.convertTo[Team])
      } catch {
        case e: Exception =>
          Left(deserializationError(e.getMessage()))
      }
      result
    }
  }
  private implicit val projectMarshaller = Marshaller.of[Project](`application/json`) {
    (project, ct, ctx) =>
      ctx.marshalTo(HttpEntity(ct, """{ "name": """" + project.name +
        """", "description": """" + project.description +
        """", "teamId": """ + project.teamId + """ }"""))
  }
  private implicit object projectUnmarshaller extends Unmarshaller[Project] {
    def apply(entity: HttpEntity): Deserialized[Project] = {
      object projectJsonProtocol extends DefaultJsonProtocol {
        implicit val projectFormat = jsonFormat8(Project)
      }
      import projectJsonProtocol._
      val jsonString = entity.asString
      val jsonAst = jsonString.asJson
      val result: Either[DeserializationError, Project] = try {
        Right(jsonAst.convertTo[Project])
      } catch {
        case e: Exception =>
          Left(deserializationError(e.getMessage()))
      }
      result
    }
  }

  var givenId: Int = 0 // yes: a var; sinful, but useful

  // now, the proper specification of the agile management service:
  sequential
  "AgileService" should {

    "reject direct GET requests to the root path" in {
      Get() ~> sealRoute(route) ~> check {
        status === BadRequest
      }
    }

    "leave GET requests to other paths unhandled" in {
      Get("/kermit") ~> route ~> check {
        handled must beFalse
      }
    }

    "adding a team via POST should return the team with a 201 header" in {
      Post("/api/teams", Team(0, "First Team", 0)) ~> addHeader("Content-Type", "application/json") ~> route ~> check {
        status === Created
        val t = entityAs[Team]
        givenId = t.id // in preparation for the get occurring later on
        t.name === "First Team"
        t.members === 0
      }
    }

    "reading a team via GET with the wrong id should fail" in {
      Get("/api/teams/-1") ~> sealRoute(route) ~> check {
        status === NotFound
      }
    }

    "reading a correct Team should deliver a correct Team" in {
      Get("/api/teams/" + givenId) ~> route ~> check {
        status === OK
        val t = entityAs[Team]
        t.id === givenId
        t.name === "First Team"
        t.members === 0
      }
    }

    "adding a project via POST should return the project with a 201 header" in {
      Post("/api/projects", Project(0, "Yet Another To-Do App", "What the world needed", 1, "", "", 0, 0)) ~>
        addHeader("Content-Type", "application/json") ~> route ~> check {
          status === Created
          val p = entityAs[Project]
          givenId = p.id // once again, in preparation for the get occurring later on
          p.name === "Yet Another To-Do App"
          p.description === "What the world needed"
          p.teamId === 0
        }
    }

    "reading a project via GET with the wrong id should fail" in {
      Get("/api/projects/-1") ~> sealRoute(route) ~> check {
        status === NotFound
      }
    }

    "reading a correct Project should deliver a correct Project" in {
      Get("/api/projects/" + givenId) ~> route ~> check {
        status === OK
        val p = entityAs[Project]
        p.id === givenId
        p.name === "Yet Another To-Do App"
        p.description === "What the world brauches"
        p.teamId === 0
      }
    }

  }
}