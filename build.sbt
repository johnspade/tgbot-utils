import Dependencies._

name := "tgbot-utils"

version := "0.1.0"

lazy val scala213 = "2.13.4"
lazy val scala212 = "2.12.13"

ThisBuild / scalaVersion := scala213
ThisBuild / crossScalaVersions := List(scala213, scala212)

ThisBuild / organization := "ru.johnspade"
ThisBuild / licenses := List(("MIT", url("http://opensource.org/licenses/MIT")))
ThisBuild / publishMavenStyle := true

bintrayReleaseOnPublish in ThisBuild := false

ThisBuild / testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")

lazy val `callback-queries`: Project = (project in file("callback-queries"))
  .settings(
    name := "callback-queries",
    libraryDependencies ++= CallbackQueries.dependencies,
    addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.2" cross CrossVersion.full)
  )

lazy val `message-entities`: Project = (project in file("message-entities"))
  .settings(
    name := "message-entities",
    libraryDependencies ++= MessageEntities.dependencies
  )

lazy val root = (project in file("."))
  .settings(
    name := "tgbot-utils",
    skip in publish := true,
    crossScalaVersions := Nil
  )
  .aggregate(`callback-queries`, `message-entities`)
