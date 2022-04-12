import sbt._

object AppDependencies {

  val compile = Seq(
    "uk.gov.hmrc"      %% "bootstrap-backend-play-28" % "5.22.0",
    "org.apache.axis2" % "axis2-kernel"               % "1.8.0"
  )

  val test = Seq(
    "uk.gov.hmrc"            %% "bootstrap-test-play-28"  % "5.22.0" % Test,
    "com.vladsch.flexmark"   % "flexmark-all"             % "0.36.8" % "test, it",
    "org.scalatestplus.play" %% "scalatestplus-play"      % "5.1.0"  % "test, it",
    "com.github.tomakehurst" % "wiremock-jre8-standalone" % "2.32.0" % "test, it"
  )
}
