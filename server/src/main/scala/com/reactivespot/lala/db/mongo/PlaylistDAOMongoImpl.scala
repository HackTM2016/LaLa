package com.reactivespot.lala.db.mongo

import com.reactivespot.lala.db.PlaylistDAO
import com.reactivespot.lala.domain.Playlist
import reactivemongo.api.DefaultDB
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.commands.GetLastError
import reactivemongo.bson.BSONDocument

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}

import scala.concurrent.duration._
import scala.language.postfixOps

class PlaylistDAOMongoImpl(db: DefaultDB) extends PlaylistDAO with PlaylistMapping {

  val collection: BSONCollection = db("playlist")

  override def createPlayList(playlist: Playlist): Unit = {
    collection.insert(playlist, GetLastError.Acknowledged)
  }

  override def readCompaniesPlaylists(companyID: String): List[Playlist] = {
    val selector = BSONDocument("company_id" -> companyID)
    val response: Future[List[Playlist]] = collection.find(selector).cursor[Playlist].collect[List]()

    Await.result(response, 10 seconds)
  }

  override def readPlayList(id: String): Option[Playlist] = {
    val selector = BSONDocument("_id" -> id)
    val response: Future[Option[Playlist]] = collection.find(selector).one[Playlist]

    Await.result(response, 10 seconds)
  }

  override def updatePlayList(id: String, playList: Playlist): Unit = {
    val selector = BSONDocument("_id" -> id)
    val response = collection.findAndUpdate(selector, playList)

    Await.result(response, 10 seconds)
  }

  override def deletePlayList(id: String): Unit = {
    val selector1 = BSONDocument("_id" -> id)
    collection.remove(selector1)
  }

}

