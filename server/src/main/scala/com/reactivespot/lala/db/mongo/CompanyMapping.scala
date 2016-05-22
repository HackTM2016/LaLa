package com.reactivespot.lala.db.mongo

import com.reactivespot.lala.domain.{GEOJsonCoord, Company}
import reactivemongo.bson._

import scala.util.{Failure, Success}

trait CompanyMapping {

  implicit object GEOJsonWriter extends BSONDocumentWriter[GEOJsonCoord] {
    def write(coord: GEOJsonCoord): BSONDocument =
      BSONDocument(
        "type" -> coord.`type`,
        "coordinates" -> coord.coordinates
      )
  }

  implicit object GEOJsonReader extends BSONDocumentReader[GEOJsonCoord] {
    def read(doc: BSONDocument): GEOJsonCoord = {
      GEOJsonCoord(
        doc.getAs[String]("type").get,
        doc.getAs[BSONArray]("coordinates").get.iterator.toList.map { coord =>
          coord match {
            case Success(c) => c._2.asInstanceOf[BSONDouble].value
            case Failure(error) => throw new RuntimeException("Unexpected error")
          }
        }.toArray
      )
    }
  }

  implicit object CompanyWriter extends BSONDocumentWriter[Company] {
    def write(company: Company): BSONDocument =
      BSONDocument(
        "_id" -> company.email,
        "name" -> company.name,
        "loc" -> GEOJsonWriter.write(company.loc),
        "range" -> company.range,
        "logo" -> company.logo
      )
  }

  implicit object CompanyReader extends BSONDocumentReader[Company] {
    def read(doc: BSONDocument): Company = {
      Company(
        doc.getAs[String]("_id").get,
        doc.getAs[String]("name").get,
        GEOJsonReader.read(doc.getAs[BSONDocument]("loc").get),
        doc.getAs[Int]("range").get,
        doc.getAs[String]("logo")
      )
    }
  }
}
