import sbt._

object AppDependencies {

  lazy val bootstrapVersion = "7.19.0"

  val compile = Seq(
    "uk.gov.hmrc"      %% "bootstrap-backend-play-28" % bootstrapVersion,
    "org.apache.axis2" % "axis2-kernel"               % "1.8.2"
  )

  val test = Seq(
    "uk.gov.hmrc"            %% "bootstrap-test-play-28"      % bootstrapVersion % Test,
    "org.scalatest"            %% "scalatest"                 % "3.2.16"         % "test, it",
    "com.vladsch.flexmark"      % "flexmark-profile-pegdown"  % "0.64.8"         % "test, it",
    "org.scalatestplus.play" %% "scalatestplus-play"          % "5.1.0"          % "test, it",
    "com.github.tomakehurst" % "wiremock-jre8-standalone"     % "2.35.0"         % "test, it"
  )
}

