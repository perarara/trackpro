package org.trackpro.persistence

/**
  * Created by Petar on 7/31/2016.
  */

import slick.driver.MySQLDriver.api._;
import org.trackpro.model.User


class Users (tag: Tag) extends Table[User](tag, "user")  {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc, O.Length(11))
  def firstName=column[String]("first_name", O.Length(32))
  def lastName=column[String]("last_name", O.Length(32))
  def email=column[String]("email", O.Length(32))
  def password=column[String]("password", O.Length(32))

  def * =(id,firstName,lastName,email,password) <> (User.tupled, User.unapply)
}

object Users{

  val users=TableQuery[Users]
}