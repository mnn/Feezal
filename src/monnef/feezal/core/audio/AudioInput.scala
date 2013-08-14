package monnef.feezal.core.audio

import edu.cmu.sphinx.util.props.ConfigurationManager
import edu.cmu.sphinx.recognizer.Recognizer
import edu.cmu.sphinx.frontend.util.Microphone
import sys.error
import edu.cmu.sphinx.result.Result
import java.io.FileWriter
import reflect.io.Path
import monnef.feezal.core.Feezal
import monnef.feezal.core.Feezal._

object AudioInput {

  import Feezal._

  val GRAMMAR_TEMPLATE_FILE_NAME = "feezal_template.gram"
  val GRAMMAR_GENERATED_FILE_NAME = "feezal.gram"
  val MIC_ERROR_MSG = "cannot set-up microphone"
  val TMP_DIR = "tmp"
  val CONFIG_FILE_NAME = "feezal.xml"

  private var cm: ConfigurationManager = null
  private var recognizer: Recognizer = null
  private var microphone: Microphone = null

  def prepareGrammar() {
    // TODO
    log.debug("Current dir is %s", new java.io.File(".").getCanonicalPath)
    val input = getClass.getResourceAsStream(s"/monnef/feezal/core/$GRAMMAR_TEMPLATE_FILE_NAME")
    var inputText = io.Source.fromInputStream(input).getLines().mkString("\n")
    if (inputText.isEmpty) inputText = Path(s"$GRAMMAR_TEMPLATE_FILE_NAME").toFile.toString()
    Path(TMP_DIR).createDirectory(force = false, failIfExists = false)
    val output = new FileWriter(TMP_DIR + "/" + GRAMMAR_GENERATED_FILE_NAME)
    output.write(inputText)
    output.close()
  }

  def init() {
    info("Merging grammars...")
    prepareGrammar()
    info("Grammar generated")
    info("Initializing recognizer...")
    cm = new ConfigurationManager(CONFIG_FILE_NAME)
    recognizer = cm.lookup("recognizer").asInstanceOf[Recognizer]
    recognizer.allocate()
    forceReInitLoggers() // recognizer's allocate messes up loggers, have no idea why...
    info("Recognizer ready")
    info("Initializing mic...")
    microphone = cm.lookup("microphone").asInstanceOf[Microphone]
    if (!microphone.startRecording()) {
      log.critical(MIC_ERROR_MSG)
      error(MIC_ERROR_MSG)
    }
    info("Microphone ready")
  }

  def destroy() {
    info("Unloading mic...")
    microphone.stopRecording()
    info("Unloading recognizer...")
    recognizer.deallocate()
    info("Audio input part unloaded")
  }

  def recognize(): (String, Result) = {
    val result = recognizer.recognize()
    (result.getBestFinalResultNoFiller, result)
  }
}
