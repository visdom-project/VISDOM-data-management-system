// Copyright 2022 Tampere University
// This software was developed as a part of the VISDOM project: https://iteavisdom.org/
// This source code is licensed under the MIT license. See LICENSE in the repository root directory.
// Author(s): Ville Heikkilä <ville.heikkila@tuni.fi>

package visdom.adapter.gitlab.queries.commits

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.server.RequestContext
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.RouteResult
import akka.pattern.ask
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
import visdom.adapter.gitlab.queries.Constants
import visdom.http.server.GitlabAdapterResponseHandler
import visdom.http.server.ResponseUtils
import visdom.http.server.adapter.gitlab.CommitDataQueryOptions
import visdom.http.server.response.JsonResponse
import visdom.http.server.response.ResponseProblem
import visdom.utils.WarningConstants.UnusedMethodParameter


// scalastyle:off method.length
@SuppressWarnings(Array(UnusedMethodParameter))
@Path(CommitDataConstants.CommitDataRootPath)
class CommitDataService(commitDataActor: ActorRef)(implicit executionContext: ExecutionContext)
extends Directives
with GitlabAdapterResponseHandler {
    val route: Route = (
        getCommitDataRoute
    )

    @GET
    @Produces(Array(MediaType.APPLICATION_JSON))
    @Operation(
        summary = CommitDataConstants.CommitDataEndpointSummary,
        description = CommitDataConstants.CommitDataEndpointDescription,
        parameters = Array(
            new Parameter(
                name = Constants.ParameterProjectName,
                in = ParameterIn.QUERY,
                required = false,
                description = Constants.ParameterDescriptionProjectName,
                example = Constants.ParameterExampleProjectName
            ),
            new Parameter(
                name = Constants.ParameterUserName,
                in = ParameterIn.QUERY,
                required = false,
                description = Constants.ParameterDescriptionUserName,
                example = Constants.ParameterExampleUserName
            ),
            new Parameter(
                name = Constants.ParameterStartDate,
                in = ParameterIn.QUERY,
                required = false,
                description = Constants.ParameterDescriptionStartDate,
                schema = new Schema(
                    implementation = classOf[String],
                    format = Constants.DateFormat
                )
            ),
            new Parameter(
                name = Constants.ParameterEndDate,
                in = ParameterIn.QUERY,
                required = false,
                description = Constants.ParameterDescriptionEndDate,
                schema = new Schema(
                    implementation = classOf[String],
                    format = Constants.DateFormat
                )
            )
        ),
        responses = Array(
            new ApiResponse(
                responseCode = Constants.StatusOkCode,
                description = CommitDataConstants.CommitDataStatusOkDescription,
                content = Array(
                    new Content(
                        schema = new Schema(implementation = classOf[JsObject]),
                        examples = Array(
                            new ExampleObject(
                                name = CommitDataConstants.ResponseExampleOkName,
                                value = CommitDataConstants.CommitDataResponseExampleOk
                            )
                        )
                    )
                )
            ),
            new ApiResponse(
                responseCode = Constants.StatusInvalidCode,
                description = Constants.StatusInvalidDescription,
                content = Array(
                    new Content(
                        schema = new Schema(implementation = classOf[ResponseProblem]),
                        examples = Array(
                            new ExampleObject(
                                name = Constants.ResponseExampleInvalidName,
                                value = Constants.ResponseExampleInvalid
                            )
                        )
                    )
                )
            ),
            new ApiResponse(
                responseCode = Constants.StatusNotFoundCode,
                description = Constants.StatusNotFoundDescription,
                content = Array(
                    new Content(
                        schema = new Schema(implementation = classOf[ResponseProblem]),
                        examples = Array(
                            new ExampleObject(
                                name = Constants.ResponseExampleNotFoundName,
                                value = Constants.ResponseExampleNotFound
                            )
                        )
                    )
                )
            ),
            new ApiResponse(
                responseCode = Constants.StatusErrorCode,
                description = Constants.StatusErrorDescription,
                content = Array(
                    new Content(
                        schema = new Schema(implementation = classOf[ResponseProblem]),
                        examples = Array(
                            new ExampleObject(
                                name = Constants.ResponseExampleErrorName,
                                value = Constants.ResponseExampleError
                            )
                        )
                    )
                )
            )
        )
    )
    def getCommitDataRoute: RequestContext => Future[RouteResult] = (
        path(CommitDataConstants.CommitDataPath) &
        parameters(
            Constants.ParameterProjectName.optional,
            Constants.ParameterUserName.optional,
            Constants.ParameterStartDate.optional,
            Constants.ParameterEndDate.optional
        )
    ) {
        (
            projectName,
            userName,
            startDate,
            endDate
        ) => get {
            val options: CommitDataQueryOptions = CommitDataQueryOptions(
                projectName,
                userName,
                startDate,
                endDate
            )
            getRoute(commitDataActor, options)
        }
    }
}
// scalastyle:on method.length
