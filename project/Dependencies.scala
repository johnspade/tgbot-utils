import ModuleWithDependencies.V
import sbt._

object Dependencies {
  object CallbackData extends ModuleWithDependencies {
    val distributionDependencies = Seq(
      "com.softwaremill.magnolia1_3" %% "magnolia" % V.magnolia,
      "ru.johnspade"                 %% "csv3s"    % V.csv3s
    )

    override val testDependencies = Seq(
      "dev.zio"       %% "zio-test"     % V.zio,
      "dev.zio"       %% "zio-test-sbt" % V.zio,
      "org.typelevel" %% "cats-core"    % V.cats
    )
  }

  object CallbackQueries extends ModuleWithDependencies {
    val distributionDependencies = Seq(
      "io.github.apimorphism" %% "telegramium-core" % V.telegramium,
      "io.github.apimorphism" %% "telegramium-high" % V.telegramium
    )
  }
}
