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

package config

import javax.inject.{Inject, Singleton}
import play.api.Configuration
import uk.gov.hmrc.play.bootstrap.binders.SafeRedirectUrl
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

trait AppConfig {
  val signInContinueUrl: String
  val signInUrl: String

  val incomeTaxSubmissionBaseUrl: String
  val incomeTaxSubmissionUrl: String
  val personalIncomeTaxSubmissionBaseUrl: String
  val personalIncomeTaxSubmissionUrl: String
  def personalIncomeTaxDividendsUrl(taxYear: Int): String
  def personalIncomeTaxInterestUrl(taxYear: Int): String
  def viewAndChangeCalculationUrl(taxYear: Int): String
}

@Singleton
class FrontendAppConfig @Inject()(config: Configuration, servicesConfig: ServicesConfig) extends AppConfig {

  private val signInBaseUrl: String = config.get[String](ConfigKeys.signInUrl)
  lazy val defaultTaxYear: Int = config.get[Int](ConfigKeys.defaultTaxYear)
  private val signInContinueBaseUrl: String = config.get[String](ConfigKeys.signInContinueBaseUrl)
  override val signInContinueUrl: String = SafeRedirectUrl(signInContinueBaseUrl).encodedUrl //TODO add redirect to overview page
  private val signInOrigin = servicesConfig.getString("appName")
  override val signInUrl: String = s"$signInBaseUrl?continue=$signInContinueUrl&origin=$signInOrigin"

  lazy val incomeTaxSubmissionBaseUrl: String = servicesConfig.baseUrl("income-tax-submission")
  lazy val incomeTaxSubmissionUrl: String = s"$incomeTaxSubmissionBaseUrl/income-tax-submission-service/income-tax"
  lazy val personalIncomeTaxSubmissionBaseUrl: String = config.get[String](ConfigKeys.personalIncomeBaseUrl)
  lazy val personalIncomeTaxSubmissionUrl: String =s"$personalIncomeTaxSubmissionBaseUrl/income-through-software/return/personal-income"
  def personalIncomeTaxDividendsUrl(taxYear: Int): String = s"$personalIncomeTaxSubmissionUrl/$taxYear/dividends/uk-dividends"
  def personalIncomeTaxDividendsSubmissionCYAUrl(taxYear: Int): String = s"$personalIncomeTaxSubmissionUrl/$taxYear/dividends/check-your-answers"
  def personalIncomeTaxInterestSubmissionCYAUrl(taxYear: Int): String = s"$personalIncomeTaxSubmissionUrl/$taxYear/interest/check-your-answers"
  def personalIncomeTaxInterestUrl(taxYear: Int): String = s"$personalIncomeTaxSubmissionUrl/$taxYear/interest/untaxed-uk-interest"
  private val vcBaseUrl: String = config.get[String](ConfigKeys.viewAndChangeBaseUrl)
  def viewAndChangeCalculationUrl(taxYear: Int): String = s"$vcBaseUrl/report-quarterly/income-and-expenses/view/calculation/$taxYear/submitted"

}
