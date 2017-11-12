name := """memo"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.3"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-slick" % "3.0.2",
  "com.typesafe.slick" %% "slick-codegen" % "3.2.1",
  "mysql" % "mysql-connector-java" % "5.1.42"
)


// Temporary fix https://github.com/playframework/playframework/issues/7832#issuecomment-336014319
val akkaVersion = "2.5.6"
dependencyOverrides ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.google.guava" % "guava" % "22.0",
  "org.slf4j" % "slf4j-api" % "1.7.25"
)

// Workaround https://github.com/sbt/sbt/issues/1636#issuecomment-57498141
// evictionWarningOptions in update := EvictionWarningOptions.default
//   .withWarnTransitiveEvictions(false)
//   .withWarnDirectEvictions(false)
//   .withWarnScalaVersionEviction(false)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
