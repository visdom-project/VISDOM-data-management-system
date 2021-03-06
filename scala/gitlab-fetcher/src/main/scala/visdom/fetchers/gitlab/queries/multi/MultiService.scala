// Copyright 2022 Tampere University
// This software was developed as a part of the VISDOM project: https://iteavisdom.org/
// This source code is licensed under the MIT license. See LICENSE in the repository root directory.
// Author(s): Ville Heikkilä <ville.heikkila@tuni.fi>

package visdom.fetchers.gitlab.queries.multi

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
import visdom.fetchers.gitlab.queries.Constants
import visdom.http.server.GitlabFetcherResponseHandler
import visdom.http.server.fetcher.gitlab.MultiQueryOptions
import visdom.http.server.response.ResponseProblem
import visdom.http.server.response.ResponseAccepted
import visdom.utils.WarningConstants


// scalastyle:off method.length
@SuppressWarnings(Array(WarningConstants.UnusedMethodParameter))
@Path(MultiConstants.MultiRootPath)
class MultiService(multiActor: ActorRef)(implicit executionContext: ExecutionContext)
extends Directives
with GitlabFetcherResponseHandler
{
    val route: Route = (
        getMultiRoute
    )

    @GET
    @Produces(Array(MediaType.APPLICATION_JSON))
    @Operation(
        summary = MultiConstants.MultiEndpointSummary,
        description = MultiConstants.MultiEndpointDescription,
        parameters = Array(
            new Parameter(
                name = Constants.ParameterProjectNames,
                in = ParameterIn.QUERY,
                required = true,
                description = Constants.ParameterDescriptionProjectNames,
                example = Constants.ParameterExampleProjectNames
            ),
            new Parameter(
                name = Constants.ParameterReference,
                in = ParameterIn.QUERY,
                required = false,
                description = Constants.ParameterDescriptionReference,
                schema = new Schema(
                    implementation = classOf[String],
                    defaultValue = Constants.ParameterDefaultReference
                )
            ),
            new Parameter(
                name = Constants.ParameterFilePath,
                in = ParameterIn.QUERY,
                required = false,
                description = Constants.ParameterDescriptionFilePathForFiles,
                schema = new Schema(
                    implementation = classOf[String]
                )
            ),
            new Parameter(
                name = Constants.ParameterRecursive,
                in = ParameterIn.QUERY,
                required = false,
                description = Constants.ParameterDescriptionRecursive,
                schema = new Schema(
                    implementation = classOf[String],
                    defaultValue = Constants.ParameterDefaultRecursiveString,
                    allowableValues = Array(Constants.FalseString, Constants.TrueString)
                )
            ),
            new Parameter(
                name = Constants.ParameterStartDate,
                in = ParameterIn.QUERY,
                required = false,
                description = MultiConstants.ParameterDescriptionStartDate,
                schema = new Schema(
                    implementation = classOf[String],
                    format = Constants.DateTimeFormat
                )
            ),
            new Parameter(
                name = Constants.ParameterEndDate,
                in = ParameterIn.QUERY,
                required = false,
                description = MultiConstants.ParameterDescriptionEndDate,
                schema = new Schema(
                    implementation = classOf[String],
                    format = Constants.DateTimeFormat
                )
            ),
            new Parameter(
                name = Constants.ParameterUseAnonymization,
                in = ParameterIn.QUERY,
                required = false,
                description = Constants.ParameterDescriptionUseAnonymization,
                schema = new Schema(
                    implementation = classOf[String],
                    defaultValue = Constants.ParameterDefaultUseAnonymization,
                    allowableValues = Array(Constants.FalseString, Constants.TrueString)
                )
            )
        ),
        responses = Array(
            new ApiResponse(
                responseCode = Constants.StatusAcceptedCode,
                description = MultiConstants.MultiStatusAcceptedDescription,
                content = Array(
                    new Content(
                        schema = new Schema(implementation = classOf[ResponseAccepted]),
                        examples = Array(
                            new ExampleObject(
                                name = Constants.ResponseExampleAcceptedName,
                                value = MultiConstants.MultiResponseExampleAccepted
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
                                name = Constants.ResponseExampleInvalidName3,
                                value = Constants.ResponseExampleInvalid3
                            ),
                            new ExampleObject(
                                name = Constants.ResponseExampleInvalidName2,
                                value = Constants.ResponseExampleInvalid2
                            )
                        )
                    )
                )
            ),
            new ApiResponse(
                responseCode = Constants.StatusUnauthorizedCode,
                description = Constants.StatusUnauthorizedDescription,
                content = Array(
                    new Content(
                        schema = new Schema(implementation = classOf[ResponseProblem]),
                        examples = Array(
                            new ExampleObject(
                                name = Constants.ResponseExampleUnauthorizedName2,
                                value = Constants.ResponseExampleUnauthorized2
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
                                name = Constants.ResponseExampleNotFoundName2,
                                value = Constants.ResponseExampleNotFound2
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
    def getMultiRoute: RequestContext => Future[RouteResult] = (
        path(MultiConstants.MultiPath) &
        parameters(
            Constants.ParameterProjectNames.withDefault(""),
            Constants.ParameterReference
                .withDefault(Constants.ParameterDefaultReference),
            Constants.ParameterFilePath.optional,
            Constants.ParameterRecursive.withDefault(Constants.ParameterDefaultRecursiveString),
            Constants.ParameterStartDate.optional,
            Constants.ParameterEndDate.optional,
            Constants.ParameterUseAnonymization
                .withDefault(Constants.ParameterDefaultUseAnonymization)
        )
    ) {
        (
            projectNames,
            reference,
            filePath,
            recursive,
            startDate,
            endDate,
            useAnonymization
        ) => get {
            val options: MultiQueryOptions = MultiQueryOptions(
                projectNames = projectNames,
                reference = reference,
                filePath = filePath,
                recursive = recursive,
                startDate = startDate,
                endDate = endDate,
                useAnonymization = useAnonymization
            )
            getRoute(multiActor, options)
        }
    }
}
// scalastyle:on method.length
