package org.trackpro.services

import org.trackpro.model.{Project, User}

import scala.concurrent.Future

/**
  * Created by Petar on 8/3/2016.
  */
class ProjectService (val persistence: PersistenceServices) {
  import scala.concurrent.ExecutionContext.Implicits.global

  //kreiramo projekat
  def createProject(project: Project): Future[Project]=persistence.persistProject(project)
  //dohvati project
  def getProject(id:Long): Future[Option[Project]] =persistence.findProjectById(id)

  //modifikujemo projekat
  def updateById(id: Long, title: String): Future[Option[Project]] =
    persistence.updateProjectById(id,title) flatMap {
      case true => persistence.findProjectById(id)
      case _ =>  Future.successful(None)
    }

  def deleteById(id:Long):Future[Boolean]=persistence.deleteProjectById(id)

  // ne koristi se
  def findAll(): Future[Seq[(Project, User)]] = persistence.findAllProjects
}

