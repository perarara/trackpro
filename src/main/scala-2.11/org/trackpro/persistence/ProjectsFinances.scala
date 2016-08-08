package org.trackpro.persistence

import org.trackpro.model.ProjectFinance
import slick.driver.MySQLDriver.api._
import org.trackpro.persistence.Projects._
/**
  * Created by Petar on 8/2/2016.
  */
class ProjectsFinances (tag: Tag) extends Table[ProjectFinance](tag, "project_finance") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc, O.Length(11))
  def projectId = column[Long]("project_id")
  def project = foreignKey("project_finance_ibfk_1", projectId, projects)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)
  def title=column[String]("title", O.Length(256))
  def amount=column[BigDecimal]("amount")

  def * = (id, projectId,title,amount) <> (ProjectFinance.tupled, ProjectFinance.unapply)
}
object ProjectsFinances{

  val projectsFinances=TableQuery[ProjectsFinances]
}