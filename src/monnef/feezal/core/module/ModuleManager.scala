package monnef.feezal.core.module

import java.io.{IOException, FileInputStream, InputStreamReader, File}
import sys.error
import collection.mutable.ArrayBuffer
import java.util.Properties
import monnef.feezal.utils._
import monnef.feezal.core.Feezal
import scala.tools.nsc.util.ScalaClassLoader.URLClassLoader
import monnef.feezal.core.audio.AudioOutput

object ModuleManager {

  import Feezal._

  val MODULE_DIR = "modules"

  val modules = new ArrayBuffer[ModuleRecord]()

  def loadAndInstantiateClass(dir: File, module: ModuleRecord) {
    val jar = new File(dir.getAbsolutePath + "/" + module.jarFile)
    if (!jar.exists()) loadingError(s"Jar file '${jar.getPath}' not found.")
    if (!jar.isFile) loadingError("Jar object doesn't seem to be a file.")
    try {
      val cl = new URLClassLoader(List(jar.toURI.toURL), this.getClass.getClassLoader)
      val moduleClass: Class[_] = cl.loadClass(module.className)
      module.bindModule(moduleClass.newInstance().asInstanceOf[FeezalModule])
      modules += module
    } catch {
      case e: ClassNotFoundException => loadingError("Cannot load module class from jar.", e)
      case e: ClassCastException => loadingError("Supplied class doesn't seem to be a module.", e)
    }
  }

  def loadingError(msg: String) { throw ModuleLoadingException.create(msg) }

  def loadingError(msg: String, cause: Throwable) { throw ModuleLoadingException.create(msg, cause) }

  def processModuleDir(dir: File) {
    log.debug( s"""Processing module directory "${dir.getName}".""")
    try {
      val module = loadConfig(dir)
      log.trace(s"Read config: ${module.toString}")

      loadAndInstantiateClass(dir, module)
      log.debug(s"Got new module instance - ${module.module.toString}")
    } catch {
      case e: ModuleLoadingException => {
        log.warning( s"""Problem with loading of module in directory "${dir.getName}": "${e.getMessage}" """)
        if (e.getCause != null) {
          log.debug( s"""Cause: ${e.getCause}""")
          log.debug((for (line <- e.getStackTraceString.lines) yield "   " + line) mkString "\n")
        }
      }
    }
  }

  def loadConfig(dir: File): ModuleRecord = {
    try {
      val prop = new Properties()
      val confs = for (file <- dir.listFiles() if file.isFile && file.getName.endsWith(".conf")) yield file
      if (confs.length < 1) {
        loadingError( s"""Missing config file in module directory.""")
      }
      val conf = confs(0)
      if (confs.length > 1) log.warning( s"""Multiple config files in module directory, using surely the wrong one ("${conf.getName}").""")
      prop.load(new InputStreamReader(new FileInputStream(conf)))
      val module = new ModuleRecord(
        prop.getProperty("title"),
        prop.getProperty("version"),
        prop.getProperty("author"),
        prop.getProperty("jarFile"),
        prop.getProperty("description"),
        prop.getProperty("className")
      )
      if (!module.checkValidity()) {
        loadingError( s"""Incomplete module config file in directory.""")
      }
      module
    } catch {
      case e: IOException => {
        loadingError("Problem occurred when opening/reading module config file.", e)
        null // to silence IDE
      }
      case e: Throwable => throw e
    }
  }

  def load() {
    val modulesDir = new File(MODULE_DIR)
    if (!modulesDir.exists()) modulesDir.mkdir()
    if (!modulesDir.exists() || !modulesDir.isDirectory) error("some problem with modules directory")
    for (subFile <- modulesDir.listFiles if subFile.isDirectory) processModuleDir(subFile)
  }

  def init(output: AudioOutput): Int = {
    var c = 0
    modules.foreach(modRec => {
      log.trace( s"""Initializing module "${modRec.title}".""")
      modRec.module.init(output)
      info( s"""Initialized module ${modRec.title} ${modRec.version} created by ${modRec.author}.""")
      c += 1
    })
    c
  }

  def destroy() {
    info("Unloading modules...")
    modules.foreach(_.module.destroy())
    info("All modules unloaded")
  }

  class ModuleRecord(val title: String, val version: String, val author: String, val jarFile: String, val description: String, val className: String) {
    var module: FeezalModule = _

    def checkValidity() = title ?-: version ?-: author ?-: jarFile ?-: className

    def bindModule(newModule: FeezalModule) { module = newModule }

    override def toString: String = s"t[$title] v[$version] a[$author] j[$jarFile] d[$description] c[$className]"
  }

}
