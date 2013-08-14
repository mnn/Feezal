package monnef.feezal.core.audio

import edu.cmu.sphinx.util.props.ConfigurationManager
import edu.cmu.sphinx.recognizer.Recognizer
import edu.cmu.sphinx.frontend.util.Microphone
import sys.error
import edu.cmu.sphinx.result.{ConfidenceScorer, Result}
import reflect.io.Path
import monnef.feezal.core.{Utils, Feezal}
import monnef.feezal.core.module.ModuleManager
import scala.collection.immutable.Range

object AudioInput {

  import Feezal._

  val GRAMMAR_TEMPLATE_FILE_NAME = "feezal_template.gram"
  val GRAMMAR_GENERATED_FILE_NAME = "feezal.gram"
  val MIC_ERROR_MSG = "cannot set-up microphone"
  val TMP_DIR = "tmp"
  val CONFIG_FILE_NAME = "feezal.xml"
  val COMMAND_PLACEHOLDER: String = "%command%"

  private var cm: ConfigurationManager = null
  private var recognizer: Recognizer = null
  private var microphone: Microphone = null
  private var score: ConfidenceScorer = null

  def prepareGrammar() {
    log.debug("Current dir is %s", new java.io.File(".").getCanonicalPath)
    val input = getClass.getResourceAsStream(s"/monnef/feezal/core/$GRAMMAR_TEMPLATE_FILE_NAME")
    var templateGrammar = io.Source.fromInputStream(input).getLines().mkString("\n")
    if (templateGrammar.isEmpty) templateGrammar = Path(s"$GRAMMAR_TEMPLATE_FILE_NAME").toFile.toString()
    if (templateGrammar.isEmpty) error("Cannot load template for grammar.")

    val generatedTail = (for {(moduleRec, id) <- ModuleManager.modules.zipWithIndex} yield {
      moduleRec.module.getPartialGrammar.replace(COMMAND_PLACEHOLDER, "command" + id)
    }).mkString("\n")
    val commandTerm = (for (id <- Range(0, ModuleManager.modules.length)) yield s"<command$id>").mkString("<commandM> = ", " | ", " ;")
    val finalGrammar = List(templateGrammar, commandTerm, generatedTail) mkString "\n"

    Path(TMP_DIR).createDirectory(force = false, failIfExists = false)
    log.trace(List("]] Generated grammar:", finalGrammar, "]] End of generated grammar").mkString("\n"))
    Utils.writeTextToFile(TMP_DIR + "/" + GRAMMAR_GENERATED_FILE_NAME, finalGrammar)
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
    score = cm.lookup("confidenceScorer").asInstanceOf[ConfidenceScorer]
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

  def printScores(resultObj: Result) {
    if (score == null) return;
    val confRes = score.score(resultObj)
    val best = confRes.getBestHypothesis
    log.trace( s"""best transcript: ${best.getTranscriptionNoFiller}" """)
    val confidence = best.getLogMath.logToLinear(best.getConfidence.asInstanceOf[Float])
    log.debug( s"""total conf.: ${"%.2f".format(confidence)} """)
  }
}
