package com.numberfour.domain

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.MongoClient
import com.mongodb.casbah.commons.MongoDBObject
import com.numberfour.infrastructure.Autoincremental

case class Team(id: Int, name: String, members: Int)

// only needed when receiving a request with a json-represented team (not all fields are supplied):
case class SubTeam(name: String)

case object TeamManager extends Autoincremental {

  val teamCollection = db("team")

  def create(name: String): Team = {
    val selector = MongoDBObject("name" -> name)
    val readTeam = teamCollection.findOne(selector)
    val team: Team = if (readTeam.isDefined) {
      val t = readTeam.get
      findById(t.getAs[Int]("_id").get).get
    } else {
      val nextId = calcNextId("team")
      val insertable = MongoDBObject("_id" -> nextId, "name" -> name, "members" -> 0)
      teamCollection.insert(insertable)
      // TODO validate correct db write

      Team(nextId, name, 0)
    }
    team
  }

  def findById(id: Int): Option[Team] = {
    val selector = MongoDBObject("_id" -> id)
    val readTeam = teamCollection.findOne(selector)
    if (readTeam.isEmpty) None
    else {
      val t = readTeam.get
      val name = t.getAs[String]("name").get
      val members = t.getAs[Int]("members").get
      Some(Team(id, name, members))
    }
  }

}
