package com.reactivespot.lala.domain

case class Company (
  email: String,
  name: String,
  loc: GEOJsonCoord,
  range: Int,
  logo: Option[String]
)

case class Coordinates(
  lat: Double,
  lng: Double
)

case class GEOJsonCoord(
  `type`: String,
  coordinates: Array[Double]
)