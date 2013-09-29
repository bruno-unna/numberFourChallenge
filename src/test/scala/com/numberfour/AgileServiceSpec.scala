package com.numberfour

import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest
import spray.http._
import StatusCodes._
import com.numberfour.domain.Project
import spray.httpx.unmarshalling.Unmarshaller

class AgileServiceSpec extends Specification with Specs2RouteTest with AgileService {
  def actorRefFactory = system

  "AgileService" should {

    "return a greeting for GET requests to the root path" in {
      Get() ~> route ~> check {
        entityAs[String] must contain("Say hello")
      }
    }

    "leave GET requests to other paths unhandled" in {
      Get("/kermit") ~> route ~> check {
        handled must beFalse
      }
    }

    "return a MethodNotAllowed error for PUT requests to the root path" in {
      Put() ~> sealRoute(route) ~> check {
        status === MethodNotAllowed
        entityAs[String] === "HTTP method not allowed, supported methods: GET"
      }
    }

    "adding a project via POST should return the project" in {
      Post("project").withEntity(HttpEntity("first")) ~> route ~> check {
        status === OK
        entityAs[String] must contain("first")
      }
    }
  }
}