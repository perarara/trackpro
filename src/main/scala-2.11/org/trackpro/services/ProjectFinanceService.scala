package org.trackpro.services

import org.trackpro.model.{Project, ProjectFinance}

import scala.concurrent.Future

/**
  * Created by Petar on 8/7/2016.
  */
class ProjectFinanceService (val persistence: PersistenceServices) {


  import scala.concurrent.ExecutionContext.Implicits.global


  //kreiramo projekat finance
  def createProjectFn(projectFn:ProjectFinance ): Future[ProjectFinance]=persistence.persistProjectFn(projectFn)
  //brisemo finance
  def deleteById(projectId:Long,id:Long):Future[Boolean]=persistence.deleteProjectFnById(projectId,id)

  //dohvatamo project finance
  def getProjectsFn(id:Long) : Future[Seq[(Project,ProjectFinance)]]=persistence.findFnbyProjectId(id)


  def updateById(projectId:Long,id: Long, title: String,amount:BigDecimal): Future[Option[ProjectFinance]] =
    persistence.updateProjectFn(projectId,id,title,amount) flatMap {
      case true => persistence.findProjectFnById(projectId,id)
      case _ =>  Future.successful(None)
    }

}
