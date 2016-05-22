package com.reactivespot.lala.db.mongo

import com.reactivespot.lala.domain.{VisitStat, Company}
import reactivemongo.bson.{BSONDocumentReader, BSONDocument, BSONDocumentWriter}


trait VisitStatMapping {
  implicit object VisitStatWriter extends BSONDocumentWriter[VisitStat] {
    def write(visitStat: VisitStat): BSONDocument =
      BSONDocument(
        "company_id" -> visitStat.companyID,
        "h" -> visitStat.h,
        "day" -> visitStat.day,
        "nr" -> visitStat.nr
      )
  }

  implicit object VisitStatReader extends BSONDocumentReader[VisitStat] {
    def read(doc: BSONDocument): VisitStat = {
      VisitStat(
        doc.getAs[String]("company_id").get,
        doc.getAs[Int]("h").get,
        doc.getAs[Int]("day").get,
        doc.getAs[Int]("nr").get
      )
    }
  }
}
