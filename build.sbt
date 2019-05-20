name := "akka-quickstart-scala"

version := "1.0"

scalaVersion := "2.12.2"

lazy val akkaVersion = "2.5.2"

libraryDependencies ++= Seq(
  "com.typesafe.akka"    %% "akka-actor"              % akkaVersion,
  "com.typesafe.akka"    %% "akka-testkit"            % akkaVersion,
  "com.lightbend.akka"   %% "akka-stream-alpakka-csv" % "1.0.1",
  "org.scalatest"        %% "scalatest"               % "3.0.1" % "test",
  "com.github.pathikrit" %% "better-files"            % "3.0.0",
  "com.davegurnell"      %% "unindent"                % "1.1.0" % "test"
)
