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
import visdom.utils.GeneralUtils
import visdom.utils.SnakeCaseConstants


class ModelArtifactUtils(sparkSession: SparkSession, modelUtils: ModelUtils) {
    import sparkSession.implicits.newProductEncoder
    import sparkSession.implicits.newSequenceEncoder

    def getFiles(): Dataset[FileArtifactResult] = {
        val allFiles: Dataset[FileSchema] =
            modelUtils.loadMongoData[FileSchema](MongoConstants.CollectionFiles)
                .flatMap(row => FileSchema.fromRow(row))

        val fileParentMap: Map[(String, String, String), Seq[String]] =
            allFiles
                .map(
                    fileSchema => (
                        fileSchema,
                        GeneralUtils.getUpperFolder(fileSchema.path)
                    )
                )
                .filter(schemaWithParentFolder => schemaWithParentFolder._2 != CommonConstants.EmptyString)
                .groupByKey({case (schema, parentFolder) => (schema.host_name, schema.project_name, parentFolder)})
                .mapValues({case (schema, _) => Seq(schema.path)})
                .reduceGroups((first, second) => first ++ second)
                .collect()
                .toMap

        allFiles
            .map(
                fileSchema => ArtifactResult.fromFileSchema(
                    fileSchema,
                    fileSchema.`type` == SnakeCaseConstants.Tree match {
                        case true => fileParentMap.getOrElse(
                            (fileSchema.host_name, fileSchema.project_name, fileSchema.path),
                            Seq.empty
                        )
                        case false => Seq.empty
                    }
                )
            )
    }

    def getPipelineReports(): Dataset[PipelineReportArtifactResult] = {
        val projectNames: Map[Int, String] = modelUtils.getProjectNameMap()

        modelUtils.loadMongoData[PipelineReportSchema](MongoConstants.CollectionPipelineReports)
            .flatMap(row => PipelineReportSchema.fromRow(row))
            // include only the reports that have a known project name
            .filter(report => projectNames.keySet.contains(report.pipeline_id))
            .map(
                reportSchema =>
                    ArtifactResult.fromPipelineReportSchema(
                        reportSchema,
                        projectNames.getOrElse(reportSchema.pipeline_id, CommonConstants.EmptyString)
                    )
            )
    }
}