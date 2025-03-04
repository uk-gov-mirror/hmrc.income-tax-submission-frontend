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

package views

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.twirl.api.Html
import utils.ViewTest

class AgentAuthErrorPageViewSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite with ViewTest{

  val p1Selector = "#main-content > div > div > p:nth-child(2)"
  val p2Selector = "#main-content > div > div > p:nth-child(3)"
  val authoriseAsAnAgentLinkSelector = "#client_auth_link"

  val pageHeadingText = "There’s a problem"
  val pageTitleText = "There’s a problem"
  val youCannotViewText: String = "You cannot view this client’s information. Your client needs to"
  val authoriseYouAsText = "authorise you as their agent (opens in new tab)"
  val beforeYouCanTryText = "before you can sign in to this service."
  val tryAnotherClientText = "Try another client’s details"
  val authoriseAsAnAgentLink = "https://www.gov.uk/guidance/client-authorisation-an-overview"

  "AgentAuthErrorPageView when called in English" should {
    "render correctly" which {
      lazy val view: Html = agentAuthErrorPageView()(fakeRequest, messages, mockConfig)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      titleCheck(pageTitleText)
      welshToggleCheck("English")
      h1Check(pageHeadingText)
      textOnPageCheck(s"$youCannotViewText $authoriseYouAsText $beforeYouCanTryText", p1Selector)
      linkCheck(authoriseYouAsText, authoriseAsAnAgentLinkSelector, authoriseAsAnAgentLink)
      textOnPageCheck(tryAnotherClientText, p2Selector)
    }
  }

  "AgentAuthErrorPageView when called in Welsh" should {
    "render correctly" which {
      lazy val view: Html = agentAuthErrorPageView()(fakeRequest, welshMessages, mockConfig)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      titleCheck(pageTitleText)
      welshToggleCheck("Welsh")
      h1Check(pageHeadingText)
      textOnPageCheck(s"$youCannotViewText $authoriseYouAsText $beforeYouCanTryText", p1Selector)
      linkCheck(authoriseYouAsText, authoriseAsAnAgentLinkSelector, authoriseAsAnAgentLink)
      textOnPageCheck(tryAnotherClientText, p2Selector)
    }
  }

}
