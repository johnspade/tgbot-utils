import ModuleWithDependencies.V
import sbt._

object Dependencies {
  object CallbackData extends ModuleWithDependencies {
    val distributionDependencies = Seq(
      "com.nrinaudo" %% "kantan.csv" % V.kantanCsv,
      "com.propensive" %% "magnolia" % V.magnolia
    )

    override val testDependencies = Seq(
      "dev.zio" %% "zio-test" % V.zio,
      "dev.zio" %% "zio-test-sbt" % V.zio,
      "org.typelevel" %% "cats-core" % V.cats
    )
  }

  object CallbackQueries extends ModuleWithDependencies {
    val distributionDependencies = Seq(
      "io.github.apimorphism" %% "telegramium-core" % V.telegramium,
      "io.github.apimorphism" %% "telegramium-high" % V.telegramium
    )
  }

  object MessageEntities extends ModuleWithDependencies {
    val distributionDependencies = Seq(
      "io.github.apimorphism" %% "telegramium-core" % V.telegramium,
      "io.github.apimorphism" %% "telegramium-high" % V.telegramium
    )

    override val testDependencies = Seq(
      "dev.zio" %% "zio" % V.zio,
      "dev.zio" %% "zio-interop-cats" % V.zioCats,
      "dev.zio" %% "zio-test" % V.zio,
      "dev.zio" %% "zio-test-sbt" % V.zio
    )
  }
}
