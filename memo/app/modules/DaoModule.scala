package modules

import com.google.inject.AbstractModule
import dao.MemoDao
import dao.MemoDaoImpl

class DaoModule extends AbstractModule {
  override def configure() = {
    bind(classOf[MemoDao]).to(classOf[MemoDaoImpl]).asEagerSingleton()
  }
}
