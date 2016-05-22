package com.reactivespot.lala.domain

trait ApiMessage { def message: String }

case class ErrorMessage(message: String) extends ApiMessage
case class SuccessMessage(message: String) extends ApiMessage
