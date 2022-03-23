/*
 * Copyright 2022 HM Revenue & Customs
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

import java.io.{ByteArrayInputStream, File}

import helpers.IntegrationSpecBase
import javax.wsdl.xml.WSDLReader
import javax.wsdl.{Operation, PortType}
import org.apache.axis2.wsdl.WSDLUtil
import play.api.http.Status

import scala.collection.JavaConverters._
import scala.io.Source
import scala.util.Try
import scala.xml.XML

class AssetsControllerISpec extends IntegrationSpecBase {

  val wsdlBaseUrlV1 = s"http://localhost:$port/assets/eu/outbound/CR-for-NES-Services/"
  val wsdlBaseUrlV2 = s"http://localhost:$port/assets/eu/outbound/CR-for-NES-Services-V2/"

  val wsdlOperationsForFileNamesV1 = Map(
    "BusinessActivityService/ICS/ReferralManagementBAS/V1/CCN2.Service.Customs.Default.ICS.ReferralManagementBAS_1.0.0_CCN2_1.0.0.wsdl" -> List(
      "IE4Q04requestAdditionalInformation",
      "IE4R02provideAdditionalInformation",
      "IE4Q05requestHRCM",
      "IE4R03provideHRCMResult"
    ),
    "BusinessActivityService/ICS/RiskAnalysisOrchestrationBAS/V1/CCN2.Service.Customs.Default.ICS.RiskAnalysisOrchestrationBAS_1.0.0_CCN2_1.0.0.wsdl" -> List(
      "IE4N03notifyERiskAnalysisHit",
      "IE4S01submitEScreeningAssessment",
      "IE4S02submitRiskAnalysisResult",
      "IE4S02updateERiskAnalysisResult",
      "IE4S01updateEScreeningResult"
    ),
    "BusinessActivityService/ICS/ReferralManagementBAS/V1/CCN2.Service.Customs.Default.ICS.ReferralManagementBAS_1.0.0_CCN2_1.0.0.wsdl" -> List(
      "IE4Q04requestAdditionalInformation",
      "IE4R02provideAdditionalInformation",
      "IE4Q05requestHRCM",
      "IE4R03provideHRCMResult"
    ),
    "BusinessActivityService/ICS/AEONotificationBAS/V1/CCN2.Service.Customs.Default.ICS.AEONotificationBAS_1.0.0_CCN2_1.0.0.wsdl" -> List(
      "IE4N11notifyAEOControl"
    ),
    "BusinessActivityService/ICS/ENSLifecycleManagementBAS/V1/CCN2.Service.Customs.Default.ICS.ENSLifecycleManagementBAS_1.0.0_CCN2_1.0.0.wsdl" -> List(
      "IE4S03submitControlResult",
      "IE4N10submitPresentationInformation",
      "IE4Q08revokePresentation"
    )
  )

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

  checkWsdlOperations(wsdlOperationsForFileNamesV1, wsdlBaseUrlV1)
  checkWsdlOperations(wsdlOperationsForFileNamesV2, wsdlBaseUrlV2)

  "all EU Files within public folder" should {
    val baseDirectoryV1    = new File(app.path.getCanonicalPath + s"/public/eu/outbound/CR-for-NES-Services")
    val baseDirectoryV2    = new File(app.path.getCanonicalPath + s"/public/eu/outbound/CR-for-NES-Services-V2")
    val allFilesFromEuV1   = recursiveListFiles(baseDirectoryV1).filter(_.isFile).filterNot(_.isHidden)
    val allFilesFromEuV2   = recursiveListFiles(baseDirectoryV2).filter(_.isFile).filterNot(_.isHidden)

    "have correct amount of WSDLs and XSDs for V1" in {
      countAllWSDLandXSDFiles(allFilesFromEuV1) shouldBe 65
    }

    "have correct amount of WSDLs and XSDs for V2" in {
      countAllWSDLandXSDFiles(allFilesFromEuV2) shouldBe 69
    }

    "not contain {DestinationID} as this should have been replaced for V1" in {
      checkForDestinationIdElement(allFilesFromEuV1) shouldBe false
    }

    "not contain {DestinationID} as this should have been replaced for V2" in {
      checkForDestinationIdElement(allFilesFromEuV2) shouldBe false
    }

    s"return ${Status.OK} and parse to xml when using V1" when {
      allFilesFromEuV1.foreach { eachFile =>
        s"file is ${eachFile.getName}" in {
          val pathToFile           = eachFile.getCanonicalPath.split("/CR-for-NES-Services/")(1).trim
          val resultOfGettingAsset = await(buildClient(s"/assets/eu/outbound/CR-for-NES-Services/$pathToFile").get())
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

    s"return ${Status.OK} and parse to xml when using V2" when {
      allFilesFromEuV2.foreach { eachFile =>
        s"file is ${eachFile.getName}" in {
          val pathToFile           = eachFile.getCanonicalPath.split("/CR-for-NES-Services-V2/")(1).trim
          val resultOfGettingAsset = await(buildClient(s"/assets/eu/outbound/CR-for-NES-Services-V2/$pathToFile").get())
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

  def recursiveListFiles(f: File): Array[File] = {
    val these = f.listFiles
    these ++ these.filter(_.isDirectory).flatMap(recursiveListFiles)
  }

  def parseWsdlAndGetOperationsNames(wsdlUrl: String): List[String] = {
    val reader: WSDLReader = WSDLUtil.newWSDLReaderWithPopulatedExtensionRegistry
    reader.setFeature("javax.wsdl.importDocuments", true)
    val wsdlDefinition = reader.readWSDL(wsdlUrl)
    val portType       = wsdlDefinition.getAllPortTypes.asScala.values.head.asInstanceOf[PortType]
    portType.getOperations.asScala.map(_.asInstanceOf[Operation].getName).toList
  }
}
