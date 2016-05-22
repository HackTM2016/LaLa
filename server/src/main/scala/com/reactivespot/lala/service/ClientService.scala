package com.reactivespot.lala.service

import com.reactivespot.lala.domain.{Heatness, Place}


trait ClientService {
  def getNearestPlaces(lat: Double, lon: Double): Array[Place]

  def chose(companyID: String)

  def heatmapNow(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Array[Heatness]

  def heatmapHistory(lat1: Double, lon1: Double, lat2: Double, lon2: Double, h: Int): Array[Heatness]
}
