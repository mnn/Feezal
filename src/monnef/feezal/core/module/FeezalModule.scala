package monnef.feezal.core.module

import monnef.feezal.core.audio.AudioOutput

trait FeezalModule {
  protected var output: AudioOutput = _

  def getPartialGrammar: String

  def init(output: AudioOutput) {
    this.output = output
  }

  def destroy() {}

  def processAudioInput(capturedText: String, rawText: String): Boolean
}
