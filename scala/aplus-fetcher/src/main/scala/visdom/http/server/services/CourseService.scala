// Copyright 2022 Tampere University
// This software was developed as a part of the VISDOM project: https://iteavisdom.org/
// This source code is licensed under the MIT license. See LICENSE in the repository root directory.
// Author(s): Ville Heikkilä <ville.heikkila@tuni.fi>

package visdom.http.server.services

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
import visdom.http.HttpConstants
import visdom.http.server.APlusFetcherResponseHandler
import visdom.http.server.ServerConstants
import visdom.http.server.fetcher.aplus.CourseDataQueryOptions
import visdom.http.server.response.ResponseAccepted
import visdom.http.server.response.ResponseProblem
import visdom.http.server.services.constants.APlusFetcherDescriptions
import visdom.http.server.services.constants.APlusFetcherExamples
import visdom.http.server.services.constants.APlusServerConstants
import visdom.utils.WarningConstants


// scalastyle:off method.length
@SuppressWarnings(Array(WarningConstants.UnusedMethodParameter))
@Path(ServerConstants.CoursesRootPath)
class CourseService(courseDataActor: ActorRef)(implicit executionContext: ExecutionContext)
extends Directives
with APlusFetcherResponseHandler
{
    val route: Route = (
        getCourseDataRoute
    )

    @GET
    @Produces(Array(MediaType.APPLICATION_JSON))
    @Operation(
        summary = APlusFetcherDescriptions.APlusFetcherCourseEndpointSummary,
        description = APlusFetcherDescriptions.APlusFetcherCourseEndpointDescription,
        parameters = Array(
            new Parameter(
                name = APlusServerConstants.CourseId,
                in = ParameterIn.QUERY,
                required = false,
                description = APlusServerConstants.ParameterDescriptionCourseId,
                schema = new Schema(
                    implementation = classOf[Int]
                )
            ),
            new Parameter(
                name = APlusServerConstants.ParseNames,
                in = ParameterIn.QUERY,
                required = false,
                description = APlusServerConstants.ParameterDescriptionParseNames,
                schema = new Schema(
                    implementation = classOf[String],
                    defaultValue = APlusServerConstants.DefaultParseNames,
                    allowableValues = Array(ServerConstants.FalseString, ServerConstants.TrueString)
                )
            ),
            new Parameter(
                name = APlusServerConstants.IncludeModules,
                in = ParameterIn.QUERY,
                required = false,
                description = APlusServerConstants.ParameterDescriptionIncludeModules,
                schema = new Schema(
                    implementation = classOf[String],
                    defaultValue = APlusServerConstants.DefaultIncludeModules,
                    allowableValues = Array(ServerConstants.FalseString, ServerConstants.TrueString)
                )
            ),
            new Parameter(
                name = APlusServerConstants.IncludeExercises,
                in = ParameterIn.QUERY,
                required = false,
                description = APlusServerConstants.ParameterDescriptionIncludeExercises,
                schema = new Schema(
                    implementation = classOf[String],
                    defaultValue = APlusServerConstants.DefaultIncludeExercises,
                    allowableValues = Array(ServerConstants.FalseString, ServerConstants.TrueString)
                )
            ),
            new Parameter(
                name = APlusServerConstants.IncludeSubmissions,
                in = ParameterIn.QUERY,
                required = false,
                description = APlusServerConstants.ParameterDescriptionIncludeSubmissions,
                schema = new Schema(
                    implementation = classOf[String],
                    defaultValue = APlusServerConstants.DefaultIncludeSubmissions,
                    allowableValues = Array(ServerConstants.FalseString, ServerConstants.TrueString)
                )
            ),
            new Parameter(
                name = APlusServerConstants.IncludeGitlabData,
                in = ParameterIn.QUERY,
                required = false,
                description = APlusServerConstants.ParameterDescriptionIncludeGitlabData,
                schema = new Schema(
                    implementation = classOf[String],
                    defaultValue = APlusServerConstants.DefaultIncludeGitlabData,
                    allowableValues = Array(ServerConstants.FalseString, ServerConstants.TrueString)
                )
            ),
            new Parameter(
                name = APlusServerConstants.IncludePoints,
                in = ParameterIn.QUERY,
                required = false,
                description = APlusServerConstants.ParameterDescriptionIncludePoints,
                schema = new Schema(
                    implementation = classOf[String],
                    defaultValue = APlusServerConstants.DefaultIncludePoints,
                    allowableValues = Array(ServerConstants.FalseString, ServerConstants.TrueString)
                )
            ),
            new Parameter(
                name = APlusServerConstants.UseAnonymization,
                in = ParameterIn.QUERY,
                required = false,
                description = APlusServerConstants.ParameterDescriptionUseAnonymization,
                schema = new Schema(
                    implementation = classOf[String],
                    defaultValue = APlusServerConstants.DefaultUseAnonymization,
                    allowableValues = Array(ServerConstants.FalseString, ServerConstants.TrueString)
                )
            ),
            new Parameter(
                name = APlusServerConstants.GDPRExerciseId,
                in = ParameterIn.QUERY,
                required = false,
                description = APlusServerConstants.ParameterDescriptionGDPRExerciseId,
                schema = new Schema(
                    implementation = classOf[Int]
                )
            ),
            new Parameter(
                name = APlusServerConstants.GDPRFieldName,
                in = ParameterIn.QUERY,
                required = false,
                description = APlusServerConstants.ParameterDescriptionGDPRFieldName,
                schema = new Schema(
                    implementation = classOf[String],
                    defaultValue = APlusServerConstants.DefaultGDPRFieldName
                )
            ),
            new Parameter(
                name = APlusServerConstants.GDPRAcceptedAnswer,
                in = ParameterIn.QUERY,
                required = false,
                description = APlusServerConstants.ParameterDescriptionGDPRAcceptedAnswer,
                schema = new Schema(
                    implementation = classOf[String],
                    defaultValue = APlusServerConstants.DefaultGDPRAcceptedAnswer
                )
            )
        ),
        responses = Array(
            new ApiResponse(
                responseCode = HttpConstants.StatusAcceptedCode,
                description = APlusFetcherDescriptions.StatusAcceptedDescription,
                content = Array(
                    new Content(
                        schema = new Schema(implementation = classOf[ResponseAccepted]),
                        examples = Array(
                            new ExampleObject(
                                name = ServerConstants.ResponseExampleAcceptedName,
                                value = APlusFetcherExamples.CourseDataResponseExampleAccepted
                            )
                        )
                    )
                )
            ),
            new ApiResponse(
                responseCode = HttpConstants.StatusInvalidCode,
                description = APlusFetcherDescriptions.StatusInvalidDescription,
                content = Array(
                    new Content(
                        schema = new Schema(implementation = classOf[ResponseProblem]),
                        examples = Array(
                            new ExampleObject(
                                name = APlusFetcherExamples.ResponseExampleInvalidName,
                                value = APlusFetcherExamples.ResponseExampleInvalid
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
    def getCourseDataRoute: RequestContext => Future[RouteResult] = (
        path(ServerConstants.CoursesPath) &
        parameters(
            APlusServerConstants.CourseId.optional,
            APlusServerConstants.ParseNames.withDefault(APlusServerConstants.DefaultParseNames),
            APlusServerConstants.IncludeModules.withDefault(APlusServerConstants.DefaultIncludeModules),
            APlusServerConstants.IncludeExercises.withDefault(APlusServerConstants.DefaultIncludeExercises),
            APlusServerConstants.IncludeSubmissions.withDefault(APlusServerConstants.DefaultIncludeSubmissions),
            APlusServerConstants.IncludeGitlabData.withDefault(APlusServerConstants.DefaultIncludeGitlabData),
            APlusServerConstants.IncludePoints.withDefault(APlusServerConstants.DefaultIncludePoints),
            APlusServerConstants.UseAnonymization.withDefault(APlusServerConstants.DefaultUseAnonymization),
            APlusServerConstants.GDPRExerciseId.optional,
            APlusServerConstants.GDPRFieldName.withDefault(APlusServerConstants.DefaultGDPRFieldName),
            APlusServerConstants.GDPRAcceptedAnswer.withDefault(APlusServerConstants.DefaultGDPRAcceptedAnswer)
        )
    ) {
        (
            courseId,
            parseNames,
            includeModules,
            includeExercises,
            includeSubmissions,
            includeGitlabData,
            includePoints,
            useAnonymization,
            gdprExerciseId,
            gdprFieldName,
            gdprAcceptedAnswer
        ) => get {
            getRoute(
                courseDataActor,
                CourseDataQueryOptions(
                    courseId = courseId,
                    parseNames = parseNames,
                    includeModules = includeModules,
                    includeExercises = includeExercises,
                    includeSubmissions = includeSubmissions,
                    includeGitlabData = includeGitlabData,
                    includePoints = includePoints,
                    useAnonymization = useAnonymization,
                    gdprExerciseId = gdprExerciseId,
                    gdprFieldName = gdprFieldName,
                    gdprAcceptedAnswer = gdprAcceptedAnswer
                )
            )
        }
    }
}
// scalastyle:on method.length
