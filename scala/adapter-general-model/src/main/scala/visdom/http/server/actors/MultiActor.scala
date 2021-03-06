// Copyright 2022 Tampere University
// This software was developed as a part of the VISDOM project: https://iteavisdom.org/
// This source code is licensed under the MIT license. See LICENSE in the repository root directory.
// Author(s): Ville Heikkilä <ville.heikkila@tuni.fi>

package visdom.http.server.actors

import akka.actor.Actor
import akka.actor.ActorLogging
import spray.json.JsObject
import spray.json.JsString
import visdom.adapters.general.AdapterValues
import visdom.adapters.general.usecases.MultiQuery
import visdom.adapters.queries.IncludesQueryCode
import visdom.adapters.results.BaseResultValue
import visdom.http.HttpConstants
import visdom.http.server.ResponseUtils
import visdom.http.server.options.BaseMultiInputOptions
import visdom.http.server.response.BaseResponse
import visdom.http.server.response.JsonResponse
import visdom.utils.QueryUtils
import visdom.utils.WartRemoverConstants


class MultiActor extends Actor with ActorLogging {
    val multiQueryClass: Class[_ <: MultiQuery] = classOf[MultiQuery]
    val multiQueryObject: IncludesQueryCode = MultiQuery
    val queryUtils: QueryUtils = AdapterValues.queryUtils

    @SuppressWarnings(Array(WartRemoverConstants.WartsAny))
    def receive: Receive = {
        case inputOptions: BaseMultiInputOptions => {
            log.info(s"Received multi query with options: ${inputOptions.toString()}")

            val response: BaseResponse = {
                (
                    queryUtils.runCacheResultQuery(
                        multiQueryObject.queryCode,
                        multiQueryClass,
                        inputOptions.toQueryOptions(),
                        10 * HttpConstants.DefaultWaitDurationSeconds
                    )
                ) match {
                    case Right(result: BaseResultValue) => result.toJsValue() match {
                        case jsObject: JsObject => JsonResponse(jsObject)
                        case message: JsString => ResponseUtils.getNotFoundResponse(message.value)
                        case _ => ResponseUtils.getNotFoundResponse("No results found")
                    }
                    case Left(errorValue: String) => ResponseUtils.getErrorResponse(errorValue)
                }
            }

            sender() ! response
        }
    }
}
