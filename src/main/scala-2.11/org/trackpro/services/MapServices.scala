package org.trackpro.services

import org.trackpro.model.Project
import org.trackpro.model.Map
import spray.routing.directives.OnCompleteFutureMagnet

import scala.concurrent.Future

/**
  * Created by Petar on 8/8/2016.
  */
class MapServices (val persistence: PersistenceServices){



  import scala.concurrent.ExecutionContext.Implicits.global


  //kreiramo projekat map
  def createMap(map:Map ): Future[Map]=persistence.persistProjectMap(map)
  //brisemo map
  def deleteById(projectId:Long,id:Long):Future[Boolean]=persistence.deleteProjectFnById(projectId,id)

  //dohvatamo project map
  def getMaps(id:Long) : Future[Seq[(Project,Map)]]=persistence.findMapProjectId(id)


  def updateById(projectId:Long,id: Long, mapTitle: String,think:String,hear:String,feel:String,see:String,pain:String,
                 gain:String,avgTransAmount:BigDecimal,avgUpSaleAmount:BigDecimal,supportExpectations:String,mainConcerns:String,paygrade:String, advertisingLocations:String,gatheringPlaces:String): Future[Option[Map]] = {
      persistence.updateMap(projectId,id,mapTitle, think, hear, feel, see,
      pain, gain, avgTransAmount, avgUpSaleAmount,
      supportExpectations, mainConcerns,
      paygrade, advertisingLocations, gatheringPlaces) flatMap {
      case true => persistence.findMapById(projectId, id)
      case _ => Future.successful(None)
    }
  }

}
