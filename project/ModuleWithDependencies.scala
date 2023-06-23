import sbt.librarymanagement.ModuleID
import sbt.librarymanagement.syntax.Test

trait ModuleWithDependencies {
  protected def distributionDependencies: Seq[ModuleID]

  protected def testDependencies: Seq[ModuleID] = Seq.empty

  def dependencies: Seq[ModuleID] = distributionDependencies ++ testDependencies.map(_ % Test)
}

object ModuleWithDependencies {
  object V {
    val telegramium = "8.67.1"
    val zio         = "2.0.15"
    val zioCats     = "23.0.0.7"
    val magnolia    = "1.3.2"
    val cats        = "2.9.0"
    val csv3s       = "0.1.3"
  }
}
