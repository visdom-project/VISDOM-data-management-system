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
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
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
import spray.json.JsObject
import visdom.http.HttpConstants
import visdom.http.server.ServerConstants
import visdom.http.server.options.OnlyPageInputOptions
import visdom.http.server.options.TestOptions
import visdom.http.server.response.ResponseProblem
import visdom.http.server.services.constants.GeneralAdapterConstants
import visdom.http.server.services.constants.GeneralAdapterDescriptions
import visdom.http.server.services.constants.GeneralAdapterExamples
import visdom.utils.WarningConstants


// scalastyle:off method.length
@SuppressWarnings(Array(WarningConstants.UnusedMethodParameter))
@Path(ServerConstants.TestRootPath)
class TestService(actorRef: ActorRef)(implicit executionContext: ExecutionContext)
extends Directives
with AdapterService
{
    val route: Route = (
        getServiceRoute
    )

    @GET
    @Produces(Array(MediaType.APPLICATION_JSON))
    @Operation(
        summary = GeneralAdapterDescriptions.TestEndpointSummary,
        description = GeneralAdapterDescriptions.TestEndpointDescription,
        parameters = Array(
            new Parameter(
                name = GeneralAdapterConstants.Page,
                in = ParameterIn.QUERY,
                required = false,
                description = GeneralAdapterDescriptions.DescriptionPage,
                schema = new Schema(
                    implementation = classOf[String],
                    defaultValue = GeneralAdapterConstants.DefaultPage
                )
            ),
            new Parameter(
                name = GeneralAdapterConstants.PageSize,
                in = ParameterIn.QUERY,
                required = false,
                description = GeneralAdapterDescriptions.DescriptionPageSize,
                schema = new Schema(
                    implementation = classOf[String],
                    defaultValue = GeneralAdapterConstants.DefaultPageSize
                )
            ),
            new Parameter(
                name = GeneralAdapterConstants.Target,
                in = ParameterIn.QUERY,
                required = true,
                description = GeneralAdapterDescriptions.DescriptionTarget,
                schema = new Schema(
                    implementation = classOf[String],
                    defaultValue = GeneralAdapterConstants.DefaultTarget,
                    allowableValues = Array(
                        GeneralAdapterConstants.DefaultTarget,
                        GeneralAdapterConstants.ValidTargetOrigin,
                        GeneralAdapterConstants.ValidTargetAuthor,
                        GeneralAdapterConstants.ValidTargetArtifact
                    )
                )
            ),
            new Parameter(
                name = GeneralAdapterConstants.PrivateToken,
                in = ParameterIn.HEADER,
                required = false,
                description = GeneralAdapterDescriptions.DescriptionPrivateToken,
                schema = new Schema(implementation = classOf[String])
            )
        ),
        responses = Array(
            new ApiResponse(
                responseCode = HttpConstants.StatusOkCode,
                description = GeneralAdapterDescriptions.TestStatusOkDescription,
                content = Array(
                    new Content(
                        schema = new Schema(implementation = classOf[JsObject]),
                        examples = Array(
                            new ExampleObject(
                                name = GeneralAdapterExamples.TestExampleOkName,
                                value = GeneralAdapterExamples.TestExampleOk
                            )
                        )
                    )
                )
            ),
            new ApiResponse(
                responseCode = HttpConstants.StatusErrorCode,
                description = ServerConstants.StatusErrorDescription,
                content = Array(
                    new Content(
                        schema = new Schema(implementation = classOf[ResponseProblem]),
                        examples = Array(
                            new ExampleObject(
                                name = ServerConstants.ResponseExampleErrorName,
                                value = ServerConstants.ResponseExampleError
                            )
                        )
                    )
                )
            )
        )
    )
    def getServiceRoute: RequestContext => Future[RouteResult] = (
        path(ServerConstants.TestPath) &
        parameters(
            GeneralAdapterConstants.Page.optional,
            GeneralAdapterConstants.PageSize.optional,
            GeneralAdapterConstants.Target.withDefault(GeneralAdapterConstants.DefaultTarget)
        ) &
        optionalHeaderValueByName(HttpConstants.HeaderPrivateToken)
    ) {
        (
            page,
            pageSize,
            target,
            privateToken
        ) => get {
            getRoute(
                actorRef,
                TestOptions(
                    pageOptions = OnlyPageInputOptions(
                        page = page,
                        pageSize = pageSize
                    ),
                    target = target,
                    token = privateToken
                )
            )
        }
    }
}
// scalastyle:on method.length
