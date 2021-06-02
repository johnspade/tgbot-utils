import sbt.librarymanagement.ModuleID
import sbt.librarymanagement.syntax.Test

trait ModuleWithDependencies {
  protected def distributionDependencies: Seq[ModuleID]

  protected def testDependencies: Seq[ModuleID] = Seq.empty

  def dependencies: Seq[ModuleID] = distributionDependencies ++ testDependencies.map(_ % Test)
}

object ModuleWithDependencies {
  object V {
    val telegramium = "4.52.2"
    val zio = "1.0.9"
    val zioCats = "3.1.1.0"
    val kantanCsv = "0.6.1"
    val magnolia = "0.17.0"
    val cats = "2.6.1"
  }
}
