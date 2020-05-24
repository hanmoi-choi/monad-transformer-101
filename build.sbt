import Dependencies.{scalaTest, _}
import sbt.Keys.libraryDependencies

ThisBuild / scalaVersion     := "2.13.2"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "mondad-transformer",
    libraryDependencies += scalaTest % Test,
    libraryDependencies += cats,
    libraryDependencies += catsEffect,

  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
