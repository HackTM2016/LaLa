package com.reactivespot.lala.db.mongo

import com.reactivespot.lala.db.VisitStatDAO
import com.reactivespot.lala.domain.{Playlist, VisitStat}
import reactivemongo.api.DefaultDB
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.commands.GetLastError
import reactivemongo.bson.BSONDocument

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}

import scala.concurrent.duration._
import scala.language.postfixOps


class VisitStatDAOMongoImpl(db: DefaultDB) extends VisitStatDAO with VisitStatMapping {

  val collection: BSONCollection = db("visit_stat")

  override def createVisitStat(visitStat: VisitStat): Unit = {
    collection.insert(visitStat, GetLastError.Acknowledged)
  }

  override def readVisitStat(id: String): Option[VisitStat] = {
    val selector = BSONDocument("_id" -> id)
    val response: Future[Option[VisitStat]] = collection.find(selector).one[VisitStat]

    Await.result(response, 10 seconds)
  }

  override def readVisitStat(companyID: String, h: Int): List[VisitStat] = {
    val selector = BSONDocument("company_id" -> companyID, "h" -> h)
    val response: Future[List[VisitStat]] = collection.find(selector).cursor[VisitStat].collect[List]()

    Await.result(response, 10 seconds)
  }

  override def readVisitStat(companyID: String, h: Int, day: Int): Option[VisitStat] = {
    val selector = BSONDocument("company_id" -> companyID, "h" -> h, "day" -> day)
    val response: Future[Option[VisitStat]] = collection.find(selector).one[VisitStat]

    Await.result(response, 10 seconds)
  }

  override def updateVisitStat(id: String, visitStat: VisitStat): Unit = {
    val selector = BSONDocument("_id" -> id)
    val response = collection.findAndUpdate(selector, visitStat)

    Await.result(response, 10 seconds)
  }

  override def updateVisitStat(visitStat: VisitStat): Unit = {
    val selector = BSONDocument("company_id" -> visitStat.companyID, "h" -> visitStat.h, "day" -> visitStat.day)
    val response = collection.findAndUpdate(selector, visitStat, upsert = true)

    Await.result(response, 10 seconds)
  }

  override def deleteVisitStat(id: String): Unit = {
    val selector1 = BSONDocument("_id" -> id)
    collection.remove(selector1)
  }

}
