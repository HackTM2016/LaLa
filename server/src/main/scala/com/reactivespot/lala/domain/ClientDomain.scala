package com.reactivespot.lala.domain


case class Place(
  id: String,
  name: String,
  lat: Double,
  lon: Double,
  range: Int,
  logo: Option[String]
)

case class Heatness(
  lat: Double,
  lon: Double,
  value: Int
)