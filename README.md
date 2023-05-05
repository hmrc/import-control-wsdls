
# import-control-wsdls

This service is used to get files for ICS2.


## Development Setup
- Run locally: `sbt run` which runs on port `7208` by default

## Tests

- Run Integration Tests: `sbt it:test`
- Run Integration Tests with coverage report: `sbt runAllChecks`<br/> which runs `clean compile scalastyle coverage it:test coverageReport`


## GET /assets/eu/outbound/CR-for-NES-Services/:fileNameAndPath
## GET /assets/eu/outbound/CR-for-NES-Services-V2/:fileNameAndPath


Where 
 - `:fileNameAndPath` could be  any file within the `CR-for-NES-Services` and `CR-for-NES-Services-V2` file structures in the `public` folder
 
With some common top level files referenced below for V1 and V2 to be used in the `import-control-outbound-proxy` microservice

V1:
- BusinessActivityService/ICS/ReferralManagementBAS/V1/CCN2.Service.Customs.Default.ICS.ReferralManagementBAS_1.0.0_1.0.0.wsdl
- BusinessActivityService/ICS/ReferralManagementBAS/V1CCN2.Service.Customs.Default.ICS.ReferralManagementBAS_1.0.0_CCN2_1.0.0.wsdl
- BusinessActivityService/ICS/RiskAnalysisOrchestrationBAS/V1/CCN2.Service.Customs.Default.ICS.RiskAnalysisOrchestrationBAS_1.0.0_1.0.0.wsdl
- BusinessActivityService/ICS/RiskAnalysisOrchestrationBAS/V1/CCN2.Service.Customs.Default.ICS.RiskAnalysisOrchestrationBAS_1.0.0_CCN2_1.0.0.wsdl
- Policies/CCN2/CCN2.Service.Platform.SecurityPolicies.wsdl

V2:
- BusinessActivityService/ICS/AEONotificationBAS/V2/CCN2.Service.Customs.EU.ICS.AEONotificationBAS_2.0.0_CCN2_2.0.0.wsdl
- BusinessActivityService/ICS/CRErrorNotificationBAS/V2/CCN2.Service.Customs.EU.ICS.CRErrorNotificationBAS_2.0.0_CCN2_2.0.0.wsdl
- BusinessActivityService/ICS/ENSLifecycleManagementBAS/V2/CCN2.Service.Customs.EU.ICS.ENSLifecycleManagementBAS_2.0.0_CCN2_2.0.0.wsdl
- BusinessActivityService/ICS/ReferralManagementBAS/V2/CCN2.Service.Customs.EU.ICS.ReferralManagementBAS_2.0.0_CCN2_2.0.0.wsdl
- BusinessActivityService/ICS/RiskAnalysisOrchestrationBAS/V2/CCN2.Service.Customs.EU.ICS.RiskAnalysisOrchestrationBAS_2.0.0_CCN2_2.0.0.wsdl

### Success Response
HTTP Status code of 200 is returned with the file when `:fileNameAndPath` exists.

### Error Responses
| Scenario | HTTP Status |
| --- | --- |
| `:fileNameAndPath` not recognised | `404` |

### Notes for Developer updating the files to the latest version

- Previous Version of EU requirements `V1.24`, Current Version of EU requirements `V2.21` (March 2022)
- The folder structure was directly copied from the EU documentation for both V1 and V2
- Renamed the top level folders be to `CR-for-NES-Services` and `CR-for-NES-Services-V2`
- We replaced `{DestinationID}` in all filenames that contained it with `EU.CR`
- We replaced http to https in `soap12:address location` and `wsa:Address` tags for wsdl's that contain "\_CCN2_\" within their filename e.g. CCN2.Service.Customs.EU.ICS.AEONotificationBAS_2.0.0_CCN2_2.0.0.wsdl. All other URLs that use http within those files are to remain unchanged. 


### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
