package visdom.adapters.general.usecases

import org.apache.spark.sql.SparkSession
import visdom.adapters.options.CacheQueryOptions
import visdom.adapters.queries.BaseSparkQuery
import visdom.adapters.utils.ModelUtils


class CacheUpdater(queryOptions: CacheQueryOptions, sparkSession: SparkSession)
extends BaseSparkQuery(queryOptions, sparkSession) {
    override def runQuery(): Unit = {
        new ModelUtils(sparkSession).updateTargetCache(queryOptions.targetType)
    }
}