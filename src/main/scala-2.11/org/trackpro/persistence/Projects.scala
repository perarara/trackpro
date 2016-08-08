package org.trackpro.persistence

import org.trackpro.model.Project
import slick.driver.MySQLDriver.api._
import org.trackpro.persistence.Users._

/**
  * Created by Petar on 7/31/2016.
  */
class Projects (tag: Tag) extends Table[Project](tag , "project") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc, O.Length(11))
  def title=column[String]("title", O.Length(255))
  def userId = column[Long]("user_id")
  def user = foreignKey("project_ibfk_1", userId, users)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)

  def * = (id, title, userId) <> (Project.tupled, Project.unapply)
}


object Projects{

  val projects=TableQuery[Projects]
}