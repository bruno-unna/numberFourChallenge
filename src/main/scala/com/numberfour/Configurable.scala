package com.numberfour

import com.typesafe.config.ConfigFactory

trait Configurable {

  val config = ConfigFactory.load()

}