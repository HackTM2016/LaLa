package com.reactivespot.lala.util

import java.math.BigInteger
import java.security.SecureRandom
import java.util.UUID

import com.roundeights.hasher.Implicits._
import scala.language.postfixOps

object Utils {
  val random = new SecureRandom()

  def nextSessionId():String = {
    UUID.randomUUID().toString
  }

  def nextSalt(): String = {
    new BigInteger(220, random).toString(32)
  }

  def hashPassword(salt: String, password: String): String = {
    password.salt(salt).sha256.hex
  }
}
