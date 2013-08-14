package monnef.feezal.core

import scala.util.Random

object Utils {
  def sleep(ms: Int) {
    Thread.sleep(ms)
  }

  def getCaller: String = getCaller(4)

  def getCaller(depth: Int): String = {
    val stack = Thread.currentThread().getStackTrace
    val last = stack(depth)
    s"${last.getClassName}.${last.getMethodName}@${last.getLineNumber}"
  }

  val rand = new Random()

  implicit class ListMethods[A](val list: List[A]) {
    def pickRandom: A = list(rand.nextInt(list.length))
  }

  def falseIfEmpty(test: String, elseBranch: => Boolean): Boolean = if (test == null || test.trim.isEmpty) false else elseBranch

  implicit def pimpBool(b: => Boolean) = new {
    def ?-:(s: String): Boolean = falseIfEmpty(s, b)
  }

  implicit def pimpString(r: => String) = new {
    def ?-:(l: String): Boolean = l ?-: r ?-: true
  }
}
