package com.reactivespot.lala.domain


trait ClientJSONFormats extends CommonJSONFormats {
  implicit val placeFormat = jsonFormat6(Place)

  implicit val heatnessFormat = jsonFormat3(Heatness)
}
