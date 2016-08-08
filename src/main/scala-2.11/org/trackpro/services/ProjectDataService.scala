package org.trackpro.services

import org.trackpro.model.{Project, ProjectData}

import scala.concurrent.Future

/**
  * Created by Petar on 8/6/2016.
  */
class ProjectDataService (val persistence: PersistenceServices) {

  import scala.concurrent.ExecutionContext.Implicits.global

  //kreiramo projekatData
  def createProjectData(projectData: ProjectData): Future[ProjectData]=persistence.persistProjectData(projectData)
  //brisemo projekat
  def deleteById(projectId:Long,id:Long):Future[Boolean]=persistence.deleteProjectDataById(projectId,id)

  //dohvatamo project data
  def getProjectsData(id:Long) : Future[Seq[(Project,ProjectData)]]=persistence.findPDbyProjectId(id)

}
