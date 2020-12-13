ThisBuild / baseVersion := "0.1"
ThisBuild / organization := "org.systemfw"
ThisBuild / publishGithubUser := "SystemFw"
ThisBuild / publishFullName := "Fabio Labella"

// sbt-sonatype wants these in Global
Global / homepage := Some(url("https://github.com/SystemFw/my-project"))
Global / scmInfo := Some(ScmInfo(url("https://github.com/SystemFw/$name$"), "git@github.com:SystemFw/$name$.git"))
Global / excludeLintKeys += scmInfo

ThisBuild / crossScalaVersions := Seq("3.0.0-M2", "2.12.10", "2.13.4")
ThisBuild / versionIntroduced := Map("3.0.0-M2" -> "3.0.0")

ThisBuild / initialCommands := """
  |import cats._, data._, syntax.all._
  |import cats.effect._, concurrent._, implicits._
  |import fs2._
  |import fs2.concurrent._
  |import scala.concurrent.duration._
  |import $package$
""".stripMargin

ThisBuild / testFrameworks += new TestFramework("munit.Framework")

lazy val root = project
  .in(file("."))
  .aggregate(core)
  .enablePlugins(NoPublishPlugin)

lazy val core = project
  .in(file("modules/core"))
  .settings(
    name := "$name$-core",
    scalafmtOnCompile := true,
    libraryDependencies ++=
      dep("org.typelevel", "cats-", "$cats$")("core")() ++
      dep("org.typelevel", "cats-effect", "$ce$")("")("-laws") ++
      dep("co.fs2", "fs2-", "$fs2$")("core", "io")() ++
      dep("org.scalameta", "munit", "$munit$")()("", "-scalacheck") ++
      dep("org.typelevel", "", "$munit_ce$")()("munit-cats-effect-2") ++
      dep("org.typelevel",  "scalacheck-effect", "$munit_check_eff$")()("", "-munit")
  )

lazy val docs = project
  .in(file("docs"))
  .settings(
    mdocIn := file("modules/docs"),
    mdocOut := file("docs"),
    mdocVariables := Map("VERSION" -> version.value)
  ).dependsOn(core)
   .enablePlugins(MdocPlugin)

def dep(org: String, prefix: String, version: String)(modules: String*)(testModules: String*) =
  modules.map(m => org %% (prefix ++ m) % version) ++
   testModules.map(m => org %% (prefix ++ m) % version % Test)

