package monnef.feezal.module.greeter

import monnef.feezal.core.Utils._
import monnef.feezal.core.module.FeezalModule

class GreeterModule extends FeezalModule {
  val SAY_HI = "say hi"
  val SAY_HELLO = "say hello"
  val HI = "hi"
  val HELLO = "hello"

  val inputGreetings = List("greet", HELLO, "good morning", HI, SAY_HI, SAY_HELLO)
  val outputGreetings = List(HELLO, HI, "good morning", "greetings", "nice to see you")
  val special = Map[String, String](SAY_HI -> HI, SAY_HELLO -> HELLO)

  def getPartialGrammar: String = {
    s"""
      |<%command%> = ${inputGreetings mkString " | " };
    """.stripMargin
  }

  def processAudioInput(capturedText: String, rawText: String): Boolean = {
    if (inputGreetings.contains(capturedText)) {
      output.addToSpeakQueue(
        if (special.contains(capturedText)) special(capturedText)
        else outputGreetings.pickRandom
      )
      true
    } else false
  }
}
