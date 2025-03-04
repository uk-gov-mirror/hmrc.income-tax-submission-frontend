/*
 * Copyright 2021 HM Revenue & Customs
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

package helpers

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.Status.OK
import play.api.libs.json.Json.JsValueWrapper
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.auth.core.{AffinityGroup, ConfidenceLevel}

trait WireMockHelper {

  val wiremockPort = 11111
  val wiremockHost = "localhost"

  lazy val wmConfig: WireMockConfiguration = wireMockConfig().port(wiremockPort)
  lazy val wireMockServer = new WireMockServer(wmConfig)

  private val authoriseUri = "/auth/authorise"

  private val mtditEnrolment = Json.obj(
    "key" -> "HMRC-MTD-IT",
    "identifiers" -> Json.arr(
      Json.obj(
        "key" -> "MTDITID",
        "value" -> "1234567890"
      )
    )
  )

  private val ninoEnrolment = Json.obj(
    "key" -> "HMRC-NI",
    "identifiers" -> Json.arr(
      Json.obj(
        "key" -> "NINO",
        "value" -> "AA123456A"
      )
    )
  )

  private def successfulAuthResponse(affinityGroup: Option[AffinityGroup], confidenceLevel: Option[ConfidenceLevel], enrolments: JsObject*): JsObject = {
      affinityGroup.fold(Json.obj())(unwrappedAffinityGroup => Json.obj("affinityGroup" -> unwrappedAffinityGroup)) ++
        confidenceLevel.fold(Json.obj())(unwrappedConfidenceLevel => Json.obj("confidenceLevel" -> unwrappedConfidenceLevel)) ++
        Json.obj("allEnrolments" -> enrolments)
  }

  def startWiremock(): Unit = {
    wireMockServer.start()
    WireMock.configureFor(wiremockHost, wiremockPort)
  }

  def stopWiremock(): Unit = wireMockServer.stop()

  def resetWiremock(): Unit = WireMock.reset()

  def verifyPost(uri: String, optBody: Option[String] = None): Unit = {
    val uriMapping = postRequestedFor(urlEqualTo(uri))
    val postRequest = optBody match {
      case Some(body) => uriMapping.withRequestBody(equalTo(body))
      case None => uriMapping
    }
    verify(postRequest)
  }

  def verifyGet(uri: String): Unit = {
    verify(getRequestedFor(urlEqualTo(uri)))
  }

  def stubGet(url: String, status: Integer, body: String): StubMapping =
    stubFor(get(urlMatching(url))
      .willReturn(
        aResponse().
          withStatus(status).
          withBody(body)
      )
    )

  def stubGetWithHeaderCheck(url: String, status: Integer, body: String, header:(String, String)): StubMapping =
    stubFor(get(urlMatching(url))
      .withHeader(header._1,equalTo(header._2))
      .willReturn(
        aResponse().
          withStatus(status).
          withBody(body)
      )
    )

  def stubPost(url: String, status: Integer, responseBody: String): StubMapping =
    stubFor(post(urlMatching(url))
      .willReturn(
        aResponse().
          withStatus(status).
          withBody(responseBody)
      )
    )

  def stubPut(url: String, status: Integer, responseBody: String): StubMapping =
    stubFor(put(urlMatching(url))
      .willReturn(
        aResponse().
          withStatus(status).
          withBody(responseBody)
      )
    )

  def stubPatch(url: String, status: Integer, responseBody: String): StubMapping =
    stubFor(patch(urlMatching(url))
      .willReturn(
        aResponse().
          withStatus(status).
          withBody(responseBody)
      )
    )

  def stubDelete(url: String, status: Integer, responseBody: String): StubMapping =
    stubFor(delete(urlMatching(url))
      .willReturn(
        aResponse().
          withStatus(status).
          withBody(responseBody)
      )
    )

  def authoriseIndividual(withNino: Boolean = true): StubMapping = {
    stubPost(authoriseUri, OK, Json.prettyPrint(successfulAuthResponse(Some(AffinityGroup.Individual), None, Seq.empty[JsObject]: _*)))
    stubPost(authoriseUri, OK, Json.prettyPrint(successfulAuthResponse(None, Some(ConfidenceLevel.L200), Seq(mtditEnrolment, ninoEnrolment): _*)))
  }

  def unauthorisedIndividualInsufficientConfidenceLevel(withNino: Boolean = true): StubMapping = {
    stubPost(authoriseUri, OK, Json.prettyPrint(successfulAuthResponse(Some(AffinityGroup.Individual), None, Seq.empty[JsObject]: _*)))
    stubPost(authoriseUri, OK, Json.prettyPrint(successfulAuthResponse(None, Some(ConfidenceLevel.L50), Seq(mtditEnrolment, ninoEnrolment): _*)))
  }

  def unauthorisedIndividualWrongCredentials(withNino: Boolean = true): StubMapping = {
    stubPost(authoriseUri, OK, Json.prettyPrint(successfulAuthResponse(Some(AffinityGroup.Individual), None, Seq.empty[JsObject]: _*)))
    stubPost(authoriseUri, OK, Json.prettyPrint(successfulAuthResponse(None, Some(ConfidenceLevel.L200), Seq.empty[JsObject]: _*)))
  }

}
