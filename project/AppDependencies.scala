import sbt._

object AppDependencies {

  lazy val bootstrapVersion = "8.6.0"

  val compile = Seq(
    "uk.gov.hmrc"        %% "bootstrap-backend-play-30"   % bootstrapVersion,
    "wsdl4j"              % "wsdl4j"                      % "1.6.3"
  )

  val test = Seq(
    "uk.gov.hmrc"            %% "bootstrap-test-play-30"      % bootstrapVersion % Test
  )
}
