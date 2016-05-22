package com.reactivespot.lala.db.mongo

import com.reactivespot.lala.db.CompanyDAO
import com.reactivespot.lala.domain.Company
import reactivemongo.api.{BSONSerializationPack, DefaultDB}
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.commands._
import reactivemongo.bson.{BSONArray, BSONDocument}
import reactivemongo.core.commands._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success}


class CompanyDAOMongoImpl(db: DefaultDB) extends CompanyDAO with CompanyMapping {

  val collection: BSONCollection = db("company")

  override def createCompany(company: Company): Unit = {
    collection.insert(company, reactivemongo.api.commands.GetLastError.Acknowledged)
  }

  override def readCompany(id: String): Option[Company] = {
    val selector = BSONDocument("_id" -> id)
    val response: Future[Option[Company]] = collection.find(selector).one[Company]

    Await.result(response, 10 seconds)
  }

  override def readCompanies(lat: Double, lon: Double, range: Int): List[Company] = {
    val selector = BSONDocument(
      "geoNear" -> "company",
      "near" ->
        BSONDocument(
          "type" -> "Point",
          "coordinates" -> Array(lat, lon)
        ),
      "spherical" -> true,
      "maxDistance" -> range
    )

    val runner = Command.run(BSONSerializationPack)

    val futureResult =
      runner.apply(db, runner.rawCommand(selector)).one[BSONDocument]
    val result = Await.result(futureResult, 10 seconds)

    result.get("results") match {
      case Some(r) =>
        val parsedResult = r.asInstanceOf[BSONArray].iterator map { cd =>
          cd match {
            case Success(obj) => Some(CompanyReader.read(obj._2.asInstanceOf[BSONDocument].get("obj").get.asInstanceOf[BSONDocument]))
            case Failure(error) => None
          }
        }

        parsedResult.flatten.toList
      case None => List()
    }
  }

  override def updateCompany(id: String, company: Company): Unit = {
    val selector = BSONDocument("_id" -> id)
    val response = collection.findAndUpdate(selector, company)

    Await.result(response, 10 seconds)
  }

  override def deleteCompany(id: String): Unit = {
    val selector1 = BSONDocument("_id" -> id)
    collection.remove(selector1)
  }

}
