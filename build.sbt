import Dependencies._

name := "tgbot-utils"

ThisBuild / version := "0.2.0"

lazy val scala213 = "2.13.4"
lazy val scala212 = "2.12.13"

ThisBuild / scalaVersion := scala213
ThisBuild / crossScalaVersions := List(scala213, scala212)

ThisBuild / organization := "ru.johnspade"
ThisBuild / licenses := List(("MIT", url("http://opensource.org/licenses/MIT")))

bintrayReleaseOnPublish in ThisBuild := false
ThisBuild / githubWorkflowJavaVersions := Seq("adopt@1.11", "adopt@1.8")
ThisBuild / githubWorkflowPublishTargetBranches := Seq.empty

ThisBuild / testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")

lazy val `tgbot-callback-queries`: Project = (project in file("tgbot-callback-queries"))
  .settings(
    name := "tgbot-callback-queries",
    libraryDependencies ++= CallbackQueries.dependencies,
    addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.2" cross CrossVersion.full)
  )

lazy val `tgbot-message-entities`: Project = (project in file("tgbot-message-entities"))
  .settings(
    name := "tgbot-message-entities",
    libraryDependencies ++= MessageEntities.dependencies
  )

lazy val root = (project in file("."))
  .settings(
    name := "tgbot-utils"
  )
  .aggregate(`tgbot-callback-queries`, `tgbot-message-entities`)
  .dependsOn(`tgbot-callback-queries`, `tgbot-message-entities`)
