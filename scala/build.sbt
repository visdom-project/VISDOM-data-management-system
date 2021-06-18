scalaVersion := "2.12.13"
name := "data-management-system"
version := "0.2"

val AkkaVersion = "2.6.14"
val AkkaHttpVersion = "10.2.4"
val JavaWsRestApiVersion: String = "2.1.1"
val LoggerVersion: String = "1.8.0-beta4"
val MongoConnectorVersion: String = "3.0.1"
val MongoDriverVersion: String = "4.0.5"
val SparkVersion: String = "3.1.1"
val ScalajVersion: String = "2.4.2"
val ScalaTestVersion: String = "3.2.7"
val ScapeGoatVersion: String = "1.4.8"
val SprayJsonVersion: String = "1.3.6"
val SwaggerAkkaVersion: String = "2.4.2"

libraryDependencies ++= Seq(
    "com.github.swagger-akka-http" %% "swagger-akka-http" % SwaggerAkkaVersion,
    "com.typesafe.akka" %% "akka-actor" % AkkaVersion,
    "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
    "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
    "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
    "io.spray" %%  "spray-json" % SprayJsonVersion,
    "javax.ws.rs" % "javax.ws.rs-api" % JavaWsRestApiVersion,
    "org.apache.spark" %% "spark-core" % SparkVersion % "provided",
    "org.apache.spark" %% "spark-sql" % SparkVersion % "provided",
    "org.mongodb.scala" %% "mongo-scala-driver" % MongoDriverVersion,
    "org.mongodb.spark" %% "mongo-spark-connector" % MongoConnectorVersion,
    "org.scalactic" %% "scalactic" % ScalaTestVersion,
    "org.scalaj" %% "scalaj-http" % ScalajVersion,
    "org.scalatest" %% "scalatest" % ScalaTestVersion % "test",
    "org.scalatest" %% "scalatest-funsuite" % ScalaTestVersion % "test",
    "org.slf4j" % "slf4j-api" % LoggerVersion,
    "org.slf4j" % "slf4j-simple" % LoggerVersion
)

ThisBuild / scapegoatVersion := ScapeGoatVersion

wartremoverErrors ++= Warts.unsafe

scalacOptions ++= Seq("-deprecation", "-feature")

// to get rid of deduplicate errors, from https://stackoverflow.com/a/67937671
ThisBuild / assemblyMergeStrategy := {
    case PathList("module-info.class") => MergeStrategy.discard
    case name if name.endsWith("/module-info.class") => MergeStrategy.discard
    case name => {
        val oldStrategy = (ThisBuild / assemblyMergeStrategy).value
        oldStrategy(name)
    }
}

enablePlugins(JavaAppPackaging)

val MainGitlabFetcher: String = "visdom.fetchers.gitlab.GitlabFetcher"
val MainGitlabAdapter: String = "visdom.adapter.gitlab.Adapter"
val MainDataBroker: String = "visdom.broker.DataBroker"

Global / excludeLintKeys := Set(stage / mainClass)

lazy val GitlabFetcher = (project in file("."))
    .settings(
        Compile / mainClass := Some(MainGitlabFetcher),
        stage / mainClass := Some(MainGitlabFetcher)
    )

lazy val GitlabAdapter = (project in file("."))
    .settings(
        Compile / mainClass := Some(MainGitlabAdapter),
        assembly / mainClass := Some(MainGitlabAdapter)
    )

lazy val DataBroker = (project in file("."))
    .settings(
        Compile / mainClass := Some(MainDataBroker),
        stage / mainClass := Some(MainDataBroker)
    )
