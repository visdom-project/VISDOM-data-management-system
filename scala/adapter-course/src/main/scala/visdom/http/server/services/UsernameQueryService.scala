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
import visdom.adapters.course.options.UsernameQueryInput
import visdom.http.HttpConstants
import visdom.http.server.CourseAdapterResponseHandler
import visdom.http.server.ServerConstants
import visdom.http.server.response.ResponseProblem
import visdom.http.server.services.constants.CourseAdapterDescriptions
import visdom.http.server.services.constants.CourseAdapterConstants
import visdom.http.server.services.constants.CourseAdapterExamples
import visdom.utils.CommonConstants
import visdom.utils.WarningConstants


// scalastyle:off method.length
@SuppressWarnings(Array(WarningConstants.UnusedMethodParameter))
@Path(ServerConstants.UsernamesRootPath)
class UsernameQueryService(usernameActor: ActorRef)(implicit executionContext: ExecutionContext)
extends Directives
with CourseAdapterResponseHandler
{
    val route: Route = (
        getUsernameRoute
    )

    @GET
    @Produces(Array(MediaType.APPLICATION_JSON))
    @Operation(
        summary = CourseAdapterDescriptions.UsernameQueryEndpointSummary,
        description = CourseAdapterDescriptions.UsernameQueryEndpointDescription,
        parameters = Array(
            new Parameter(
                name = CourseAdapterConstants.CourseId,
                in = ParameterIn.QUERY,
                required = true,
                description = CourseAdapterConstants.DescriptionCourseId,
                schema = new Schema(
                    implementation = classOf[String]
                )
            )
        ),
        responses = Array(
            new ApiResponse(
                responseCode = HttpConstants.StatusOkCode,
                description = CourseAdapterDescriptions.UsernameStatusOkDescription,
                content = Array(
                    new Content(
                        schema = new Schema(implementation = classOf[JsObject]),
                        examples = Array(
                            new ExampleObject(
                                name = CourseAdapterExamples.ResponseExampleOkName,
                                value = CourseAdapterExamples.UsernameResponseExampleOk
                            )
                        )
                    )
                )
            ),
            new ApiResponse(
                responseCode = HttpConstants.StatusInvalidCode,
                description = CourseAdapterDescriptions.StatusInvalidDescription,
                content = Array(
                    new Content(
                        schema = new Schema(implementation = classOf[ResponseProblem]),
                        examples = Array(
                            new ExampleObject(
                                name = CourseAdapterExamples.ResponseExampleInvalidName,
                                value = CourseAdapterExamples.ResponseExampleInvalid
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
    def getUsernameRoute: RequestContext => Future[RouteResult] = (
        path(ServerConstants.UsernamesPath) &
        parameters(
            CourseAdapterConstants.CourseId.withDefault(CommonConstants.EmptyString)
        )
    ) {
        (
            courseId
        ) => get {
            getRoute(
                usernameActor,
                UsernameQueryInput(
                    courseId = courseId
                )
            )
        }
    }
}
// scalastyle:on method.length
