package monnef.feezal.core.audio

import javax.speech.synthesis.{Synthesizer, SynthesizerModeDesc}
import java.util.Locale
import sys.error
import javax.speech.Central
import monnef.feezal.core.Feezal._

object AudioOutputImpl extends AudioOutput {
  val VOICE_NAME = "kevin16"

  def isSpeaking: Boolean = {
    synth.testEngineState(Synthesizer.QUEUE_NOT_EMPTY)
  }

  private var desc: SynthesizerModeDesc = _
  private var synth: Synthesizer = _

  def init() {
    System.setProperty("FreeTTSSynthEngineCentral", "com.sun.speech.freetts.jsapi.FreeTTSEngineCentral")
    System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory")
    Central.registerEngineCentral("com.sun.speech.freetts.jsapi.FreeTTSEngineCentral")

    desc = new SynthesizerModeDesc(null, "general", Locale.US, null, null)

    synth = Central.createSynthesizer(desc)
    synth.allocate()
    desc = synth.getEngineModeDesc.asInstanceOf[SynthesizerModeDesc]

    /*
    desc = new SynthesizerModeDesc(null,
      "time", /* use "time" or "general" */
      Locale.US,
      false,
      null)

    val central = new FreeTTSEngineCentral()
    val engineList: EngineList = central.createEngineList(desc)

    synth = null
    if (engineList.size > 0) {
      val creator: EngineCreate = engineList.get(0).asInstanceOf[EngineCreate]
      synth = creator.createEngine().asInstanceOf[Synthesizer]
    }
    if (synth == null) {
      error("Cannot create synthesizer")
    }
    synth.allocate()
    */

    val validVoices = for (voice <- desc.getVoices if voice.getName.equals(VOICE_NAME)) yield voice
    if (validVoices.length < 1) error("Requested voice not found.")
    synth.getSynthesizerProperties.setVoice(validVoices(0))
    synth.resume()
  }

  def destroy() {
    info("Unloading synthesiser...")
    synth.cancelAll()
    synth.pause()
    synth.deallocate()
    info("Audio output part unloaded")
  }

  def speakInternal(text: String) {
    synth.speakPlainText(text, null)
    synth.waitEngineState(Synthesizer.QUEUE_EMPTY)
  }

  def addToSpeakQueue(text: String) {
    speakInternal(text)
    info( s"""Saying: "$text".""")
  }
}
