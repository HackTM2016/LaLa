package com.reactivespot.lala

import com.typesafe.config.ConfigFactory
import org.apache.log4j.Logger


trait ConfigAssembly {
  val logger = Logger.getLogger(classOf[ConfigAssembly])

  lazy val conf = ConfigFactory.load()

  lazy val serverURL = conf.getString("server.url")
  lazy val serverPort = conf.getInt("server.port")

  lazy val mongoURL = conf.getString("mongo.host")
  lazy val mongoPort = conf.getInt("mongo.port")
  lazy val mongoDB = conf.getString("mongo.db")

  lazy val redisURL = conf.getString("redis.url")
  lazy val redisPort = conf.getInt("redis.port")
  lazy val redisPass = conf.getString("redis.password")
}
