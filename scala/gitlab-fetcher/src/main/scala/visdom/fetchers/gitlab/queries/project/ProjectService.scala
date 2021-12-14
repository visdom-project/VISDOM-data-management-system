package visdom.fetchers.gitlab.queries.project

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
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import visdom.fetchers.gitlab.queries.Constants
import visdom.http.server.response.ResponseAccepted
import visdom.http.server.response.ResponseProblem
import visdom.http.server.fetcher.gitlab.ProjectQueryOptions
import visdom.http.server.GitlabFetcherResponseHandler
import visdom.utils.WarningConstants


// scalastyle:off method.length
@SuppressWarnings(Array(WarningConstants.UnusedMethodParameter))
@Path(ProjectConstants.ProjectRootPath)
class ProjectService(projectActor: ActorRef)(implicit executionContext: ExecutionContext)
extends Directives
with GitlabFetcherResponseHandler {
    val route: Route = (
        getFileRoute
    )

    @GET
    @Produces(Array(MediaType.APPLICATION_JSON))
    @Operation(
        summary = ProjectConstants.ProjectEndpointSummary,
        description = ProjectConstants.ProjectEndpointDescription,
        parameters = Array(
            new Parameter(
                name = Constants.ParameterProjectId,
                in = ParameterIn.QUERY,
                required = false,
                description = Constants.ParameterDescriptionProjectId,
                example = Constants.ParameterExampleProjectId
            ),
            new Parameter(
                name = Constants.ParameterProjectName,
                in = ParameterIn.QUERY,
                required = false,
                description = Constants.ParameterDescriptionProjectName,
                example = Constants.ParameterExampleProjectName
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
                description = ProjectConstants.ProjectStatusAcceptedDescription,
                content = Array(
                    new Content(
                        schema = new Schema(implementation = classOf[ResponseAccepted]),
                        examples = Array(
                            new ExampleObject(
                                name = Constants.ResponseExampleAcceptedName,
                                value = ProjectConstants.ProjectResponseExampleAccepted
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
                                name = Constants.ResponseExampleInvalidName1,
                                value = Constants.ResponseExampleInvalid1
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
    def getFileRoute: RequestContext => Future[RouteResult] = (
        path(ProjectConstants.ProjectPath) &
        parameters(
            Constants.ParameterProjectId.optional,
            Constants.ParameterProjectName.optional,
            Constants.ParameterUseAnonymization
                .withDefault(Constants.ParameterDefaultUseAnonymization)
        )
    ) {
        (
            projectId,
            projectName,
            useAnonymization
        ) => get {
            val options: ProjectQueryOptions = ProjectQueryOptions(
                projectId = projectId,
                projectName = projectName,
                useAnonymization = useAnonymization
            )
            getRoute(projectActor, options)
        }
    }
}
// scalastyle:on method.length
