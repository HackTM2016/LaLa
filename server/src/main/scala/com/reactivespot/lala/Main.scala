package com.reactivespot.lala

import akka.actor.Props
import akka.io.IO
import spray.can.Http


object Main extends App with CommonAssembly{
  logger.info("Starting lala server ...")

  val service = system.actorOf(
    Props(new LalaServer(
      musicAPI,
      companyAPI
    )),
    "main-service"
  )

  IO(Http).tell(Http.Bind(service, interface = serverURL, port = serverPort), sender = service)
}
