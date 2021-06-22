package visdom.http.server

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.Duration


object ServerConstants {
    final val QueryOkStatus: String = "Ok"
    final val QueryAcceptedStatus: String = "Accepted"
    final val QueryInvalidStatus: String = "BadRequest"
    final val QueryUnauthorizedStatus: String = "Unauthorized"
    final val QueryNotFoundStatus: String = "NotFound"
    final val QueryErrorStatus: String = "InternalServerError"

    final val InfoRootPath = "/info"
    final val InfoPath = "info"

    final val AdaptersRootPath = "/adapters"
    final val AdaptersPath = "adapters"

    final val FetchersRootPath = "/fetchers"
    final val FetchersPath = "fetchers"

    // the default maximum delay until a response is sent for HTTP server
    val DefaultMaxResponseDelaySeconds: Int = 30
    val DefaultMaxResponseDelay: Duration = Duration(DefaultMaxResponseDelaySeconds, TimeUnit.SECONDS)

    val DefaultActorSystem: String = "akka-http-sample"
    val HttpInternalHost: String = "0.0.0.0"
    val HttpInternalPort: Int = 8080

    val DefaultInfoLogText: String = "Received info query"
}