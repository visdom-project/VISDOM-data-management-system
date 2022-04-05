package visdom.http.server.services.base

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
import visdom.adapters.options.ObjectTypes
import visdom.http.HttpConstants
import visdom.http.server.ServerConstants
import visdom.http.server.options.MultiOptions
import visdom.http.server.options.OnlyPageInputOptions
import visdom.http.server.response.ResponseProblem
import visdom.http.server.services.constants.GeneralAdapterConstants
import visdom.http.server.services.constants.GeneralAdapterDescriptions
import visdom.http.server.services.constants.GeneralAdapterExamples
import visdom.utils.CommonConstants
import visdom.utils.WarningConstants
import visdom.http.server.services.AdapterService
import visdom.http.server.services.MultiInputOptionsTrait


// scalastyle:off method.length
@SuppressWarnings(Array(WarningConstants.UnusedMethodParameter))
@Path(ServerConstants.OriginsRootPath)
abstract class OriginServiceBase(actorRef: ActorRef)(implicit executionContext: ExecutionContext)
extends Directives
with AdapterService
with MultiInputOptionsTrait
{
    val route: Route = (
        getServiceRoute
    )

    @GET
    @Produces(Array(MediaType.APPLICATION_JSON))
    @Operation(
        summary = GeneralAdapterDescriptions.OriginsEndpointSummary,
        description = GeneralAdapterDescriptions.OriginsEndpointDescription,
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
                name = GeneralAdapterConstants.Type,
                in = ParameterIn.QUERY,
                required = false,
                description = GeneralAdapterDescriptions.DescriptionType,
                schema = new Schema(
                    implementation = classOf[String]
                )
            ),
            new Parameter(
                name = GeneralAdapterConstants.Query,
                in = ParameterIn.QUERY,
                required = false,
                description = GeneralAdapterDescriptions.DescriptionQuery,
                schema = new Schema(
                    implementation = classOf[String],
                    example = GeneralAdapterExamples.ExampleQuery
                )
            ),
            new Parameter(
                name = GeneralAdapterConstants.Data,
                in = ParameterIn.QUERY,
                required = false,
                description = GeneralAdapterDescriptions.DescriptionData,
                schema = new Schema(
                    implementation = classOf[String]
                )
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
        path(ServerConstants.OriginsPath) &
        parameters(
            GeneralAdapterConstants.Page.optional,
            GeneralAdapterConstants.PageSize.optional,
            GeneralAdapterConstants.Type.withDefault(CommonConstants.EmptyString),
            GeneralAdapterConstants.Query.optional,
            GeneralAdapterConstants.Data.optional
        )
    ) {
        (
            page,
            pageSize,
            objectType,
            query,
            dataAttributes
        ) => get {
            getRoute(
                actorRef,
                getMultiOptions(
                    ObjectTypes.TargetTypeOrigin,
                    page,
                    pageSize,
                    objectType,
                    query,
                    dataAttributes,
                    GeneralAdapterConstants.All
                )
            )
        }
    }
}
// scalastyle:on method.length
