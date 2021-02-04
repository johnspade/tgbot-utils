import sbt.librarymanagement.ModuleID
import sbt.librarymanagement.syntax.Test

trait ModuleWithDependencies {
  protected def distributionDependencies: Seq[ModuleID]

  protected def testDependencies: Seq[ModuleID] = Seq.empty

  def dependencies: Seq[ModuleID] = distributionDependencies ++ testDependencies.map(_ % Test)
}

object ModuleWithDependencies {
  object V {
    val telegramium = "3.50.0"
    val zio = "1.0.4"
    val zioCats = "2.2.0.1"
    val kantanCsv = "0.6.1"
    val magnolia = "0.17.0"
    val cats = "2.3.1"
  }
}
