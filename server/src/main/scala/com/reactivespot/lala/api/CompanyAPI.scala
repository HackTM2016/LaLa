package com.reactivespot.lala.api

import akka.actor.ActorRefFactory
import com.reactivespot.lala.domain._
import com.reactivespot.lala.service.{ClientService, CompanyService}
import spray.routing._


class CompanyAPI(
                  companyService: CompanyService,
                  clientService: ClientService
                )
                (implicit actorRefFactoryParam: ActorRefFactory)
  extends HttpService
  with CompanyJSONFormat
  with ClientJSONFormats {

  val route: Route =
    pathPrefix("company") {
      path("register") {
        post {
          entity(as[Company]) { company =>
            complete(companyService.register(company))
          }
        }
      } ~
      path("nearest") {
        get {
          parameters('lat.as[Double], 'lon.as[Double]) { (lat, lon) =>
            complete(clientService.getNearestPlaces(lat, lon))
          }
        }
      } ~
      path("chose") {
        post {
          parameters('company_id.as[String]) { companyID =>
            clientService.chose(companyID)
            complete(SuccessMessage("thank you"))
          }
        }
      } ~
      path("heatmap_now") {
        get {
          parameters('lat1.as[Double], 'lon1.as[Double], 'lat2.as[Double], 'lon2.as[Double]) { (lat1, lon1, lat2, lon2) =>
            complete(clientService.heatmapNow(lat1, lon1, lat2, lon2))
          }
        }
      } ~
      path("heatmap_history") {
        get {
          parameters('lat1.as[Double], 'lon1.as[Double], 'lat2.as[Double], 'lon2.as[Double], 'h.as[Int]) { (lat1, lon1, lat2, lon2, h) =>
            complete(clientService.heatmapHistory(lat1, lon1, lat2, lon2, h))
          }
        }
      }
    }

  override implicit def actorRefFactory: ActorRefFactory = actorRefFactoryParam
}