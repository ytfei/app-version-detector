name := "app-version-detector"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache
)     

libraryDependencies += "com.typesafe" %% "play-plugins-mailer" % "2.2.0"

libraryDependencies += "org.antlr" % "antlr4-runtime" % "4.3"

play.Project.playScalaSettings
