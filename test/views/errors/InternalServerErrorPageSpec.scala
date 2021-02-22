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

import config.AppConfig
import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.twirl.api.Html
import views.html.errors.InternalServerErrorPage

class InternalServerErrorPageSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {

  object Selectors{
    val pageTitle = "body > header > div > div.govuk-header__content > a"
    val pageHeading = "#main-content > div > div > h1"
    val paragraph = "#main-content > div > div > p"
  }
  val internalServerErrorPage: InternalServerErrorPage = app.injector.instanceOf[InternalServerErrorPage]

  implicit lazy val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", "")
  implicit lazy val messagesApi: MessagesApi = app.injector.instanceOf[MessagesApi]
  implicit lazy val messages: Messages = messagesApi.preferred(fakeRequest)
  implicit lazy val mockConfig: AppConfig = app.injector.instanceOf[AppConfig]

  val pageHeading = "This page can’t be found"
  val pageTitle = "Update and submit an Income Tax Return"
  val paragraph = "Please check that you have entered the correct web address."

  def element(cssSelector: String)(implicit document: Document): Element = {
    val elements = document.select(cssSelector)

    if(elements.size == 0) {
      fail(s"No element exists with the selector '$cssSelector'")
    }

    document.select(cssSelector).first()
  }
  def elementText(selector: String)(implicit document: Document): String = {
    element(selector).text()
  }

  //errors.500.heading = Sorry, there is a problem with the service
  //errors.500.p1 = Try again later.
  //errors.500.p2 = You can also:
  //errors.500.p3 = go to the
  //errors.500.p4 = Income Tax home page (opens in new tab)
  //errors.500.p5 = for more information
  //errors.500.p6 = use
  //errors.500.p7 = Self Assessment: general enquiries (opens in new tab)
  //errors.500.p8 = to speak to someone about your income tax

  "Rendering the error page when there is an error" should {

    lazy val view: Html = internalServerErrorPage()(fakeRequest,messages,mockConfig)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct page title" in {
      elementText(Selectors.pageTitle) shouldBe "Sorry, there is a problem with the service - Update and submit an Income Tax Return - GOV.UK"
    }

    "have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe "Sorry, there is a problem with the service"
    }

    "have the correct paragraph text" in {
      elementText(Selectors.paragraph) shouldBe "Try again later."
    }
  }
}