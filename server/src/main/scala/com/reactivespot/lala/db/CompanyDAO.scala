package com.reactivespot.lala.db

import com.reactivespot.lala.domain.Company

trait CompanyDAO {
  def createCompany(company: Company)

  def readCompany(id: String): Option[Company]

  def readCompanies(lat: Double, lon: Double, range: Int): List[Company]

  def updateCompany(id: String, company: Company)

  def deleteCompany(id: String)
}
