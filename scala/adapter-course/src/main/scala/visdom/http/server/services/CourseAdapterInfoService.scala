// Copyright 2022 Tampere University
// This software was developed as a part of the VISDOM project: https://iteavisdom.org/
// This source code is licensed under the MIT license. See LICENSE in the repository root directory.
// Author(s): Ville Heikkilä <ville.heikkila@tuni.fi>

package visdom.http.server.services

import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.server.RequestContext
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.RouteResult
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import visdom.http.HttpConstants
import visdom.http.server.CourseAdapterResponseHandler
import visdom.http.server.QueryOptionsBaseObject
import visdom.http.server.ServerConstants
import visdom.http.server.response.CourseAdapterInfoResponse
import visdom.http.server.services.constants.Descriptions
import visdom.http.server.services.constants.Examples
import visdom.utils.WarningConstants.UnusedMethodParameter


@SuppressWarnings(Array(UnusedMethodParameter))
@Path(ServerConstants.InfoRootPath)
class CourseAdapterInfoService(infoActor: ActorRef)(implicit executionContext: ExecutionContext)
extends Directives
with CourseAdapterResponseHandler {
    val route: Route = (
        getInfoRoute
    )

    @GET
    @Produces(Array(MediaType.APPLICATION_JSON))
    @Operation(
        summary = constants.CourseAdapterDescriptions.CourseAdapterInfoEndpointSummary,
        description = constants.CourseAdapterDescriptions.CourseAdapterInfoEndpointDescription,
        responses = Array(
            new ApiResponse(
                responseCode = HttpConstants.StatusOkCode,
                description = Descriptions.InfoStatusOkDescription,
                content = Array(
                    new Content(
                        schema = new Schema(implementation = classOf[CourseAdapterInfoResponse]),
                        examples = Array(
                            new ExampleObject(
                                name = Examples.InfoResponseExampleName,
                                value = constants.CourseAdapterExamples.CourseAdapterInfoResponseExample
                            )
                        )
                    )
                )
            )
        )
    )
    def getInfoRoute: RequestContext => Future[RouteResult] = (
        path(ServerConstants.InfoPath)
    ) {
        get {
            getRoute(infoActor, QueryOptionsBaseObject)
        }
    }
}
