package visdom.adapters.utils

import org.apache.spark.sql.Dataset
import org.apache.spark.sql.SparkSession
import visdom.adapters.general.model.results.ArtifactResult
import visdom.adapters.general.model.results.ArtifactResult.FileArtifactResult
import visdom.adapters.general.model.results.ArtifactResult.PipelineReportArtifactResult
import visdom.adapters.general.schemas.FileSchema
import visdom.adapters.general.schemas.PipelineReportSchema
import visdom.database.mongodb.MongoConstants
import visdom.utils.CommonConstants


class ModelArtifactUtils(sparkSession: SparkSession, modelUtils: ModelUtils) {
    import sparkSession.implicits.newProductEncoder

    def getFiles(): Dataset[FileArtifactResult] = {
        modelUtils.loadMongoData[FileSchema](MongoConstants.CollectionFiles)
            .flatMap(row => FileSchema.fromRow(row))
            .map(fileSchema => ArtifactResult.fromFileSchema(fileSchema))
    }

    def getPipelineReports(): Dataset[PipelineReportArtifactResult] = {
        val pipelineProjectNames: Map[Int, String] = modelUtils.getPipelineProjectNames()

        modelUtils.loadMongoData[PipelineReportSchema](MongoConstants.CollectionPipelineReports)
            .flatMap(row => PipelineReportSchema.fromRow(row))
            // include only the reports that have a known pipeline
            .filter(report => pipelineProjectNames.keySet.contains(report.pipeline_id))
            .map(
                reportSchema =>
                    ArtifactResult.fromPipelineReportSchema(
                        reportSchema,
                        pipelineProjectNames.getOrElse(reportSchema.pipeline_id, CommonConstants.EmptyString)
                    )
            )
    }
}
