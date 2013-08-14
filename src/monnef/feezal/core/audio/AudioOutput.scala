package monnef.feezal.core.audio

import monnef.feezal.core.Utils

trait AudioOutput {
  def isSpeaking: Boolean

  def init()

  def destroy()

  def addToSpeakQueue(text: String)

  def waitUntilSilent() {
    while (isSpeaking) Utils.sleep(100)
  }
}


