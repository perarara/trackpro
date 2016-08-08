package org.trackpro

/**
  * Created by Petar on 7/31/2016.
  */

import scala.concurrent.Future

import org.trackpro.services.{PersistenceServices, UserService}
import org.trackpro.web.{ProjectRestService}
import akka.actor.{ActorSystem, Props}
import akka.event.Logging
import akka.io.IO
import slick.driver.MySQLDriver.api._
import org.trackpro.persistence.ProjectsData._
import org.trackpro.persistence.ProjectsFinances._
import spray.can.Http

import scala.concurrent.duration._
import scala.concurrent.Await

object BootApp extends App {
  import scala.concurrent.ExecutionContext.Implicits.global

  implicit val system = ActorSystem("project-intro")
  //val log = Logging(system, getClass)
  val persistence = new PersistenceServices
  val listener = system.actorOf(Props(new ProjectRestService(persistence)), name = "track-rest-service")
  IO(Http) ! Http.Bind(listener, interface = "localhost", port = 9001)


}
