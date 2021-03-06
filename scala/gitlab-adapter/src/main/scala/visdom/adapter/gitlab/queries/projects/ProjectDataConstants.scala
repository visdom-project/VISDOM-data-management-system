// Copyright 2022 Tampere University
// This software was developed as a part of the VISDOM project: https://iteavisdom.org/
// This source code is licensed under the MIT license. See LICENSE in the repository root directory.
// Author(s): Ville Heikkilä <ville.heikkila@tuni.fi>

package visdom.adapter.gitlab.queries.projects


object ProjectDataConstants {
    final val ProjectDataRootPath = "/projects"
    final val ProjectDataPath = "projects"

    final val ProjectDataEndpointDescription =
        """Return the available projects names grouped using the group names."""
    final val ProjectDataEndpointSummary = "Return the project names for each group."

    final val ProjectDataStatusOkDescription = "The data successfully fetched"

    // the example responses for the projects endpoint
    final val ResponseExampleOkName = "Example response"
    final val ProjectDataResponseExampleOk = """{
        "group-name-1": [
            "project-name-1",
            "project-name-2"
        ],
        "group-name-2": [
            "project-name-3"
        ]
    }"""
}
