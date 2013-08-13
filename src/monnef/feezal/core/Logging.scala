package monnef.feezal.core

import com.twitter.logging._
import com.twitter.logging.LoggerFactory
import config.Level

object Logging {
  private var configMasterLogger: LoggerFactory = null

  def checkMaster() {
    if (configMasterLogger == null) {
      configMasterLogger = new LoggerFactory("", Option(Level.WARNING), List(ConsoleHandler(new Formatter())))
      configMasterLogger()
    }
  }

  def resetMaster() { configMasterLogger = null }
}

trait Logging {

  import Logging._

  def loggerName: String

  val log = Logger.get(loggerName)

  private val configLogger = new LoggerFactory(
    loggerName,
    Option(Level.ALL),
    List(
      ConsoleHandler(new Formatter(), Option(Level.INFO)),
      FileHandler("feezal.log", level = Option(Level.TRACE))
    ),
    false
  )
  configLogger()
  checkMaster()

  def forceReInitLoggers() {
    configLogger()
    resetMaster()
    checkMaster()
  }
}
