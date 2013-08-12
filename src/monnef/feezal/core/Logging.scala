package monnef.feezal.core

import com.twitter.logging._
import com.twitter.logging.LoggerFactory
import config.Level

object Logging {
  private var configMasterLogger: LoggerFactory = null

  def checkMaster = {
    if (configMasterLogger == null) {
      configMasterLogger = new LoggerFactory("", Option(Level.WARNING), List(ConsoleHandler(new Formatter())))
      configMasterLogger()
    }
  }
}

trait Logging {
  def loggerName: String

  val log = Logger.get(loggerName)

  private val configLogger = new LoggerFactory(
    loggerName,
    Option(Level.TRACE),
    List(
      ConsoleHandler(new Formatter())
    ),
    false
  )
  configLogger()
  Logging.checkMaster
}
