package monnef.feezal.core.module

class ModuleLoadingException(msg: String) extends RuntimeException(msg)

object ModuleLoadingException {
  def create(msg: String) = new ModuleLoadingException(msg)

  def create(msg: String, cause: Throwable) = new ModuleLoadingException(msg).initCause(cause)
}