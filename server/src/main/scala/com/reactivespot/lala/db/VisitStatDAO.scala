package com.reactivespot.lala.db

import com.reactivespot.lala.domain.VisitStat


trait VisitStatDAO {
  def createVisitStat(visitStat: VisitStat)

  def readVisitStat(id: String): Option[VisitStat]

  def readVisitStat(companyID: String, h: Int): List[VisitStat]

  def readVisitStat(companyID: String, h: Int, day: Int): Option[VisitStat]

  def updateVisitStat(id: String, visitStat: VisitStat)

  def updateVisitStat(visitStat: VisitStat)

  def deleteVisitStat(id: String)
}
