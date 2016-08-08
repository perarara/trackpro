package org.trackpro.web

import akka.actor.ActorLogging
import org.trackpro.services._
import spray.http.StatusCodes._
import spray.http.{EntityTag, Uri}

import scala.util.Success
import scala.util.Failure
import spray.routing.Directives._
import spray.routing._
import javax.ws.rs.Path

import org.trackpro.model.{Project, ProjectData, ProjectFinance, User, Map}
import org.trackpro.web
import spray.http.HttpHeaders.Location
import spray.routing.authentication.BasicAuth

import scala.concurrent.{Await, Future}

/**
  * Created by Petar on 8/3/2016.
  */
class ProjectRestService(val persistence: PersistenceServices)
  extends HttpServiceActor
    with ProjectRestApiRoutes
    with UserRestServicesRoutes
    with ActorLogging {


  val userService = new UserService(persistence)
  val projectService = new ProjectService(persistence)
  val projectDataService = new ProjectDataService(persistence)
  val projectFnService = new ProjectFinanceService(persistence)
  val mapService = new MapServices(persistence)

  def receive = runRoute {
    routes
  }
}


trait ProjectRestApiRoutes extends ProjectRestServiceRoutes with UserRestServicesRoutes with ProjectDataRestServicesRoutes with ProjectFinanceServiceRoutes with MapServiceRoutes {
  this: HttpService =>

  val routes = pathPrefix("trackpro") {
    userRoutes ~ projectRoutes ~ projectDataRoutes ~ projectFnRoutes ~ mapRoutes
  }

}


trait UserRestServicesRoutes {

  import spray.httpx.SprayJsonSupport._
  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.concurrent.duration._
  import scala.concurrent.Await
  import spray.routing.directives._

  val userService: UserService

  import org.trackpro.web.ProjectsJsonProtocol._

  def root(path: Uri.Path): Uri.Path = Uri.Path(path.toString.split("/").take(3).mkString("/"))


  @Path("/{id}")
  def getUser(id: Long) = get {

    authenticate(BasicAuth(userService.authenticate _, realm = "Project List")) { user => {

      val exsist = Await.result(userService.canManageProjects(user.id, id), 10 seconds).isEmpty
      authorize(!exsist) {
        complete {
          userService.getUser(id)
        }
      }
    }
    }
  }

  def createUser = post {
    requestUri { uri =>
      entity(as[UserCreatePayload]) {
        payload =>
          onComplete(userService.createUser(User(payload.id, payload.firstName, payload.lastName, payload.password, payload.email))) {
            case Success(user) =>
              respondWithHeader(Location(uri.withPath(uri.path / user.id.toString))) {
                complete(Created, user)
              }
            case Failure(ex) => complete(Conflict)
          }
      }
    }
  }

  val userRoutes = pathPrefix("user") {

    pathEnd {
      createUser
    } ~
      pathPrefix(LongNumber) {
        // id => getUser(id)
        id =>
          pathEnd {
            getUser(id)
          }

      }
  }

}

trait ProjectRestServiceRoutes {

  import org.trackpro.web.ProjectsJsonProtocol._

  val projectService: ProjectService
  val userService: UserService

  import spray.httpx.SprayJsonSupport._
  import scala.concurrent.ExecutionContext.Implicits.global
  import spray.httpx.marshalling._
  import spray.routing.directives._
  import scala.concurrent.duration._


  //Na osnovu ove metode kreiramo projekat
  def createProject(id: Long) = post {
    authenticate(BasicAuth(userService.authenticate _, realm = "Project Catalog")) { user =>
      val exsist = Await.result(userService.getUser(user.id), 10 seconds).isEmpty
      authorize(!exsist) {
        requestUri { uri =>
          entity(as[ProjectCreatePayload]) {
            payload =>
              onComplete(projectService.createProject(Project(id, payload.title, user.id))) {
                case Success(project) =>
                  respondWithHeader(Location(uri.withPath(uri.path / project.id.toString))) {
                    complete(Created, ProjectResource(project.id, project.title, project.userId))
                  }
                case Failure(ex) => complete(Conflict)
              }
          }
        }
      }
    }
  }


  //Na osnovu ove metode brisemo projekat

  def deleteProject(id: Long) = delete {
    authenticate(BasicAuth(userService.authenticate _, realm = "Project List")) { user => {

      val exsist = Await.result(userService.canManageProjects(user.id, id), 10 seconds).isEmpty
      authorize(!exsist) {
        complete {
          projectService.deleteById(id) map {
            case true => NoContent
            case false => NotFound
          }
        }
      }
    }
    }

  }


  def updateProject(id: Long) = put {
    authenticate(BasicAuth(userService.authenticate _, realm = "Project Catalog")) { user =>
      val exsist = Await.result(userService.canManageProjects(user.id, id), 10 seconds).isEmpty
      authorize(!exsist) {
        requestUri { uri =>
          entity(as[ProjectUpdatePayload]) {
            payload =>
              onComplete(projectService.createProject(Project(id, payload.title, payload.userId))) {
                case Success(project) =>
                  respondWithHeader(Location(uri.withPath(uri.path / project.id.toString))) {
                    complete(Created, ProjectResource(id, project.title, project.userId))
                  }
                case Failure(ex) => complete(Conflict)
              }
          }
        }
      }
    }
  }

  def getProject(id: Long) = get {
    onSuccess(projectService getProject (id)) {
      case Some((project)) =>

        complete(ProjectResource(project.id, project.title, project.userId))

      case None => complete(NotFound)
    }
  }


  val projectRoutes = pathPrefix("project") {

    pathPrefix(LongNumber) {
      // id => getUser(id)
      id =>
        pathEnd {
          createProject(id) ~ deleteProject(id) ~ updateProject(id) ~ getProject(id)
        }

    }
  }


}


trait ProjectDataRestServicesRoutes {

  import org.trackpro.web.ProjectsJsonProtocol._

  val projectService: ProjectService
  val userService: UserService
  val projectDataService: ProjectDataService

  import spray.httpx.SprayJsonSupport._
  import scala.concurrent.ExecutionContext.Implicits.global
  import spray.httpx.marshalling._
  import spray.routing.directives._
  import scala.concurrent.duration._


  def createProjectData(projectId: Long, id: Long) = post {
    authenticate(BasicAuth(userService.authenticate _, realm = "Project Data Catalog")) { user =>
      val exsist = Await.result(userService.getUser(user.id), 10 seconds).isEmpty
      authorize(!exsist) {
        requestUri { uri =>
          entity(as[ProjectDataCreatePayload]) {
            payload =>
              onComplete(projectDataService.createProjectData(ProjectData(projectId, id, payload.dataType))) {
                case Success(projectData) =>
                  respondWithHeader(Location(uri.withPath(uri.path / projectData.id.toString))) {
                    complete(Created, ProjectDataResource(projectId, id, projectData.dataType))
                  }
                case Failure(ex) => complete(Conflict)
              }
          }
        }
      }
    }
  }

  def deleteProjectData(projectId: Long, id: Long) = delete {
    authenticate(BasicAuth(userService.authenticate _, realm = "Project List")) { user => {

      val exsist = Await.result(userService.canManageProjects(user.id, id), 10 seconds).isEmpty
      authorize(!exsist) {
        complete {
          projectDataService.deleteById(projectId, id) map {
            case true => NoContent
            case false => NotFound
          }
        }
      }
    }
    }

  }

  def getProjectsData(projectId: Long) = get {

    complete {
      projectDataService getProjectsData (projectId) map {
        _.map {
          case (project, projectData) => ProjectDataResource(project, projectData)
        }
      }
    }


  }


  val projectDataRoutes = pathPrefix("project") {

    pathPrefix(LongNumber) { projectId =>
      pathPrefix("projectdata") {

        getProjectsData(projectId)

        pathPrefix(LongNumber) {
          // id => getUser(id)
          id =>
            pathEnd {
              createProjectData(projectId, id) ~ deleteProjectData(projectId, id)
            }

        }
      }
    }


  }
}


trait ProjectFinanceServiceRoutes {

  import org.trackpro.web.ProjectsJsonProtocol._

  val projectService: ProjectService
  val userService: UserService
  val projectFnService: ProjectFinanceService

  import spray.httpx.SprayJsonSupport._
  import scala.concurrent.ExecutionContext.Implicits.global
  import spray.httpx.marshalling._
  import spray.routing.directives._
  import scala.concurrent.duration._


  def createProjectFn(projectId: Long, id: Long) = post {
    authenticate(BasicAuth(userService.authenticate _, realm = "Project Catalog")) { user =>
      val exsist = Await.result(userService.canManageProjects(user.id, id), 10 seconds).isEmpty
      authorize(!exsist) {
        requestUri { uri =>
          entity(as[ProjectFinanceCreatePayload]) {
            payload =>
              onComplete(projectFnService.createProjectFn(ProjectFinance(id, projectId, payload.title, payload.amount))) {
                case Success(projectFinance) =>
                  respondWithHeader(Location(uri.withPath(uri.path / projectFinance.id.toString))) {
                    complete(Created, ProjectFnResource(projectId, id, projectFinance.title, projectFinance.amount))
                  }
                case Failure(ex) => complete(Conflict)
              }
          }
        }
      }
    }
  }

  def deleteProjectFn(projectId: Long, id: Long) = delete {
    authenticate(BasicAuth(userService.authenticate _, realm = "Project List")) { user => {

      val exsist = Await.result(userService.canManageProjects(user.id, id), 10 seconds).isEmpty
      authorize(!exsist) {
        complete {
          projectFnService.deleteById(projectId, id) map {
            case true => NoContent
            case false => NotFound
          }
        }
      }
    }
    }

  }

  def getProjectsFnData(projectId: Long) = get {

    complete {
      projectFnService getProjectsFn (projectId) map {
        _.map {
          case (project, projectFinance) => ProjectFnResource(projectId, projectFinance)
        }
      }
    }


  }


  def updateProjectFn(projectId: Long, id: Long) = put {
    authenticate(BasicAuth(userService.authenticate _, realm = "Project Catalog")) { user =>
      val exsist = Await.result(userService.canManageProjects(user.id, id), 10 seconds).isEmpty
      authorize(!exsist) {
        requestUri { uri =>
          entity(as[ProjectFinanceCreatePayload]) {
            payload =>
              onComplete(projectFnService.updateById(projectId, id, payload.title, payload.amount)) {
                case Success(projectFinance) =>
                  respondWithHeader(Location(uri.withPath(uri.path / id.toString))) {
                    complete(Created, ProjectFnResource(projectId, projectFinance))
                  }
                case Failure(ex) => complete(Conflict)
              }
          }
        }
      }
    }
  }


  val projectFnRoutes = pathPrefix("project") {

    pathPrefix(LongNumber) { projectId =>
      pathPrefix("projectfn") {

        getProjectsFnData(projectId)

        pathPrefix(LongNumber) {
          // id => getUser(id)
          id =>
            pathEnd {
              createProjectFn(projectId, id) ~ deleteProjectFn(projectId, id) ~ updateProjectFn(projectId, id)
            }

        }
      }
    }


  }
}

trait MapServiceRoutes {

  import org.trackpro.web.ProjectsJsonProtocol._

  val projectService: ProjectService
  val userService: UserService
  val mapService: MapServices

  import spray.httpx.SprayJsonSupport._
  import scala.concurrent.ExecutionContext.Implicits.global
  import spray.httpx.marshalling._
  import spray.routing.directives._
  import scala.concurrent.duration._
  import org.trackpro.model.Map



  def createMaps(projectId: Long, id: Long) = post {
    authenticate(BasicAuth(userService.authenticate _, realm = "Project Catalog")) { user =>
      val exsist = Await.result(userService.canManageProjects(user.id, id), 10 seconds).isEmpty
      authorize(!exsist) {
        requestUri { uri =>
          entity(as[MapCreatePayload]) {
            payload =>
              onComplete(mapService.createMap(Map(id, projectId, payload.mapTitle, payload.think, payload.feel, payload.hear, payload.see, payload.pain,
                payload.gain, payload.avgTransAmount, payload.avgUpSaleAmount, payload.supportExpectations,
                payload.mainConcerns, payload.paygrade, payload.advertisingLocations, payload.gatheringPlaces))) {
                case Success(map) =>
                  respondWithHeader(Location(uri.withPath(uri.path / map.id.toString))) {
                    complete(Created, MapResource(projectId, map))
                  }
                case Failure(ex) => complete(Conflict)
              }
          }
        }
      }
    }
  }

  def getMaps(projectId: Long) = get {

    complete {
      mapService getMaps (projectId) map {
        _.map {
          case (project, map) => MapResource(projectId, map)
        }
      }
    }
  }


  def deleteMaps(projectId: Long, id: Long) = delete {
    authenticate(BasicAuth(userService.authenticate _, realm = "Project List")) { user => {

      val exsist = Await.result(userService.canManageProjects(user.id, id), 10 seconds).isEmpty
      authorize(!exsist) {
        complete {
          mapService.deleteById(projectId, id) map {
            case true => NoContent
            case false => NotFound
          }
        }
      }
    }
    }
  }

  def updateMaps(projectId: Long, id: Long) = put {
    authenticate(BasicAuth(userService.authenticate _, realm = "Project Catalog")) { user =>
      val exsist = Await.result(userService.canManageProjects(user.id, id), 10 seconds).isEmpty
      authorize(!exsist) {
        requestUri { uri =>
          entity(as[MapCreatePayload]) {
            payload =>
              onComplete(mapService.updateById(projectId, id, payload.mapTitle, payload.think, payload.feel, payload.hear, payload.see, payload.pain,
                payload.gain, payload.avgTransAmount, payload.avgUpSaleAmount, payload.supportExpectations,
                payload.mainConcerns, payload.paygrade, payload.advertisingLocations, payload.gatheringPlaces)) {
                case Success(map) =>
                  respondWithHeader(Location(uri.withPath(uri.path / id.toString))) {
                    complete(Created, MapResource(projectId, map))
                  }
                case Failure(ex) => complete(Conflict)
              }
          }
        }
      }
    }
  }

  val mapRoutes = pathPrefix("project") {

    pathPrefix(LongNumber) { projectId =>
      pathPrefix("map") {

        getMaps(projectId)

        pathPrefix(LongNumber) {
          // id => getUser(id)
          id =>
            pathEnd {
              createMaps(projectId, id) ~ deleteMaps(projectId, id) ~ updateMaps(projectId, id)
            }

        }
      }
    }


  }

}

