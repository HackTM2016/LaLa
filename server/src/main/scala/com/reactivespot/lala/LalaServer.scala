package com.reactivespot.lala

import akka.actor.{Actor, ActorRefFactory, ActorSystem}
import com.reactivespot.lala.api.{CompanyAPI, MusicAPI}
import com.reactivespot.lala.util.CORSSupport
import spray.http.StatusCodes
import spray.routing.HttpService


class LalaServer(
                   musicAPI: MusicAPI,
                   companyAPI: CompanyAPI
                 )(implicit actorSystem: ActorSystem)
  extends HttpService with Actor with CORSSupport {

  override implicit def actorRefFactory: ActorRefFactory = context

  def route =
    cors {
      options(complete(StatusCodes.OK)) ~
        sealRoute {
          pathPrefix("lalaserver") {
            musicAPI.route ~
            companyAPI.route
          }
        }
    }

  override def receive: Receive = runRoute(route)
}
