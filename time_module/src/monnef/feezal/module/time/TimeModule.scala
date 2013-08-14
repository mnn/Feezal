package monnef.feezal.module.time

import monnef.feezal.core.Utils._
import monnef.feezal.core.module.FeezalModule
import monnef.feezal.core.audio.AudioInput
import java.text.SimpleDateFormat

class TimeModule extends FeezalModule {
  val TIME = List("what time is it")
  val DAY = List("what day is it", "what day is today", "what is today")
  val DATE = List("what is the date", "what date is it")

  val ALL_STUFF = TIME ::: DAY ::: DATE

  val DATE_FORMAT = new SimpleDateFormat("'The date is 'd MMMMMMMMMMM yyyy'.'")
  val TIME_FORMAT = new SimpleDateFormat("'It is 'Hmm'.'")
  val DAY_FORMAT = new SimpleDateFormat("'Today is' EEEEEEEEEEE'.'")

  val textToFormat = Map[List[String], SimpleDateFormat](TIME -> TIME_FORMAT, DAY -> DAY_FORMAT, DATE -> DATE_FORMAT)

  def getPartialGrammar: String = {
    s"""
      |<${AudioInput.COMMAND_PLACEHOLDER}> = ${ALL_STUFF.mkString(" | ")};
    """.stripMargin
  }

  def processAudioInput(capturedText: String, rawText: String): Boolean = {
    for {
      (phrases, dateFormat) <- textToFormat
      if phrases.contains(capturedText)
    } {
      output.addToSpeakQueue(dateFormat.formatNow)
      return true
    }

    false
  }
}
