/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import helpers.IntegrationSpecBase
import play.api.http.Status

import java.io.{ByteArrayInputStream, File}
import javax.wsdl.factory.WSDLFactory
import javax.wsdl.xml.WSDLReader
import javax.wsdl.{Operation, PortType}
import scala.io.Source
import scala.jdk.CollectionConverters._
import scala.util.Try
import scala.xml.XML

class AssetsControllerISpec extends IntegrationSpecBase {

  val wsdlBaseUrlV2 = s"http://localhost:$port/assets/eu/outbound/CR-for-NES-Services-V2/"

  val wsdlOperationsForFileNamesV2 = Map(
    "BusinessActivityService/ICS/AEONotificationBAS/V2/CCN2.Service.Customs.EU.ICS.AEONotificationBAS_2.0.0_CCN2_2.0.0.wsdl" -> List(
      "IE4N11notifyAEOControl"
    ),
    "BusinessActivityService/ICS/CRErrorNotificationBAS/V2/CCN2.Service.Customs.EU.ICS.CRErrorNotificationBAS_2.0.0_CCN2_2.0.0.wsdl" -> List(
      "IE4N99notifyError"
    ),
    "BusinessActivityService/ICS/ENSLifecycleManagementBAS/V2/CCN2.Service.Customs.EU.ICS.ENSLifecycleManagementBAS_2.0.0_CCN2_2.0.0.wsdl" -> List(
      "IE4N07notifyArrival",
      "IE4N09notifyControlDecision",
      "IE4N10submitPresentationInformation",
      "IE4S03submitControlResult",
      "IE4Q08revokePresentation"
    ),
    "BusinessActivityService/ICS/ReferralManagementBAS/V2/CCN2.Service.Customs.EU.ICS.ReferralManagementBAS_2.0.0_CCN2_2.0.0.wsdl" -> List(
      "IE4Q04requestAdditionalInformation",
      "IE4R02provideAdditionalInformation",
      "IE4Q05requestHRCM",
      "IE4R03provideHRCMResult"
    ),
    "BusinessActivityService/ICS/RiskAnalysisOrchestrationBAS/V2/CCN2.Service.Customs.EU.ICS.RiskAnalysisOrchestrationBAS_2.0.0_CCN2_2.0.0.wsdl" -> List(
      "IE4N03notifyERiskAnalysisHit",
      "IE4S01submitEScreeningAssessment",
      "IE4S02submitRiskAnalysisResult",
      "IE4S02updateERiskAnalysisResult",
      "IE4S01updateEScreeningResult"
    )
  )

  checkWsdlOperations(wsdlOperationsForFileNamesV2, wsdlBaseUrlV2)

  "all EU Files within public folder" should {
    val baseDirectoryV2    = new File(app.path.getCanonicalPath + s"/public/eu/outbound/CR-for-NES-Services-V2")
    val allFilesFromEuV2   = recursiveListFiles(baseDirectoryV2).filter(_.isFile).filterNot(_.isHidden)

    "have correct amount of WSDLs and XSDs for V2" in {
      countAllWSDLandXSDFiles(allFilesFromEuV2) shouldBe 69
    }

    "not contain {DestinationID} as this should have been replaced for V2" in {
      checkForDestinationIdElement(allFilesFromEuV2) shouldBe false
    }

    s"return ${Status.OK} and parse to xml when using V2 WSDLs and XSDs" when {
      checkAndParseXML(allFilesFromEuV2)
    }
  }

  def checkWsdlOperations(wsdls: Map[String, List[String]], wsdlBaseUrl: String): Unit = {
    wsdls.foreach {
      case (fileName, wsdlOperationList) =>
        s"$wsdlBaseUrl" when {
          s"a request is made for $fileName" should {
            wsdlOperationList.foreach { wsdlOperation =>
              s"include the operation $wsdlOperation" in {
                parseWsdlAndGetOperationsNames(wsdlBaseUrl + fileName) should contain(wsdlOperation)
              }
            }
          }
        }
    }
  }

  def countAllWSDLandXSDFiles(files: Array[File]): Int = {
    files.count(file => file.getName.endsWith(".xsd") || file.getName.endsWith(".wsdl"))
  }

  def checkForDestinationIdElement(files: Array[File]): Boolean = {
    files.exists(_.getName.contains("{DestinationID}"))
  }

  def checkAndParseXML(files: Array[File]): Unit = {
    files.foreach { eachFile =>
      s"file is ${eachFile.getName}" in {
        val pathToFile = eachFile.getCanonicalPath.split("/public/")(1).trim
        val resultOfGettingAsset = await(buildClient(s"/assets/$pathToFile").get())
        resultOfGettingAsset.status shouldBe Status.OK

        val sourceOfFile            = Source.fromFile(eachFile, "UTF-8")
        val byteArrayStreamOfFile   = new ByteArrayInputStream(sourceOfFile.mkString.getBytes("UTF-8"))
        val byeArrayStreamOfBody    = new ByteArrayInputStream(resultOfGettingAsset.body.getBytes("UTF-8"))
        val fileFromDirectoryParsed = Try(XML.load(byteArrayStreamOfFile))

        fileFromDirectoryParsed.get shouldBe XML.load(byeArrayStreamOfBody)

        sourceOfFile.close()
        byteArrayStreamOfFile.close()
        byeArrayStreamOfBody.close()
      }
    }
  }

  def recursiveListFiles(f: File): Array[File] = {
    val these = f.listFiles
    these ++ these.filter(_.isDirectory).flatMap(recursiveListFiles)
  }

  def parseWsdlAndGetOperationsNames(wsdlUrl: String): List[String] = {
    val reader: WSDLReader = WSDLFactory.newInstance().newWSDLReader()
    val wsdlDefinition = reader.readWSDL(wsdlUrl)
    val portType       = wsdlDefinition.getAllPortTypes.asScala.values.head.asInstanceOf[PortType]
    portType.getOperations.asScala.map(_.asInstanceOf[Operation].getName).toList
  }
}
