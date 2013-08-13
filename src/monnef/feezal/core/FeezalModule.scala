package monnef.feezal.core

trait FeezalModule {
  def getPartialGrammar: String

  def init() {}

  def destroy() {}

  def processAudioInput(capturedText: String, rawText: String): Boolean
}
