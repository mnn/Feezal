package monnef.feezal.core.audio

import monnef.feezal.utils._

trait AudioOutput {
  def isSpeaking: Boolean

  def init()

  def destroy()

  def addToSpeakQueue(text: String)

  def waitUntilSilent() {
    while (isSpeaking) sleep(100)
  }

  def getLastMessage: String
}


