package visdom.fetchers.gitlab.queries.info

import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.server.RequestContext
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.RouteResult
import akka.pattern.ask
import io.swagger.v3.oas.annotations.Operation
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
import visdom.http.server.response.GitlabFetcherInfoResponse
import visdom.http.server.GitlabFetcherResponseHandler
import visdom.http.server.QueryOptionsBaseObject


@SuppressWarnings(Array("UnusedMethodParameter"))
@Path(InfoConstants.InfoRootPath)
class InfoService(infoActor: ActorRef)(implicit executionContext: ExecutionContext)
extends Directives
with GitlabFetcherResponseHandler {
    val route: Route = (
        getInfoRoute
    )

    @GET
    @Produces(Array(MediaType.APPLICATION_JSON))
    @Operation(
        summary = InfoConstants.InfoEndpointSummary,
        description = InfoConstants.InfoEndpointDescription,
        responses = Array(
            new ApiResponse(
                responseCode = Constants.StatusOkCode,
                description = InfoConstants.InfoStatusOkDescription,
                content = Array(
                    new Content(
                        schema = new Schema(implementation = classOf[GitlabFetcherInfoResponse]),
                        examples = Array(
                            new ExampleObject(
                                name = InfoConstants.InfoResponseExampleName,
                                value = InfoConstants.InfoResponseExample
                            )
                        )
                    )
                )
            )
        )
    )
    def getInfoRoute: RequestContext => Future[RouteResult] = (
        path(InfoConstants.InfoPath)
    ) {
        get {
            getRoute(infoActor, QueryOptionsBaseObject)
        }
    }
}