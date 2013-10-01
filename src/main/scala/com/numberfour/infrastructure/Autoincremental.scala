package com.numberfour.infrastructure

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.MongoClient
import com.mongodb.casbah.commons.MongoDBObject

trait Autoincremental {

  val mongoClient = MongoClient("localhost", 27017)
  val db = mongoClient("numberfour")

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