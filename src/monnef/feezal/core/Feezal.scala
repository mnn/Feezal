package monnef.feezal.core

import sys.error

object Feezal extends App with Logging {
  val RESOURCE_PATH = "/monnef/feezal/core/"
  val COMPUTER_PREFIX = "computer"
  val GRAMMAR_TEMPLATE_FILE_NAME = "feezal_template.gram"
  val FORCE_INFO_TO_STD_OUT = false
  val LOG_DELIM = "-" * 20

  myMain()

  def info(msg: String) {
    if (FORCE_INFO_TO_STD_OUT) println(s"STDOUT: $msg")
    if (msg == null) error("oh man, got null in log message");
    log.info(msg)
  }

  def myMain() {
    info(LOG_DELIM)
    info("Feezal is starting...")
    ModuleManager.init()
    info("Modules loaded")
    AudioInput.init()

    info("You can input commands now.")
    System.out.flush()
    var running = true
    while (running) {
      var (lastTextResult, _) = AudioInput.recognize()

      info(
        (
          if (lastTextResult.isEmpty) "I can't hear what you said."
          else s"You said: $lastTextResult"
          )
      )

      if (lastTextResult.startsWith(COMPUTER_PREFIX)) {
        lastTextResult = lastTextResult.substring(COMPUTER_PREFIX.length + 1)
      }

      lastTextResult match {
        case "initiate self destruct" => running = false
        case _ =>
      }
      System.out.flush()
    }
    info("Terminating.")
    AudioInput.destroy()
  }

  def loggerName = "Feezal"
}
