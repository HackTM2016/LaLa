package com.reactivespot.lala

import akka.actor.ActorSystem
import com.reactivespot.lala.api.{CompanyAPI, MusicAPI}
import com.reactivespot.lala.db.mongo.{CompanyDAOMongoImpl, PlaylistDAOMongoImpl, VisitStatDAOMongoImpl}
import com.reactivespot.lala.db.{CompanyDAO, PlaylistDAO, VisitStatDAO}
import com.reactivespot.lala.logic.{ClientServiceImpl, CompanyServiceImpl, MusicServiceImpl}
import com.reactivespot.lala.service.{ClientService, CompanyService, MusicService}
import com.redis.RedisClient
import reactivemongo.api.{DefaultDB, MongoDriver}

import scala.concurrent.ExecutionContext.Implicits.global


trait CommonAssembly extends ConfigAssembly {
  implicit val system = ActorSystem("lalaSystem")

  val driver = new MongoDriver
  val connection = driver.connection(List(mongoURL))

  val db: DefaultDB = connection(mongoDB)
  lazy val redis = new RedisClient(redisURL, redisPort)
  redis.auth(redisPass)

  // ===================================================================================================================

  lazy val playlistDAO: PlaylistDAO = new PlaylistDAOMongoImpl(db)
  lazy val companyDAO: CompanyDAO = new CompanyDAOMongoImpl(db)
  lazy val visitStatDAO: VisitStatDAO = new VisitStatDAOMongoImpl(db)

  // ===================================================================================================================

  lazy val musicService: MusicService = new MusicServiceImpl(playlistDAO, redis)
  lazy val companyService: CompanyService = new CompanyServiceImpl(companyDAO)
  lazy val clientService: ClientService = new ClientServiceImpl(companyDAO, visitStatDAO)

  // ===================================================================================================================

  lazy val musicAPI: MusicAPI = new MusicAPI(musicService)
  lazy val companyAPI: CompanyAPI = new CompanyAPI(companyService, clientService)
}
