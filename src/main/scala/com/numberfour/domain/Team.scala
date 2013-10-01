package com.numberfour.domain

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.MongoClient
import com.mongodb.casbah.commons.MongoDBObject

case class Team(id: Int, name: String, members: Int)

// only needed when receiving a request with a json-represented team (not all fields are supplied):
case class SubTeam(name: String)

case object TeamManager {

  def getMongoCollection() = {
    val mongoClient = MongoClient("localhost", 27017)
    val db = mongoClient("numberfour")
    db("team")
  }

  val coll = getMongoCollection

  def create(name: String): Team = {
    val selector = MongoDBObject("name" -> name)
    val readTeam = coll.findOne(selector)
    val team: Team = if (readTeam.isDefined) {
      val t = readTeam.get
      Team(t.getAs[Int]("id").get, t.getAs[String]("name").get, t.getAs[Int]("members").get)
    } else {
      // TODO find a better way to generate ids
      val nextId = coll.count().toInt + 1
      val insertable = MongoDBObject("id" -> nextId, "name" -> name, "members" -> 0)
      coll.insert(insertable)
      // TODO validate correct db write
      Team(nextId, name, 0)
    }
    team
  }

  def findById(id: Int): Option[Team] = {
    val selector = MongoDBObject("id" -> id)
    val readTeam = coll.findOne(selector)
    if (readTeam.isEmpty) None
    else {
      val t = readTeam.get
      Some(Team(t.getAs[Int]("id").get, t.getAs[String]("name").get, t.getAs[Int]("members").get))
    }
  }
}
