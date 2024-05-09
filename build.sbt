import scoverage.ScoverageKeys
import uk.gov.hmrc.DefaultBuildSettings

val appName = "import-control-wsdls"

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := "2.13.12"

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
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
//    libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always,
//    scalacOptions += "-Wconf:src=routes/.*:s",
//    scalacOptions += "-Wconf:cat=unused-imports&src=html/.*:s",
    Compile / unmanagedResourceDirectories += baseDirectory.value / "public",
    PlayKeys.playDefaultPort := 7208
  )
  .settings(coverageSettings: _*)
//  .configs(IntegrationTest)
//  .settings(integrationTestSettings(): _*)
  .settings(resolvers += Resolver.jcenterRepo)
//  .settings((IntegrationTest / managedClasspath) += (Assets / packageBin).value)

lazy val it = (project in file("it"))
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test") // the "test->test" allows reusing test code and test dependencies
  .settings(
    DefaultBuildSettings.itSettings(),
    libraryDependencies ++= AppDependencies.test,
    Compile / unmanagedResourceDirectories += baseDirectory.value / "public",
    unmanagedSourceDirectories := Seq(
      baseDirectory.value / "it"
    ),
    unmanagedResourceDirectories := Seq(
      baseDirectory.value / "public"
    ),
//   Runtime / managedClasspath += (Assets / packageBin).value,
    parallelExecution := false,
    fork := true
  )

(Runtime / managedClasspath) += (Assets / packageBin).value

addCommandAlias("runAllChecks", "clean;compile;scalastyle;coverage;it/test;coverageReport")

// for all services
scalacOptions += "-Wconf:cat=unused-imports&src=routes/.*:s"

