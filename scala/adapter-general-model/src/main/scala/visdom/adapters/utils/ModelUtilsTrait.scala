package visdom.adapters.utils

import com.mongodb.spark.config.ReadConfig
import org.apache.spark.sql.SparkSession
import visdom.adapters.general.AdapterValues
import visdom.adapters.options.ObjectTypes
import visdom.adapters.options.ObjectTypesTrait
import visdom.spark.ConfigUtils


trait ModelUtilsTrait {
    val objectTypes: ObjectTypesTrait = ObjectTypes

    def isOriginCacheUpdated(): Boolean = {
        isTargetCacheUpdated(objectTypes.TargetTypeOrigin)
    }

    def isEventCacheUpdated(): Boolean = {
        isTargetCacheUpdated(objectTypes.TargetTypeEvent)
    }

    def isAuthorCacheUpdated(): Boolean = {
        isTargetCacheUpdated(objectTypes.TargetTypeAuthor)
    }

    def isArtifactCacheUpdated(): Boolean = {
        isTargetCacheUpdated(objectTypes.TargetTypeArtifact)
    }

    def isMetadataCacheUpdated(): Boolean = {
        isTargetCacheUpdated(objectTypes.TargetTypeMetadata)
    }

    def isTargetCacheUpdated(targetType: String): Boolean = {
        objectTypes.objectTypes.get(targetType) match {
            case Some(objectTypes: Set[String]) =>
                objectTypes.forall(objectType => GeneralQueryUtils.isCacheUpdated(objectType))
            case None => false
        }
    }

    def getReadConfigGitlab(sparkSession: SparkSession, collectionName: String): ReadConfig = {
        ConfigUtils.getReadConfig(
            sparkSession,
            AdapterValues.gitlabDatabaseName,
            collectionName
        )
    }

    def getReadConfigAplus(sparkSession: SparkSession, collectionName: String): ReadConfig = {
        ConfigUtils.getReadConfig(
            sparkSession,
            AdapterValues.aPlusDatabaseName,
            collectionName
        )
    }
}