package com.numberfour.domain

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.MongoClient
import com.mongodb.casbah.commons.MongoDBObject
import org.eclipse.egit.github.core._
import org.eclipse.egit.github.core.client._
import org.eclipse.egit.github.core.service._
import com.numberfour.infrastructure.Autoincremental
import com.numberfour.infrastructure.Github
import scala.collection.JavaConversions._
import com.numberfour.Configurable

case class Project(id: Int, name: String, description: String, teamId: Int,
  githubUrl: String, gitUrl: String, githubWatchers: Int, githubForks: Int)

// only needed when receiving a request with a json-represented project (not all fields are supplied):
case class SubProject(name: String, description: String, teamId: Int)

case object ProjectManager extends Autoincremental with Github with Configurable {

  val projectCollection = db("project")

  def create(name: String, description: String, teamId: Int): Project = {
    val selector = MongoDBObject("name" -> name, "description" -> description, "teamId" -> teamId)
    val readProject = projectCollection.findOne(selector)
    val project: Project = if (readProject.isDefined) {
      val p = readProject.get
      findById(p.getAs[Int]("_id").get).get
    } else {
      val nextId = calcNextId("project")

      // perform the github magic here
      val repository = new Repository()
      repository.setName(name)
      val validRepo = repoService.createRepository(repository)
      val gitUrl = validRepo.getGitUrl()
      val gitHtmlUrl = validRepo.getHtmlUrl()
      val gitWatchers = validRepo.getWatchers()
      val gitForks = validRepo.getForks()

      val insertable = MongoDBObject("_id" -> nextId, "name" -> name, "description" -> description, "teamId" -> teamId)
      projectCollection.insert(insertable)
      // TODO validate correct db write

      Project(nextId, name, description, teamId, gitHtmlUrl, gitUrl, gitWatchers, gitForks)
    }
    project
  }

  def findById(id: Int): Option[Project] = {
    val selector = MongoDBObject("_id" -> id)
    val readProject = projectCollection.findOne(selector)
    if (readProject.isEmpty) None
    else {
      val t = readProject.get
      val name = t.getAs[String]("name").get
      val description = t.getAs[String]("description").get
      val teamId = t.getAs[Int]("teamId").get

      val githubUser = config.getString("numberfour.github.user")
      val userRepos = repoService.getRepositories(githubUser).toList
      val validRepo = userRepos.filter(r => r.getName() == name.replaceAll(" ", "-"))(0) // TODO danger here, check existence before

      val gitUrl = validRepo.getGitUrl()
      val gitHtmlUrl = validRepo.getHtmlUrl()
      val gitWatchers = validRepo.getWatchers()
      val gitForks = validRepo.getForks()

      Some(Project(id, name, description, teamId, gitHtmlUrl, gitUrl, gitWatchers, gitForks))
    }
  }
}
