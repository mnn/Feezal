package monnef.feezal.core

import monnef.feezal.core.module.ModuleManager
import monnef.feezal.core.audio.{AudioOutputImpl, AudioInput}


object Feezal extends App with Logging {
  val RESOURCE_PATH = "/monnef/feezal/core/"
  val COMPUTER_PREFIX = "computer"
  val GRAMMAR_TEMPLATE_FILE_NAME = "feezal_template.gram"
  val FORCE_INFO_TO_STD_OUT = false
  val LOG_DELIM = "-" * 20

  val input = AudioInput
  val output = AudioOutputImpl

  myMain()

  def info(msg: String) {
    val toPrint =
      if (msg == null)
        s"Null in log message from ${Utils.getCaller}"
      else
        msg
    if (FORCE_INFO_TO_STD_OUT) println(s"STDOUT: $toPrint")
    log.info(toPrint)
  }

  def myMain() {
    info(LOG_DELIM)
    info("Feezal is starting...")

    info("Processing modules...")
    ModuleManager.load()
    info("Modules loaded and instantiated")

    info("Setting-up audio input...")
    input.init()
    info("Audio input ready")

    info("Setting-up audio output...")
    output.init()
    info("Audio output ready")

    info("Initializing modules...")
    val modsInitCount = ModuleManager.init(output)
    info(s"Initialized $modsInitCount module(s).")

    info("System ready for commands.")
    System.out.flush()
    var running = true
    while (running) {
      var (lastTextResult, _) = input.recognize()

      info(
        if (lastTextResult.isEmpty) "I can't hear what you said."
        else s"You said: $lastTextResult"
      )

      if (lastTextResult.startsWith(COMPUTER_PREFIX)) {
        lastTextResult = lastTextResult.substring(COMPUTER_PREFIX.length + 1)
      }

      lastTextResult match {
        case "initiate self destruct" => running = false
        case "greet" => output.addToSpeakQueue("Hello")
        case _ =>
      }
      System.out.flush()
    }
    info("Terminating.")
    ModuleManager.destroy()
    input.destroy()
    output.destroy()
    info("Good bye, my beloved.")
  }

  def loggerName = "Feezal"
}
