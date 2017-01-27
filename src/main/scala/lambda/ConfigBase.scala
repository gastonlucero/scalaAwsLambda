package lambda

import com.typesafe.config.{Config, ConfigFactory}

/**
  * Using configFacfory from typesafe
  */
trait ConfigBase {
  val config: Config = ConfigFactory.load("application.conf")
}
