// Copyright 2022 Tampere University
// This software was developed as a part of the VISDOM project: https://iteavisdom.org/
// This source code is licensed under the MIT license. See LICENSE in the repository root directory.
// Author(s): Ville Heikkilä <ville.heikkila@tuni.fi>

package visdom.adapters.general.usecases

import visdom.adapters.general.AdapterValues
import visdom.adapters.options.CacheQueryOptions
import visdom.adapters.options.ObjectTypes
import visdom.adapters.options.ObjectTypesTrait
import visdom.adapters.options.SingleQueryOptions
import visdom.adapters.queries.BaseCacheQuery
import visdom.adapters.queries.BaseSparkQuery
import visdom.adapters.queries.IncludesQueryCode
import visdom.adapters.results.BaseResultValue
import visdom.adapters.utils.GeneralQueryUtils
import visdom.adapters.utils.ModelUtils
import visdom.adapters.utils.ModelUtilsTrait
import visdom.utils.CommonConstants


class SingleQuery(queryOptions: SingleQueryOptions)
extends BaseCacheQuery(queryOptions) {
    val objectTypes: ObjectTypesTrait = ObjectTypes
    val modelUtilsObject: ModelUtilsTrait = ModelUtils
    val cacheUpdaterClass: Class[_ <: CacheUpdater] = classOf[CacheUpdater]
    val generalQueryUtils: GeneralQueryUtils = AdapterValues.generalQueryUtils

    def cacheCheck(): Boolean = {
        objectTypes.getTargetType(queryOptions.objectType) match {
            case Some(targetType: String) => modelUtilsObject.isTargetCacheUpdated(targetType)
            case None => false
        }
    }

    def updateCache(): (Class[_ <: BaseSparkQuery], CacheQueryOptions) = {
        (
            cacheUpdaterClass,
            CacheQueryOptions(
                objectTypes.getTargetType(queryOptions.objectType).getOrElse(CommonConstants.EmptyString)
            )
        )
    }

    def getResults(): Option[BaseResultValue] = {
        generalQueryUtils.getCacheResult(queryOptions)
    }
}

object SingleQuery extends IncludesQueryCode {
    val queryCode: Int = 102
}
