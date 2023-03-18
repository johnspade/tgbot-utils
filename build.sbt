import Dependencies._

name := "tgbot-utils"

ThisBuild / version := "0.7.0"

lazy val scala3 = "3.2.2"

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
    "Ivan Lopatin",
    "ivan+tgbotutils@ilopatin.ru",
    url("https://about.johnspade.ru")
  )
)

ThisBuild / publishMavenStyle := true
ThisBuild / publishTo         := sonatypePublishToBundle.value
sonatypeCredentialHost        := "s01.oss.sonatype.org"

ThisBuild / githubWorkflowJavaVersions          := Seq("adopt@1.11", "adopt@1.8")
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

lazy val `tgbot-message-entities`: Project = (project in file("tgbot-message-entities"))
  .settings(
    libraryDependencies ++= MessageEntities.dependencies
  )

lazy val root = (project in file("."))
  .settings(
    name := "tgbot-utils"
  )
  .aggregate(`tgbot-callback-data`, `tgbot-callback-queries`, `tgbot-message-entities`)
  .dependsOn(`tgbot-callback-data`, `tgbot-callback-queries`, `tgbot-message-entities`)
