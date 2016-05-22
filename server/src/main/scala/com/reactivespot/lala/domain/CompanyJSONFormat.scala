package com.reactivespot.lala.domain

trait CompanyJSONFormat extends CommonJSONFormats {
  implicit val geoJsonFormat = jsonFormat2(GEOJsonCoord)
  implicit val companyFormat = jsonFormat5(Company)
}
