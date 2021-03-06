// Copyright 2022 Tampere University
// This software was developed as a part of the VISDOM project: https://iteavisdom.org/
// This source code is licensed under the MIT license. See LICENSE in the repository root directory.
// Author(s): Ville Heikkilä <ville.heikkila@tuni.fi>

package visdom.fetchers.gitlab.queries.info

import akka.actor.Actor
import akka.actor.ActorLogging
import scala.concurrent.ExecutionContext
import visdom.database.mongodb.MongoConnection
import visdom.fetchers.gitlab.GitlabConstants
import visdom.fetchers.gitlab.GitlabFetcher
import visdom.fetchers.gitlab.Routes
import visdom.http.server.QueryOptionsBaseObject
import visdom.http.server.response.GitlabFetcherInfoResponse
import visdom.utils.WartRemoverConstants


class InfoActor extends Actor with ActorLogging {
    implicit val ec: ExecutionContext = ExecutionContext.global

    @SuppressWarnings(Array(WartRemoverConstants.WartsAny))
    def receive: Receive = {
        case QueryOptionsBaseObject => {
            log.info("Received info query")
            val response: GitlabFetcherInfoResponse = GitlabFetcherInfoResponse(
                componentName = MongoConnection.applicationName,
                componentType = GitlabConstants.ComponentType,
                fetcherType = GitlabConstants.FetcherType,
                version = GitlabConstants.FetcherVersion,
                gitlabServer = Routes.server.hostName,
                mongoDatabase = Routes.databaseName,
                startTime = GitlabFetcher.StartTime,
                apiAddress = Routes.fullApiAddress,
                swaggerDefinition = visdom.fetchers.gitlab.Routes.SwaggerLocation
            )
            sender() ! response
        }
    }
}
