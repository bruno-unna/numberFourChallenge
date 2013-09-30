package com.numberfour

import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest
import spray.http._
import StatusCodes._
import com.numberfour.domain.Team
import spray.httpx.unmarshalling.Unmarshaller
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.slf4j.Logger
import spray.httpx.marshalling.Marshaller
import spray.http.MediaTypes._

@RunWith(classOf[JUnitRunner])
class AgileServiceSpec extends Specification with Specs2RouteTest with AgileService {
  def actorRefFactory = system

  implicit val teamMarshaller = Marshaller.of[Team](`application/json`) {
    (team, ct, ctx) => ctx.marshalTo(HttpEntity(ct, """{ "name": """" + team.name + """" }"""))
  }

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

    // TODO prepare the tests fixture (db cleaning, github cleaning)

    "adding a team via POST should return the team with a 201 header" in {

      Post("/api/teams", Team(0, "Team Foo", 0)) ~> addHeader("Content-Type", "application/json") ~> route ~> check {
        status === Created
        //        entityAs[String] must contain("first")
      }
    }
  }
}