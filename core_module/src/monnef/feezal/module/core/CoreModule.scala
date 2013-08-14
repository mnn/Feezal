package monnef.feezal.module.core

import monnef.feezal.core.module.FeezalModule
import monnef.feezal.core.audio.AudioInput
import monnef.feezal.core.Feezal

class CoreModule extends FeezalModule {
  val action = Map[String, () => Unit](
    ("terminate voice interface", () => Feezal.running = false),
    ("who are you", () => output.addToSpeakQueue(s"I am Feezal ${Feezal.version}.")),
    ("what is your name", () => output.addToSpeakQueue(s"My name is Feezal ${Feezal.version}.")),
    ("what are you", () => output.addToSpeakQueue(s"I am computer voice interface.")),
    ("repeat", () => output.addToSpeakQueue(output.getLastMessage)),
    ("repeat last message", () => output.addToSpeakQueue(output.getLastMessage))
  )

  def getPartialGrammar: String = {
    s"""
      |<${AudioInput.COMMAND_PLACEHOLDER}> = ${action.keys.mkString(" | ")};
    """.stripMargin
  }

  def processAudioInput(capturedText: String, rawText: String): Boolean =
    if (action.contains(capturedText)) {
      action(capturedText)()
      true
    }
    else false
}
