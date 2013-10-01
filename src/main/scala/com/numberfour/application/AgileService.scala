package com.numberfour.application

import com.numberfour.domain.SubTeam
import com.numberfour.domain.Team
import akka.actor.Actor
import spray.http.MediaTypes._
import spray.json.pimpAny
import spray.routing.Directive.pimpApply
import spray.routing.HttpService
import spray.routing.directives.CompletionMagnet.fromObject
import com.numberfour.domain.Team
import java.util.NoSuchElementException
import spray.http.StatusCodes
import spray.json._
import DefaultJsonProtocol._
import spray.httpx.SprayJsonSupport._
import spray.routing.directives.LoggingMagnet
import com.numberfour.domain.TeamManager

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

trait AgileService extends HttpService with TeamService with ProjectService {

  // TODO create a trait to treat the abnormal cases, before releasing 
  // the route evaluation to the specific services
  val route = {
    teamRoute ~ projectRoute
  }
}