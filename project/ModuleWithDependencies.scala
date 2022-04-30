import sbt.librarymanagement.ModuleID
import sbt.librarymanagement.syntax.Test

trait ModuleWithDependencies {
  protected def distributionDependencies: Seq[ModuleID]

  protected def testDependencies: Seq[ModuleID] = Seq.empty

  def dependencies: Seq[ModuleID] = distributionDependencies ++ testDependencies.map(_ % Test)
}

object ModuleWithDependencies {
  object V {
    val telegramium = "7.60.0"
    val zio         = "2.0.0-RC5"
    val zioCats     = "3.3.0-RC6"
    val kantanCsv   = "0.6.2"
    val magnolia    = "1.0.0"
    val cats        = "2.7.0"
    val zcsv        = "0.1.0-SNAPSHOT"
  }
}
