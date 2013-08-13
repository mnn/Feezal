package monnef.feezal.core

import java.io.File
import sys.error
import collection.mutable.ArrayBuffer

object ModuleManager {

  import Feezal._

  val MODULE_DIR = "modules"

  val modules = new ArrayBuffer[FeezalModule]()

  def processModuleDir(dir: File) {
    log.debug( s"""Processing module directory "${dir.getName}".""")
    // TODO
  }

  def init() {
    val modulesDir = new File(MODULE_DIR)
    if (!modulesDir.exists()) modulesDir.mkdir()
    if (!modulesDir.exists() || !modulesDir.isDirectory) error("some problem with modules directory")
    modulesDir.listFiles.foreach(processModuleDir)
  }
}
