name := "app-version-detecter"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache
)     

libraryDependencies += "com.typesafe" %% "play-plugins-mailer" % "2.2.0"

play.Project.playScalaSettings
