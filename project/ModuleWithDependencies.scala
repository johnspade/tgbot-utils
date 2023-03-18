import sbt.librarymanagement.ModuleID
import sbt.librarymanagement.syntax.Test

trait ModuleWithDependencies {
  protected def distributionDependencies: Seq[ModuleID]

  protected def testDependencies: Seq[ModuleID] = Seq.empty

  def dependencies: Seq[ModuleID] = distributionDependencies ++ testDependencies.map(_ % Test)
}

object ModuleWithDependencies {
  object V {
    val telegramium = "7.66.0"
    val zio         = "2.0.9"
    val zioCats     = "23.0.0.1"
    val magnolia    = "1.3.0"
    val cats        = "2.7.0"
    val csv3s       = "0.1.1"
  }
}
