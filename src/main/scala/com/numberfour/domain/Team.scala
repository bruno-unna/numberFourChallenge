package com.numberfour.domain

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.MongoClient
import com.mongodb.casbah.commons.MongoDBObject

case class Team(id: Int, name: String, members: Int)

// only needed when receiving a request with a json-represented team (not all fields are supplied):
case class SubTeam(name: String)

case object TeamManager {

  // persistence related stuff:
  val mongoClient = MongoClient("localhost", 27017)
  val db = mongoClient("numberfour")
  val teamCollection = db("team")
//  def nextId(collection: String): Int = {
//    val counterCollection = db("counters")
//    val counterSelector = MongoDBObject("_id" -> collection)
//    val counter = counterCollection.findOne(counterSelector)
//  }

  def create(name: String): Team = {
    val selector = MongoDBObject("name" -> name)
    val readTeam = teamCollection.findOne(selector)
    val team: Team = if (readTeam.isDefined) {
      val t = readTeam.get
      Team(t.getAs[Int]("id").get, t.getAs[String]("name").get, t.getAs[Int]("members").get)
    } else {
      // TODO find a better way to generate ids
      val nextId = teamCollection.count().toInt + 1
      val insertable = MongoDBObject("_id" -> "getNextSequence(\"team\")", "name" -> name, "members" -> 0)
      teamCollection.insert(insertable)
      // TODO validate correct db write

      Team(nextId, name, 0)
    }
    team
  }

  def findById(id: Int): Option[Team] = {
    val selector = MongoDBObject("id" -> id)
    val readTeam = teamCollection.findOne(selector)
    if (readTeam.isEmpty) None
    else {
      val t = readTeam.get
      Some(Team(t.getAs[Int]("id").get, t.getAs[String]("name").get, t.getAs[Int]("members").get))
    }
  }
}
