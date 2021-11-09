import play.core.PlayVersion.current
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  val compile = Seq[ModuleID](
    "uk.gov.hmrc" %% "bootstrap-backend-play-28" % "5.16.0",
    "org.apache.axis2" % "axis2-kernel" % "1.8.0"
  )

  val test = Seq[ModuleID](
    "uk.gov.hmrc" %% "bootstrap-test-play-28" % "5.16.0" % Test,
    "org.scalatest" %% "scalatest" % "3.2.5" % "test, it",
    "com.typesafe.play" %% "play-test" % current % Test,
    "com.vladsch.flexmark" % "flexmark-all" % "0.36.8" % "test, it",
    "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % "test, it",
    "com.github.tomakehurst" % "wiremock-jre8-standalone" % "2.31.0" % "test, it"
  )
}
