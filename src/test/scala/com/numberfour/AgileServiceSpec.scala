package com.numberfour

import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest
import spray.http._
import StatusCodes._

class AgileServiceSpec extends Specification with Specs2RouteTest with AgileService {
  def actorRefFactory = system

  "AgileService" should {

    "return a greeting for GET requests to the root path" in {
      Get() ~> myRoute ~> check {
        entityAs[String] must contain("Say hello")
      }
    }

    "leave GET requests to other paths unhandled" in {
      Get("/kermit") ~> myRoute ~> check {
        handled must beFalse
      }
    }

    "return a MethodNotAllowed error for PUT requests to the root path" in {
      Put() ~> sealRoute(myRoute) ~> check {
        status === MethodNotAllowed
        entityAs[String] === "HTTP method not allowed, supported methods: GET"
      }
    }
  }
}