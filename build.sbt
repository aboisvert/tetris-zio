
lazy val buildSettings = Seq(
  version := "0..1-SNAPSHOT",
  //scalaVersion := "0.21.0-RC1",
  scalaVersion := "2.13.1",
  scalacOptions := Seq("-deprecation", "-unchecked"),
)

val zioVersion = "1.0.0-RC17"

lazy val libDeps = Def.setting { Seq(
  "dev.zio" % "zio-actors_2.13" % "0.0.3",
  "dev.zio" % "zio_2.13" % zioVersion,
  "dev.zio" %% "zio-test"     % zioVersion % "test",
  "dev.zio" %% "zio-test-sbt" % zioVersion % "test",
  "org.scala-lang.modules" %% "scala-swing" % "2.1.1"
)}


lazy val tetris_zio = (project in file(".")).
  settings(buildSettings: _*).
  settings(
    name := "tetris-zio",
    libraryDependencies ++= libDeps.value,
    testFrameworks ++= Seq(new TestFramework("zio.test.sbt.ZTestFramework")),
    fork in run := true,
  )
