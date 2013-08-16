package monnef.feezal.module.time

import monnef.feezal.core.module.FeezalModule
import monnef.feezal.core.audio.AudioInput
import java.text.SimpleDateFormat
import monnef.feezal.utils._

class TimeModule extends FeezalModule {
  val textToFormat = Map[List[String], SimpleDateFormat](
    List("what time is it") -> new SimpleDateFormat("'It is 'H' hundred 'mm'.'"),
    List("what day is it", "what day is today", "what is today") -> new SimpleDateFormat("'Today is' EEEEEEEEEEE'.'"),
    List("what is the date", "what date is it", "what date is today") -> new SimpleDateFormat("'The date is 'd MMMMMMMMMMM yyyy'.'")
  )

  val ALL_STUFF = textToFormat.flatMap(_._1)

  def getPartialGrammar: String =
    s"""
      |<${AudioInput.COMMAND_PLACEHOLDER}> = ${ALL_STUFF.mkString(" | ")};
    """.stripMargin

  def speakTime(d: SimpleDateFormat) = output.addToSpeakQueue(d formatNow)

  def processAudioInput(capturedText: String, rawText: String): Boolean =
    constructMatches(capturedText).headOption.map(speakTime).isDefined

  def constructMatches(capturedText: String): Iterable[SimpleDateFormat] =
    for {(phrases, dateFormat) <- textToFormat if phrases.contains(capturedText)} yield dateFormat
}
