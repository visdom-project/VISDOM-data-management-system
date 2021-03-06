// Copyright 2022 Tampere University
// This software was developed as a part of the VISDOM project: https://iteavisdom.org/
// This source code is licensed under the MIT license. See LICENSE in the repository root directory.
// Author(s): Ville Heikkilä <ville.heikkila@tuni.fi>

package visdom.adapters.general

import akka.actor.Props
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.server.Route
import visdom.http.server.actors.GeneralAdapterInfoActor
import visdom.http.server.actors.MultiActor
import visdom.http.server.actors.SingleActor
import visdom.http.server.actors.UpdateActor
import visdom.http.server.services.AdapterInfoService
import visdom.http.server.services.ArtifactService
import visdom.http.server.services.AuthorService
import visdom.http.server.services.EventService
import visdom.http.server.services.MetadataService
import visdom.http.server.services.OriginService
import visdom.http.server.services.SingleService
import visdom.http.server.services.UpdateService
import visdom.http.server.swagger.SwaggerAdapterDocService
import visdom.http.server.swagger.SwaggerRoutes


object AdapterRoutes extends visdom.adapters.AdapterRoutes {
    override val routes: Route = Directives.concat(
        new AdapterInfoService(system.actorOf(Props[GeneralAdapterInfoActor])).route,
        new SingleService(system.actorOf(Props[SingleActor])).route,
        new OriginService(system.actorOf(Props[MultiActor])).route,
        new EventService(system.actorOf(Props[MultiActor])).route,
        new AuthorService(system.actorOf(Props[MultiActor])).route,
        new ArtifactService(system.actorOf(Props[MultiActor])).route,
        new MetadataService(system.actorOf(Props[MultiActor])).route,
        new UpdateService(system.actorOf(Props[UpdateActor])).route,
        SwaggerRoutes.getSwaggerRouter(
            new SwaggerAdapterDocService(
                Adapter.adapterValues,
                Set(
                    classOf[AdapterInfoService],
                    classOf[SingleService],
                    classOf[OriginService],
                    classOf[EventService],
                    classOf[AuthorService],
                    classOf[ArtifactService],
                    classOf[MetadataService],
                    classOf[UpdateService]
                )
            )
        )
    )
}
