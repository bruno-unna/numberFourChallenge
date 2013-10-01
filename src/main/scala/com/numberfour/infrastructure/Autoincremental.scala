package com.numberfour.infrastructure

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.MongoClient
import com.mongodb.casbah.commons.MongoDBObject
import com.typesafe.config.ConfigFactory
import com.numberfour.Configurable

trait Autoincremental extends Configurable {

  val mongoHost = config.getString("numberfour.mongodb.host")
  val mongoPort = config.getInt("numberfour.mongodb.port")
  val mongoDB = config.getString("numberfour.mongodb.db-name")

  val mongoClient = MongoClient(mongoHost, mongoPort)
  val db = mongoClient(mongoDB)

  def calcNextId(collection: String): Int = {
    val counterCollection = db("counters")
    val counterSelector = MongoDBObject("_id" -> collection)
    val counterUpdate = $inc("seq" -> 1)
    val counter = counterCollection.findAndModify(counterSelector, counterUpdate).getOrElse {
      val counterCreationObject = MongoDBObject("_id" -> collection, "seq" -> 0)
      counterCollection.insert(counterCreationObject)
      counterCreationObject
    }
    counter.getAs[Int]("seq").get
  }

}