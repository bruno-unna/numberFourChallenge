package com.numberfour.domain

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.MongoClient
import com.mongodb.casbah.commons.MongoDBObject

case class SubTeam(name: String)

case class Team(id: Long, name: String, members: Long) {
  def create(): Team = {
    val mongoClient = MongoClient("localhost", 27017)
    val db = mongoClient("numberfour")
    val coll = db("team")
    val mongoTeam = MongoDBObject("name" -> this.name)
    val readTeam = coll.findOne(mongoTeam)
    if (readTeam.isEmpty) {
      val nextId = coll.count() + 1
      mongoTeam.update("id", String.valueOf(nextId))
      coll.insert(mongoTeam)
    }
    // TODO validate correct db write
    val retMongo = coll.findOne(mongoTeam).get // should be one
    val idString: String = retMongo.get("id").toString()
    Team(idString.toLong, retMongo.get("name").toString, 0)
  }

}
