package monnef.feezal.core


object Feezal extends App with Logging {
  val TIME_GRAMMAR_NAME = "time"
  val RESOURCE_PATH = "/monnef/feezal/core/"
  val COMPUTER_PREFIX = "computer"
  val CONFIG_FILE_NAME = "feezal.xml"
  val GRAMMAR_TEMPLATE_FILE_NAME = "feezal_template.gram"

  myMain()

  def info(msg: String) { log.info(msg) }

  def myMain() {
    info("Starting...")
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
          ) + "\n"
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
  }

  def loggerName = "Feezal"
}
