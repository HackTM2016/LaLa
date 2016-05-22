package com.reactivespot.lala.util

import com.reactivespot.lala.domain.Coordinates


object GEOUtils {
  def getDistanceInMeters(coord1: Coordinates, coord2: Coordinates): Double = {
    val R = 6378.137
    val dlat = (coord2.lat - coord1.lat) * Math.PI /180
    val dlng = (coord2.lng - coord1.lng) * Math.PI /180
    val a = Math.sin(dlat/2) * Math.sin(dlat/2) +
      Math.cos(coord1.lat * Math.PI/180)*Math.cos(coord2.lat * Math.PI/180) *
        Math.sin(dlng/2)*Math.sin(dlng/2)
    val c = 2*Math.atan2(Math.sqrt(a), Math.sqrt(1-a))
    val d = R*c

    d*1000
  }
}