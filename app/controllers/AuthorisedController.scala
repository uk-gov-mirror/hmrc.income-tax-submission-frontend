/*
 * Copyright 2020 HM Revenue & Customs
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

package controllers

import config.FrontendAppConfig
import javax.inject.{Inject, Singleton}
import play.api.Logger
import play.api.i18n.I18nSupport
import play.api.mvc._
import services._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AuthorisedController @Inject()(val mcc: MessagesControllerComponents,
                                     val enrolmentsAuthService: EnrolmentsAuthService,
                                     implicit val appConfig: FrontendAppConfig,
                                     implicit val ec: ExecutionContext) extends FrontendController(mcc) with I18nSupport {

  def authorisedAction(block: Request[AnyContent] => Future[Result]): Action[AnyContent] = Action.async {
    implicit request =>
      enrolmentsAuthService
        .authorised
        .retrieve(Retrievals.allEnrolments and Retrievals.affinityGroup) {
          case _ ~ Some(AffinityGroup.Agent) =>
            //Check for agent enrolment, and in future Agent-client relationship
            block(request)
          case enrolments ~ Some(_) =>
            //Check for non-agent enrolment
            block(request)
          case _ =>
            Logger.warn("[AuthorisedController][authorisedAction] - Missing affinity group")
            Future.successful(InternalServerError)
        } recoverWith {
        case _: NoActiveSession => Future.successful(Forbidden(""))
        case _: InsufficientEnrolments =>
          Logger.warn(s"[AuthorisedController][authorisedAction] insufficient enrolment exception encountered")
          Future.successful(Forbidden(""))
        case _: AuthorisationException =>
          Logger.warn(s"[AuthorisedController][authorisedAction] encountered unauthorisation exception")
          Future.successful(Forbidden(""))
      }
  }
}
