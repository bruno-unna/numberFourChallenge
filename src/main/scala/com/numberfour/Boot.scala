package com.numberfour

import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.actorRef2Scala
import akka.io.IO
import spray.can.Http
import com.numberfour.application.AgileServiceActor
import com.typesafe.config.ConfigFactory

object Boot extends App {

  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("on-spray-can")

  // create and start our service actor
  val service = system.actorOf(Props[AgileServiceActor], "agile-service")

  val config = ConfigFactory.load()
  val configuredHost = config.getString("spray.can.server.host")
  val configuredPort = config.getInt("spray.can.server.port")

  // start a new HTTP server on port 8080 with our service actor as the handler
  IO(Http) ! Http.Bind(service, interface = configuredHost, port = configuredPort)
}