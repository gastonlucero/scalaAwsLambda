version := "1.0-SNAPSHOT"

scalaVersion := "2.12.0"

javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")


lazy val root = (project in file("."))
  .settings(
    name := "awsLambdaScala",
    retrieveManaged := true,
    libraryDependencies ++= dependencies,
    artifact in (Compile, assembly) := {
      val art = (artifact in (Compile, assembly)).value
      art.copy(`classifier` = Some("assbt assemblysembly"))
    }
  )
  .settings(addArtifact(artifact in(Compile, assembly), assembly).settings: _*)

lazy val dependencies = Seq(
  "com.amazonaws" % "aws-lambda-java-core" % "1.1.0",
  "com.amazonaws" % "aws-lambda-java-events" % "1.3.0",
  "com.amazonaws" % "aws-lambda-java-log4j" % "1.0.0",
  "com.typesafe" % "config" % "1.3.1",
  "org.scalatest" % "scalatest_2.12" % "3.0.1" % Test
)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}

test in assembly := {}