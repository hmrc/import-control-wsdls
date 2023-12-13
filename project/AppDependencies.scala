import sbt._

object AppDependencies {

  lazy val bootstrapVersion = "8.2.0"

  val compile = Seq(
    "uk.gov.hmrc"        %% "bootstrap-backend-play-30"   % bootstrapVersion,
    "org.apache.axis2" % "axis2-kernel"               % "1.8.2"
  )

  val test = Seq(
    "uk.gov.hmrc"            %% "bootstrap-test-play-30"      % bootstrapVersion % Test
  )
}
