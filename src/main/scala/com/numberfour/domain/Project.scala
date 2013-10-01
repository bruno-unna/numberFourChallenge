package com.numberfour.domain

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.MongoClient
import com.mongodb.casbah.commons.MongoDBObject
import org.eclipse.egit.github.core._
import org.eclipse.egit.github.core.client._
import org.eclipse.egit.github.core.service._

case class Project(id: Int, name: String, description: String, teamId: Int,
  githubUrl: String, gitUrl: String, githubWatchers: Int, githubForks: Int)

// only needed when receiving a request with a json-represented project (not all fields are supplied):
case class SubProject(id: Int, name: String, description: String, teamId: Int)

case object ProjectManager {

  // first, the persistence related stuff:
  def getMongoCollection() = {
    val mongoClient = MongoClient("localhost", 27017)
    val db = mongoClient("numberfour")
    db("project")
  }
  val coll = getMongoCollection

  // now the github related stuff:
  val githubToken = "94a2508e594e6cc7e2451f3168d412e88ada7bf0"
  val repoService = new RepositoryService
  repoService.getClient().setOAuth2Token(githubToken)

  def create(name: String, description: String, teamId: Int): Project = {
    val selector = MongoDBObject("name" -> name, "description" -> description, "teamId" -> teamId)
    val readProject = coll.findOne(selector)
    val project: Project = if (readProject.isDefined) {
      val p = readProject.get
      Project(p.getAs[Int]("id").get, p.getAs[String]("name").get,
        p.getAs[String]("description").get, p.getAs[Int]("teamId").get,
        p.getAs[String]("githubUrl").get, p.getAs[String]("gitUrl").get,
        p.getAs[Int]("githubWatchers").get, p.getAs[Int]("githubForks").get)
    } else {
      // perform the github magic here
      val repository = new Repository()
      val repoName = name.replaceAll(" ", "-")
      repository.setName(repoName)
      val validRepo = repoService.createRepository(repository)
      val gitUrl = validRepo.getGitUrl()
      val gitHtmlUrl = validRepo.getHtmlUrl()
      val gitWatchers = validRepo.getWatchers()
      val gitForks = validRepo.getForks()

      // TODO find a better way to generate ids
      val nextId = coll.count().toInt + 1
      val insertable = MongoDBObject("id" -> nextId, "name" -> name, "members" -> 0)
      coll.insert(insertable)
      // TODO validate correct db write

      Project(nextId, name, description, teamId, gitHtmlUrl, gitUrl, gitWatchers, gitForks)
    }
    project
  }

  def findById(id: Int): Option[Project] = {
    val selector = MongoDBObject("id" -> id)
    val readProject = coll.findOne(selector)
    if (readProject.isEmpty) None
    else {
      val t = readProject.get
      Some(Project(t.getAs[Int]("id").get, t.getAs[String]("name").get, t.getAs[String]("description").get, t.getAs[Int]("teamId").get,
        t.getAs[String]("githubUrl").get, t.getAs[String]("gitUrl").get, t.getAs[Int]("gitWatchers").get, t.getAs[Int]("gitForks").get))
    }
  }
}
