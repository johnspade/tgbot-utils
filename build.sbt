import Dependencies._

name := "tgbot-utils"

ThisBuild / version := "0.8.1"

lazy val scala3 = "3.3.3"

ThisBuild / scalaVersion := scala3

ThisBuild / description  := "Collection of utilities for building Telegram bots in Scala"
ThisBuild / organization := "ru.johnspade"
ThisBuild / homepage     := Some(url("https://github.com/johnspade/tgbot-utils"))
ThisBuild / licenses     := List(("MIT", url("https://opensource.org/licenses/MIT")))
ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/johnspade/tgbot-utils"),
    "git@github.com:johnspade/tgbot-utils.git"
  )
)
ThisBuild / developers := List(
  Developer(
    "johnspade",
    "Ivan L",
    "i+tgbotutils@jspade.dev",
    url("https://github.com/johnspade")
  )
)

ThisBuild / publishMavenStyle := true
ThisBuild / publishTo         := sonatypePublishToBundle.value
sonatypeCredentialHost        := "s01.oss.sonatype.org"

ThisBuild / githubWorkflowJavaVersions          := Seq(JavaSpec.temurin("17"), JavaSpec.temurin("11"))
ThisBuild / githubWorkflowPublishTargetBranches := Seq.empty

ThisBuild / testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")

lazy val `tgbot-callback-data`: Project = (project in file("tgbot-callback-data"))
  .settings(
    libraryDependencies ++= CallbackData.dependencies
  )

lazy val `tgbot-callback-queries`: Project = (project in file("tgbot-callback-queries"))
  .settings(
    libraryDependencies ++= CallbackQueries.dependencies
  )

lazy val root = (project in file("."))
  .settings(
    name := "tgbot-utils"
  )
  .dependsOn(`tgbot-callback-data`, `tgbot-callback-queries`)
  .aggregate(`tgbot-callback-data`, `tgbot-callback-queries`)
