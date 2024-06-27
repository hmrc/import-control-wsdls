import scoverage.ScoverageKeys
import uk.gov.hmrc.DefaultBuildSettings

val appName = "import-control-wsdls"

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := "2.13.14"

lazy val coverageSettings: Seq[Setting[_]] = {
  Seq(
    ScoverageKeys.coverageExcludedPackages := "<empty>;Reverse.*;models/.data/..*;view.*;models.*;config.*;.*(BuildInfo|Routes).*",
    ScoverageKeys.coverageMinimumStmtTotal := 100,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true
  )
}

lazy val scalaStyleSettings = Seq(scalastyleFailOnError := true)

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin, ScalafmtPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    (Compile / compile) := ((Compile / compile) dependsOn copyTestResources).value,
    PlayKeys.playDefaultPort := 7208
  )
  .settings(coverageSettings: _*)
  .settings(resolvers += Resolver.jcenterRepo)

val testResourcesFolder = file("./it/target/web/public/test/public")
val copyTestResources = taskKey[Unit]("Copy files from /public to /it/target/web/public/test/public directory for tests to access wsdls")

copyTestResources := {
  IO.copyDirectory(baseDirectory.value / "public",  testResourcesFolder)
}

lazy val it = (project in file("it"))
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test") // the "test->test" allows reusing test code and test dependencies
  .settings(
    DefaultBuildSettings.itSettings(),
    libraryDependencies ++= AppDependencies.test,
    Test / unmanagedResourceDirectories += testResourcesFolder / "test",
    unmanagedSourceDirectories := Seq(
      baseDirectory.value / "it"
    ),
    parallelExecution := false,
    fork := true
  )

(Runtime / managedClasspath) += (Assets / packageBin).value

addCommandAlias("runAllChecks", "clean;compile;scalastyle;scalafmtALL;coverage;it/test;coverageReport;dependencyUpdates")

// for all services
scalacOptions += "-Wconf:cat=unused-imports&src=routes/.*:s"