// Copyright 2022 Tampere University
// This software was developed as a part of the VISDOM project: https://iteavisdom.org/
// This source code is licensed under the MIT license. See LICENSE in the repository root directory.
// Author(s): Ville Heikkilä <ville.heikkila@tuni.fi>

package visdom.utils


object APlusEnvironmentVariables {
    val EnvironmentAPlusHost: String = "APLUS_HOST"
    val EnvironmentAPlusToken: String = "APLUS_TOKEN"
    val EnvironmentAPlusInsecureConnection: String = "APLUS_INSECURE_CONNECTION"
    val EnvironmentDataDatabase: String = "MONGODB_DATA_DATABASE"
    val EnvironmentAdditionalMetadata: String = "ADDITIONAL_METADATA"

    // the default values for the A+ environment variables
    val DefaultAPlusHost: String = ""
    val DefaultAPlusToken: String = ""
    val DefaultAPlusInsecureConnection: String = "false"
    val DefaultDataDatabase: String = "aplus"
    val DefaultAdditionalMetadata: String = "metadata.json"

    val APlusVariableMap: Map[String, String] =
        EnvironmentVariables.VariableMap ++
        Map(
            EnvironmentAPlusHost -> DefaultAPlusHost,
            EnvironmentAPlusToken -> DefaultAPlusToken,
            EnvironmentAPlusInsecureConnection -> DefaultAPlusInsecureConnection,
            EnvironmentDataDatabase -> DefaultDataDatabase,
            EnvironmentAdditionalMetadata -> DefaultAdditionalMetadata
        )
}
