// Copyright 2022 Tampere University
// This software was developed as a part of the VISDOM project: https://iteavisdom.org/
// This source code is licensed under the MIT license. See LICENSE in the repository root directory.
// Author(s): Ville Heikkilä <ville.heikkila@tuni.fi>

package visdom.adapters

import java.time.Instant
import visdom.constants.ComponentConstants
import visdom.http.HttpConstants
import visdom.http.server.ServerConstants
import visdom.http.server.swagger.SwaggerConstants
import visdom.utils.CommonConstants
import visdom.utils.AdapterEnvironmentVariables.AdapterVariableMap
import visdom.utils.AdapterEnvironmentVariables.EnvironmentAPlusDatabase
import visdom.utils.AdapterEnvironmentVariables.EnvironmentCacheDatabase
import visdom.utils.AdapterEnvironmentVariables.EnvironmentDatasetDatabase
import visdom.utils.AdapterEnvironmentVariables.EnvironmentGitlabDatabase
import visdom.utils.EnvironmentVariables.EnvironmentApplicationName
import visdom.utils.EnvironmentVariables.EnvironmentHostName
import visdom.utils.EnvironmentVariables.EnvironmentHostPort
import visdom.utils.EnvironmentVariables.getEnvironmentVariable
import visdom.utils.QueryUtils


trait AdapterValues {
    val startTime: String = Instant.now().toString()

    val componentName: String = getEnvironmentVariable(EnvironmentApplicationName, AdapterVariableMap)
    val componentType: String = ComponentConstants.AdapterComponentType

    val hostServerName: String = getEnvironmentVariable(EnvironmentHostName, AdapterVariableMap)
    val hostServerPort: String = getEnvironmentVariable(EnvironmentHostPort, AdapterVariableMap)
    val apiAddress: String = List(hostServerName, hostServerPort).mkString(CommonConstants.DoubleDot)
    val swaggerDefinition: String = SwaggerConstants.SwaggerLocation

    val fullApiAddress: String =
        HttpConstants.HttpPrefix.concat(
            apiAddress.contains(HttpConstants.Localhost) match {
                case true => Seq(
                    getEnvironmentVariable(EnvironmentApplicationName),
                    ServerConstants.HttpInternalPort.toString()
                ).mkString(CommonConstants.DoubleDot)
                case false => apiAddress
            }
        )

    val aPlusDatabaseName: String = getEnvironmentVariable(EnvironmentAPlusDatabase, AdapterVariableMap)
    val gitlabDatabaseName: String = getEnvironmentVariable(EnvironmentGitlabDatabase, AdapterVariableMap)
    val cacheDatabaseName: String = getEnvironmentVariable(EnvironmentCacheDatabase, AdapterVariableMap)
    val datasetDatabaseName: String = getEnvironmentVariable(EnvironmentDatasetDatabase, AdapterVariableMap)

    val supportedDatabases: Seq[String] = Seq(aPlusDatabaseName, gitlabDatabaseName)
    val cache: QueryCache = new QueryCache(supportedDatabases)
    val queryUtils: QueryUtils = new QueryUtils(cache)

    val AdapterType: String = AdapterConstants.DefaultAdapterType
    val Version: String = AdapterConstants.DefaultAdapterVersion
}

object DefaultAdapterValues extends AdapterValues
