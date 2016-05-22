package com.reactivespot.lala.domain

import spray.httpx.SprayJsonSupport
import spray.json.{RootJsonFormat, DefaultJsonProtocol}

import spray.json._
import DefaultJsonProtocol._

trait CommonJSONFormats  extends DefaultJsonProtocol with SprayJsonSupport{
  implicit val errorMessage = jsonFormat1(ErrorMessage)
  implicit val successMessage = jsonFormat1(SuccessMessage)

  implicit object ApiMessageFormat extends RootJsonFormat[ApiMessage] {
    def write(a: ApiMessage) = a match {
      case error: ErrorMessage => error.toJson
      case success: SuccessMessage => success.toJson
    }
    def read(value: JsValue) =
      value.asJsObject.fields("kind") match {
        case JsString("successmessage") => value.convertTo[SuccessMessage]
        case JsString("errormessage") => value.convertTo[ErrorMessage]
      }
  }
}

