package org.trackpro.services

/**
  * Created by Petar on 8/2/2016.
  */

import org.trackpro.persistence.DataType
import org.trackpro.persistence.DataType.DataType
import org.trackpro.persistence.Maps._
import org.trackpro.persistence.ProjectsData._
import org.trackpro.persistence.ProjectsFinances._
import slick.driver.MySQLDriver.api._

import scala.concurrent.{Await, Future}
import scala.language.postfixOps

class PersistenceServices {

  import scala.concurrent.ExecutionContext.Implicits.global
  import org.trackpro.model._
  import org.trackpro.persistence.Users._
  import org.trackpro.persistence.Projects._
  import org.trackpro.persistence.ProjectsData._
  import org.trackpro.persistence.ProjectsFinances._
  import org.trackpro.persistence.Maps._
  import scala.concurrent.duration._
  lazy val db = Database.forConfig("db")
  /**
    * Deo vezan za CRUD operacije User
    *
    */


  //kreiramo usera
  def persistUser(user: User) = db.run(users += user) map { _ => user }

  //na osnovu email-a vrsimo autorizaciju posto nema polj username -->nebitno
  def userByEmail(email: String) = {
    val query = users.filter { _.email === email }
    db.run(query.result.headOption)

  }
  def userById(id: Long) = {
    val query = users.filter { _.id === id }
    db.run(query.result.headOption)

  }


  def canManage(userID: Long,projectId: Long)={

     val query= projects join users on (_.userId===_.id)
       val find =query.filter{case (project,user) => (project.id===projectId && user.id===userID )}
        val finded=for {(project,user) <- find} yield(project,user)
      db.run(finded.result.headOption)
      //def exec[T](action: DBIO[T]): T = Await.result(db.run(action), 2 seconds)

    //exec(find.result)
  }


  /**
    * deo za CRUD operacije nad Project
    *
    */


  //brisemo projekat
  def deleteProjectById(id: Long) = db.run( projects.filter { _.id === id } delete) map { _ > 0 }


  //modifikujemo projekat
  def updateProjectById(id: Long,title:String) ={
    val query =for{project <- projects.filter(_.id===id)} yield (project.title)
    db.run(query.update(title)) map { _ > 0 }
  }


  //kreiramo rpojekat
  def persistProject(project: Project) = db.run(projects += project) map { _ => project }


  def findProjectById(id:Long)={
    val query = for {(project) <- projects.filter { _.id === id } } yield (project)
    db.run(query.result.headOption)
  }


  /**
    * deo za CRUD operacije nad ProjectData
    */

  //Dohvati sve beleske za zadati projekat
  def findPDbyProjectId(id: Long) = {
    val query = for {(project, projectData) <- projects.filter(_.id===id) join projectsData on (_.id === _.projectId)} yield (project,projectData)
    db.run(query.result)
  }


  //Dohvati belezku odredjenog tipa za dati projekat
  def findPDbyType(id:Long,dataType:DataType.DataType)={
    val query = projects.filter(_.id===id) join projectsData on (_.id === _.projectId)
    val findData= query.filter({case (project,projectData)=>(projectData.dataType===dataType)})
    db.run(findData.result)
  }


  //Napravi Belesku za dati projekat
  def persistProjectData(projectData: ProjectData) = db.run(projectsData += projectData) map { _ => projectData }

  // Obrisi Belesku
  def deleteProjectDataById(projectId:Long,id: Long) = db.run(
    projectsData.filter {case (projectData) => (projectData.id===id && projectData.projectId===projectId )  } delete) map { _ > 0 }


  /**
    * deo za CRUD operacije nad Project Finance
    *
    */

  def persistProjectFn(projectFn:ProjectFinance)=db.run(projectsFinances += projectFn) map { _ => projectFn }


  def deleteProjectFnById(projectId:Long,id:Long)=db.run(
    projectsFinances.filter {case (projectFinance) => (projectFinance.id===id && projectFinance.projectId===projectId )  } delete) map { _ > 0 }

  def findFnbyProjectId(id:Long)={
    val query = for {(project, projectFinance) <- projects.filter(_.id===id) join projectsFinances on (_.id === _.projectId)} yield (project,projectFinance)
    db.run(query.result)
  }

  def updateProjectFn(projectId:Long,id: Long, title: String,amount:BigDecimal)={

   // val query=projectsFinances.filter(_.id===id)
    val query =for{projectFinance <- projectsFinances.filter(_.id===id)} yield (projectFinance.title,projectFinance.amount)
    db.run(query.update(title,amount)) map { _ > 0 }
  }


  def findProjectFnById(projectId:Long,id: Long)={
    val query = for {(project, projectFinance) <- projects.filter(_.id===id) join projectsFinances on (_.id === _.projectId)} yield (projectFinance)
    db.run(query.result.headOption)
  }

  /**
    * deo za CRUD operacije nad Map
    *
    */

  def persistProjectMap(map: Map)=db.run(maps += map) map { _ => map }


  def deleteMapById(projectId:Long,id:Long)=db.run(
    maps.filter {case (map) => (map.id===id && map.projectId===projectId )  } delete) map { _ > 0 }

  def findMapById(projectId:Long,id: Long)={
    val query = for {(project, map) <- projects.filter(_.id===id) join maps on (_.id === _.projectId)} yield (map)
    db.run(query.result.headOption)
  }


  def updateMap(projectId:Long,id: Long, mapTitle: String,think:String,hear:String,feel:String,see:String,pain:String,
                gain:String,avgTransAmount:BigDecimal,avgUpSaleAmount:BigDecimal,supportExpectations:String,mainConcerns:String,paygrade:String,
                advertisingLocations:String,gatheringPlaces:String)={

    // val query=projectsFinances.filter(_.id===id)
    val query =for{map <- maps.filter(_.id===id)} yield (map.mapTitle,map.think,map.feel,map.hear,map.see,map.pain,map.gain,map.avgTransAmount,map.avgUpSaleAmount,map.supportExpectations,map.mainConcerns,map.paygrade,map.advertisingLocations,map.gatheringPlaces)
    db.run(query.update(mapTitle,think,hear,feel,see,
      pain,gain,avgTransAmount,avgUpSaleAmount,
      supportExpectations,mainConcerns,
      paygrade,advertisingLocations,gatheringPlaces)) map { _ > 0 }
  }


  def findMapProjectId(id:Long)={
    val query = for {(project, map) <- projects.filter(_.id===id) join maps on (_.id === _.projectId)} yield (project,map)
    db.run(query.result)
  }

  /***
    *  Deo koji je sluzio za testiranje i proveru Rest-a
    */


  //nepotrebno
  def findAllProjects ={
    val query = for{(project,user) <- projects join users on (_.userId===_.id)} yield (project,user)
    db.run(query.result)
  }



  //Nepotrebno
  def getAllUsers() = {
    val query = for{(user,project) <- users join projects on (_.id===_.userId)} yield (user,project)
    db.run(query.result)
  }


}
