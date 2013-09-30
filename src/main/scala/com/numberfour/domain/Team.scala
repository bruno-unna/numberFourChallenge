package com.numberfour.domain

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.MongoClient
import com.mongodb.casbah.commons.MongoDBObject

case class SubTeam(name: String)

case class Team(id: Long, name: String, members: Int) {

  // TODO optimize this ugly code
  def getMongoCollection() = {
    val mongoClient = MongoClient("localhost", 27017)
    val db = mongoClient("numberfour")
    db("team")
  }

  def create(): Team = {
    val coll = getMongoCollection
    val mongoTeam = MongoDBObject("name" -> this.name)
    val readTeam = coll.findOne(mongoTeam)
    if (readTeam.isEmpty) {
      val nextId = coll.count() + 1
      val update = $set("id" -> nextId, "name" -> this.name, "members" -> 0)
      coll.insert(mongoTeam)
      coll.update(mongoTeam, update)
    }
    // TODO validate correct db write
    val retMongo = coll.findOne(mongoTeam).get // should be one

    Team(retMongo.get("id").asInstanceOf[Long], retMongo.get("name").toString, 0)
  }

  def findById(id: Long): Option[Team] = {
    val coll = getMongoCollection()
    val mongoTeam = MongoDBObject("id" -> id)
    val readTeam = coll.findOne(mongoTeam)
    if (readTeam.isEmpty) None
    else {
      val retMongo = readTeam.get
      val id = retMongo.get("id").asInstanceOf[Long]
      val name = retMongo.get("name").toString()
      val members = retMongo.get("members").asInstanceOf[Int]
      Some(Team(id, name, members))
    }
  }
}
