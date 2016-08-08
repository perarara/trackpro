package org.trackpro.persistence
import org.trackpro.model.Map
import slick.driver.MySQLDriver.api._
import org.trackpro.persistence.Projects._
/**
  * Created by Petar on 8/2/2016.
  */
class Maps (tag: Tag) extends Table[Map](tag, "map") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc, O.Length(11))
  def projectId = column[Long]("project_id",O.Length(10))
  def project = foreignKey("map_ibfk_1", projectId, projects)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)
  def mapTitle=column[String]("map_title", O.Length(255))
  def think=column[String]("think", O.Length(1024))
  def feel=column[String]("feel", O.Length(1024))
  def hear=column[String]("hear", O.Length(1024))
  def see=column[String]("see", O.Length(1024))
  def pain=column[String]("pain", O.Length(1024))
  def gain=column[String]("gain", O.Length(1024))
  def avgTransAmount=column[BigDecimal]("avg_trans_amount")
  def avgUpSaleAmount=column[BigDecimal]("avg_upsale_amount")
  def supportExpectations=column[String]("support_expectations", O.Length(1024))
  def mainConcerns=column[String]("main_concerns", O.Length(1024))
  def paygrade=column[String]("paygrade", O.Length(1024))
  def advertisingLocations=column[String]("advertising_locations", O.Length(1024))
  def gatheringPlaces=column[String]("gathering_places", O.Length(1024))

  def * = (id, projectId,mapTitle,think,feel,hear,see,pain,gain,
    avgTransAmount,avgUpSaleAmount,supportExpectations,
    mainConcerns,paygrade,advertisingLocations,gatheringPlaces) <> (Map.tupled, Map.unapply)
}


object Maps{
  val maps=TableQuery[Maps]
}