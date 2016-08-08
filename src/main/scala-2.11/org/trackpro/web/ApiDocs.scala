package org.trackpro.web

import com.wordnik.swagger.model.ApiInfo
import com.gettyimages.spray.swagger.SwaggerHttpService

import scala.reflect.runtime.universe.typeOf
import akka.actor.Actor

import spray.http.HttpRequest


trait ApiDocs {
  this: Actor =>

  val apiDocsRoutes = new SwaggerHttpService {
    override def apiTypes = Seq(typeOf[MapServiceRoutes],
      typeOf[ProjectDataRestServicesRoutes],
      typeOf[ProjectRestServiceRoutes],
      typeOf[ProjectFinanceServiceRoutes],
      typeOf[UserRestServicesRoutes],
      typeOf[ProjectRestApiRoutes])
    override def apiVersion = "1.0"
    override def baseUrl = "/"
    override def docsPath = "api-docs"


    override def actorRefFactory = context

    override def apiInfo = Some(
      new ApiInfo(
        "Project Track",
        "Project Track REST(ful) web API",
        "",
        "",
        "",
        ""
      )
    )
  }.routes
}