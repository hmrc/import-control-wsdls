import sbt._

object AppDependencies {

  lazy val bootstrapVersion = "8.4.0"

  val compile = Seq(
    "uk.gov.hmrc"        %% "bootstrap-backend-play-30"   % bootstrapVersion,
    "wsdl4j"              % "wsdl4j"                      % "1.6.3"
  )

  val test = Seq(
    "uk.gov.hmrc"            %% "bootstrap-test-play-30"      % bootstrapVersion % Test,
    "com.vladsch.flexmark"   % "flexmark-profile-pegdown"     % "0.64.8"         % "test, it",
    "org.scalatestplus.play" %% "scalatestplus-play"          % "7.0.1"          % "test, it",
    "org.wiremock"            % "wiremock-standalone"         % "3.3.1"           % "test, it"
  )
}
