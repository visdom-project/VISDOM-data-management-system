package visdom.http.server

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.server.StandardRoute


trait BrokerResponseHandler extends ResponseHandler with BrokerServerProtocol {
    def handleOtherResponses(receivedResponse: response.BaseResponse): StandardRoute = {
        receivedResponse match {
            case brokerInfoResponse: response.BrokerInfoResponse =>
                Directives.complete(StatusCodes.OK, brokerInfoResponse)
        }
    }
}
