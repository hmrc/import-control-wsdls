import scoverage.ScoverageKeys
import uk.gov.hmrc.DefaultBuildSettings.{addTestReportOption, itSettings}

val appName = "import-control-wsdls"

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := "2.13.14"

lazy val ItTest = config("it") extend Test

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
  .settings(
    (Test / parallelExecution) := false,
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    Compile / unmanagedResourceDirectories += baseDirectory.value / "public",
    PlayKeys.playDefaultPort := 7208
  )
  .settings(coverageSettings: _*)
  .configs(ItTest)
  .settings(itSettings(): _*)
  .settings(
    ItTest / fork := true,
    ItTest / unmanagedSourceDirectories := Seq((ItTest / baseDirectory).value / "it"),
    ItTest / unmanagedResourceDirectories := Seq((ItTest / baseDirectory).value / "public"),
    ItTest / javaOptions += "-Dlogger.resource=logback-test.xml",
    ItTest / parallelExecution := false,
    addTestReportOption(ItTest, "int-test-reports")
  )
  .settings(resolvers += Resolver.jcenterRepo)
  .settings((ItTest / managedClasspath) += (Assets / packageBin).value)

addCommandAlias("runAllChecks", "clean;compile;scalastyle;coverage;IntegrationTest/test;coverageReport")

// for all services
scalacOptions += "-Wconf:cat=unused-imports&src=routes/.*:s"

