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

  var givenId: Int = 0

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
        givenId = t.id
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

  }
}