package org.trackpro.persistence


import org.trackpro.model.ProjectData
import org.trackpro.persistence.DataType.DataType
import slick.driver.MySQLDriver.api._
import org.trackpro.persistence.Projects._

/**
  * Created by Petar on 7/31/2016.
  */




object DataType extends Enumeration{

  type DataType=Value
  val Overview=Value("overview")
  val Evaluation=Value("evaluation")
  val Brief=Value("brief")
  val Mvp=Value("mvp")
  val TechOverview=Value("tech_overview")
  val Gspa=Value("gspa")
  val Results=Value("results")
  val Support=Value("support")
  val Marketing=Value("marketing")
  val Resources=Value("resources")

  implicit val dataTypeMapper =
    MappedColumnType.base[DataType, String](
      e => e.toString,
      s => DataType.withName(s)
    )
}

class ProjectsData (tag: Tag) extends Table[ProjectData](tag, "project_Data")  {
  import  DataType._
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc, O.Length(11))
  def projectId = column[Long]("project_id")
  def project = foreignKey("project_data_ibfk_1", projectId, projects)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)
  def dataType=column[DataType]{"data_type"}

  def * = ( id,projectId, dataType) <> (ProjectData.tupled, ProjectData.unapply)
}


object ProjectsData{



  val projectsData=TableQuery[ProjectsData]


}