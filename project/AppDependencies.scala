import sbt._

object AppDependencies {
  val playVersion = "play-30"
  val bootstrapVersion = "10.1.0"

  val compile = Seq(
    "uk.gov.hmrc"        %% s"bootstrap-backend-$playVersion"   % bootstrapVersion,
    "wsdl4j"              % "wsdl4j"                            % "1.6.3"
  )

  val test = Seq(
    "uk.gov.hmrc"        %% s"bootstrap-test-$playVersion"      % bootstrapVersion % Test
  )
}
