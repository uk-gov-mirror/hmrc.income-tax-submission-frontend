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

package views.errors

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.twirl.api.Html
import utils.ViewTest
import views.html.errors.ServiceUnavailablePage

class ServiceUnavailableErrorPageSpec extends AnyWordSpec with Matchers with ViewTest{

  val youWillBeAbleSelector = "#main-content > div > div > div.govuk-body > p:nth-child(1)"
  val youCanAlsoSelector = "#main-content > div > div > div.govuk-body > p:nth-child(2)"
  val goToTheSelector = "#main-content > div > div > ul > li:nth-child(1)"
  val useSelfAssesSelector = "#main-content > div > div > ul > li:nth-child(2)"
  val link = "#govuk-income-tax-link"
  val link2 = "#govuk-self-assessment-link"

  val pageTitleText = "Sorry, the service is unavailable"
  val pageHeadingText = "Sorry, the service is unavailable"
  val youWillBeAbleText = "You will be able to use the service later."
  val youCanAlsoText = "You can also:"
  val goToTheText = "go to the Income Tax home page (opens in new tab) for more information"
  val useSelfAssesText = "use Self Assessment: general enquiries (opens in new tab) to speak to someone about your income tax"
  val link1Text = "Income Tax home page (opens in new tab)"
  val link2Text = "Self Assessment: general enquiries (opens in new tab)"

  val link1Href = "https://www.gov.uk/income-tax"
  val link2Href = "https://www.gov.uk/government/organisations/hm-revenue-customs/contact/self-assessment"

  val serviceUnavailablePage: ServiceUnavailablePage = app.injector.instanceOf[ServiceUnavailablePage]

  "The ServiceUnavailableErrorPage when called in English" should {

    "render correctly" should {

      lazy val view: Html = serviceUnavailablePage()(fakeRequest, messages, mockConfig)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      titleCheck(pageTitleText)
      welshToggleCheck("English")
      h1Check(pageHeadingText)
      linkCheck(link1Text, link, link1Href)
      linkCheck(link2Text, link2, link2Href)
      textOnPageCheck(youWillBeAbleText, youWillBeAbleSelector)
      textOnPageCheck(youCanAlsoText, youCanAlsoSelector)
      textOnPageCheck(goToTheText, goToTheSelector)
      textOnPageCheck(useSelfAssesText, useSelfAssesSelector)
    }
  }

  "The ServiceUnavailableErrorPage when called in Welsh" should {

    "render correctly" should {

      lazy val view: Html = serviceUnavailablePage()(fakeRequest, welshMessages, mockConfig)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      titleCheck(pageTitleText)
      welshToggleCheck("Welsh")
      h1Check(pageHeadingText)
      linkCheck(link1Text, link, link1Href)
      linkCheck(link2Text, link2, link2Href)
      textOnPageCheck(youWillBeAbleText, youWillBeAbleSelector)
      textOnPageCheck(youCanAlsoText, youCanAlsoSelector)
      textOnPageCheck(goToTheText, goToTheSelector)
      textOnPageCheck(useSelfAssesText, useSelfAssesSelector)
    }
  }
}