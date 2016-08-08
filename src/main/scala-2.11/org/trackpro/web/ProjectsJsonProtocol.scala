package org.trackpro.web


import org.trackpro.model.{Project, ProjectData, ProjectFinance, User}
import spray.json._
import org.trackpro.model.Map
import scala.beans.BeanProperty
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.trackpro.persistence.DataType
import org.trackpro.persistence.DataType.DataType



/**
  * Created by Petar on 8/3/2016.
  */

/**
  *
  * U ovom delu definisemosve potrebno za serializaciju User objekta u json i obratno
  */

@JsonIgnoreProperties(Array("bn"))
case class UserCreatePayload(@BeanProperty id:Long,firstName:String,lastName:String,email:String,password:String)


@JsonIgnoreProperties(Array("bn"))
case class UserResource(@BeanProperty id:Long, firstName: String, lastName: String,email:String,password:String)


object UserResource {

  def apply(user: User): UserResource =
    UserResource(user.id, user.firstName, user.lastName,user.email,user.password)
}

/**
  *
  * U ovom delu definisemosve potrebno za serializaciju Project objekta u json i obratno
  */


@JsonIgnoreProperties(Array("bn"))
case class ProjectResource(@BeanProperty id:Long, title: String, userId :Long )



object ProjectResource {
  def apply( user: User,project: Project): ProjectResource =
    ProjectResource(project.id,project.title,user.id)
}

@JsonIgnoreProperties(Array("bn"))
case class ProjectCreatePayload(@BeanProperty title:String)

@JsonIgnoreProperties(Array("bn"))
case class ProjectUpdatePayload(@BeanProperty id:Long,title:String,userId:Long)


/**
  * U ovom delu definisemo object -> json Za ProjectData
  */

@JsonIgnoreProperties(Array("bn"))
case class ProjectDataCreatePayload(dataType: DataType)

@JsonIgnoreProperties(Array("bn"))
case class ProjectDataResource(@BeanProperty id:Long, projectId: Long, dataType: DataType)

object ProjectDataResource {
  def apply(project:Project,projectData: ProjectData): ProjectDataResource =
    ProjectDataResource(projectData.id,project.id,projectData.dataType)
}

/**
  * U ovom delu definisemo object -> json Za ProjectFinance
  */

@JsonIgnoreProperties(Array("bn"))
case class ProjectFinanceCreatePayload(title:String,amount:BigDecimal)

@JsonIgnoreProperties(Array("bn"))
case class ProjectFnResource(@BeanProperty id:Long, projectId: Long, title: String,amount:BigDecimal)

object ProjectFnResource {

  def apply(projectId:Long,projectFn: ProjectFinance): ProjectFnResource =
    ProjectFnResource(projectFn.id,projectId,projectFn.title,projectFn.amount)

  def apply(projectId:Long,projectFn: Option[ProjectFinance]): ProjectFnResource =
    ProjectFnResource(projectFn.get.id,projectId,projectFn.get.title,projectFn.get.amount)
}


/**
  * U ovom delu definisemo object -> json Za Map
  */


@JsonIgnoreProperties(Array("bn"))
case class MapCreatePayload(mapTitle: String,think:String,hear:String,feel:String,see:String,pain:String,
                            gain:String,avgTransAmount:BigDecimal,avgUpSaleAmount:BigDecimal,supportExpectations:String,mainConcerns:String,
                            paygrade:String, advertisingLocations:String,gatheringPlaces:String)

@JsonIgnoreProperties(Array("bn"))
case class MapResource(@BeanProperty id:Long, projectId: Long, mapTitle: String,think:String,hear:String,feel:String,see:String,pain:String,
                       gain:String,avgTransAmount:BigDecimal,avgUpSaleAmount:BigDecimal,supportExpectations:String,mainConcerns:String,
                       paygrade:String, advertisingLocations:String,gatheringPlaces:String)

object MapResource {

  def apply(projectId:Long,map: Map): MapResource =
    MapResource(map.id,projectId,map.mapTitle,map.think,map.feel,map.hear,map.see,map.pain,
      map.gain,map.avgTransAmount,map.avgUpSaleAmount,map.supportExpectations,
      map.mainConcerns,map.paygrade,map.advertisingLocations,map.gatheringPlaces)

 def apply(projectId:Long,map: Option[Map]): MapResource =
    MapResource(map.get.id,projectId,map.get.mapTitle,map.get.think,map.get.feel,map.get.hear,map.get.see,map.get.pain,
      map.get.gain,map.get.avgTransAmount,map.get.avgUpSaleAmount,map.get.supportExpectations,
      map.get.mainConcerns,map.get.paygrade,map.get.advertisingLocations,map.get.gatheringPlaces)
}
object ProjectsJsonProtocol extends DefaultJsonProtocol {

 //import  org.trackpro.persistence.DataType

 def jsonEnum[T <: Enumeration](enu: T) = new JsonFormat[T#Value] {
    def write(obj: T#Value) = JsString(obj.toString)

    def read(json: JsValue) = json match {
      case JsString(txt) => enu.withName(txt)
      case something => throw new DeserializationException(s"Expected a value from enum $enu instead of $something")
    }
  }


  implicit def userFormat=jsonFormat5(User.apply)
  implicit def userResourceFormat = jsonFormat5(UserResource.apply)
  implicit def userCreatePayloadFormat=jsonFormat5(UserCreatePayload)
  implicit def projectResourceFormat = jsonFormat3(ProjectResource.apply)
  implicit def projectCreatePayloadFormat=jsonFormat1(ProjectCreatePayload)
  implicit def projectUpdatePayloadFormat=jsonFormat3(ProjectUpdatePayload)
  implicit def projectDataCreatePayloadFormat=jsonFormat1(ProjectDataCreatePayload)
  implicit def projectDataResourceFormat=jsonFormat3(ProjectDataResource.apply)
  implicit def projectFnCreatePayloadFormat=jsonFormat2(ProjectFinanceCreatePayload)
  implicit def projectFnResourceFormat=jsonFormat4(ProjectFnResource.apply)
  implicit def mapResourceFormat=jsonFormat16(MapResource.apply)
  implicit def mapCreatePayloadFormat=jsonFormat14(MapCreatePayload)
  implicit val dataTypeFormat=jsonEnum(DataType)
}


