package monnef.feezal.core

import monnef.feezal.core.module.ModuleManager
import monnef.feezal.core.audio.{AudioOutputImpl, AudioInput}


object Feezal extends App with Logging {
  val version = "0.1"

  val RESOURCE_PATH = "/monnef/feezal/core/"
  val COMPUTER_PREFIX = "computer"
  val GRAMMAR_TEMPLATE_FILE_NAME = "feezal_template.gram"
  val FORCE_INFO_TO_STD_OUT = false
  val LOG_DELIM = "-" * 20
  val PREFIXES = List("computer", "please")

  val input = AudioInput
  val output = AudioOutputImpl

  var running: Boolean = _

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
    init()
    run()
    terminate()
  }


  def terminate() {
    info("Terminating.")
    ModuleManager.destroy()
    input.destroy()
    output.destroy()
    info("Good bye, my beloved.")
  }

  def run() {
    def tossPrefixes(text: String): String = { text.split(" ").dropWhile(PREFIXES.contains(_)).mkString(" ") }
    running = true
    while (running) {
      val (rawTextResult, resultObj) = input.recognize()
      val filteredTextResult = tossPrefixes(rawTextResult)
      val skip = filteredTextResult.isEmpty
      info(if (skip) "I can't hear what you said." else s"You said: $rawTextResult (filtered: $filteredTextResult)")
      if (!skip) {
        input.printScores(resultObj)
        val processedBy = for {
          moduleRec <- ModuleManager.modules
          if moduleRec.module.processAudioInput(filteredTextResult, rawTextResult)
        } yield moduleRec.title
        if (processedBy.length > 1) log.warning(s"Message processed by multiple modules - ${processedBy mkString ", "}")
        if (processedBy.length <= 0) log.info(s"Command not recognized.")
      }
      System.out.flush()
    }
  }

  def init() {
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
  }

  def loggerName = "Feezal"
}
