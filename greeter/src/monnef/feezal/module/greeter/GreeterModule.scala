package monnef.feezal.module.greeter

import monnef.feezal.core.{Feezal, FeezalModule}
import monnef.feezal.core.Utils._

class GreeterModule extends FeezalModule {
  val inputGreetings = List("greet", "hello", "good morning")
  val outputGreetings = List("hello", "hi", "good morning", "greetings", "nice to see you")

  def getPartialGrammar: String = {
    s"""
      |<%command%> = ${inputGreetings mkString "|" };
    """.stripMargin
  }

  def processAudioInput(capturedText: String, rawText: String): Boolean = {
    if (inputGreetings.contains(capturedText)) {
      Feezal.output.addToSpeakQueue(outputGreetings.pickRandom)
      true
    } else false
  }
}
