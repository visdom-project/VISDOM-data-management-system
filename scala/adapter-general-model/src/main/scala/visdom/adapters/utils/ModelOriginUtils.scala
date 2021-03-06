// Copyright 2022 Tampere University
// This software was developed as a part of the VISDOM project: https://iteavisdom.org/
// This source code is licensed under the MIT license. See LICENSE in the repository root directory.
// Author(s): Ville Heikkilä <ville.heikkila@tuni.fi>

package visdom.adapters.utils

import org.apache.spark.sql.Dataset
import org.apache.spark.sql.SparkSession
import visdom.adapters.general.model.results.OriginResult
import visdom.adapters.general.model.results.OriginResult.AplusOriginResult
import visdom.adapters.general.model.results.OriginResult.GitlabOriginResult
import visdom.adapters.general.schemas.CourseSchema
import visdom.adapters.general.schemas.GitlabProjectInformationSchema
import visdom.adapters.general.schemas.GitlabProjectSchema
import visdom.adapters.general.schemas.GitlabProjectSimpleSchema
import visdom.database.mongodb.MongoConstants
import visdom.utils.CommonConstants


class ModelOriginUtils(sparkSession: SparkSession, modelUtils: ModelUtils) {
    import sparkSession.implicits.newProductEncoder
    import sparkSession.implicits.newStringEncoder

    def getHostNames(): Dataset[String] = {
        getAllGitlabProjects()
            .map(project => project.host_name)
    }

    def getGitlabProjects(): Dataset[GitlabProjectSimpleSchema] = {
        modelUtils.loadMongoDataGitlab[GitlabProjectSchema](MongoConstants.CollectionProjects)
            .flatMap(row => GitlabProjectSchema.fromRow(row))
            .map(projectSchema => GitlabProjectSimpleSchema.fromProjectSchema(projectSchema))
    }

    def getGitlabProjectInformation(collectionName: String): Dataset[GitlabProjectSimpleSchema] = {
        modelUtils.loadMongoDataGitlab[GitlabProjectInformationSchema](collectionName)
            .na.drop()
            .distinct()
            .flatMap(row => GitlabProjectInformationSchema.fromRow(row))
            .map(informationSchema => GitlabProjectSimpleSchema.fromProjectInformationSchema(informationSchema))
    }

    def getAllGitlabProjects(): Dataset[GitlabProjectSimpleSchema] = {
        val projects = getGitlabProjects()
        val commitProjects = getGitlabProjectInformation(MongoConstants.CollectionCommits)
        val fileProjects = getGitlabProjectInformation(MongoConstants.CollectionFiles)
        val pipelineProjects = getGitlabProjectInformation(MongoConstants.CollectionPipelines)

        projects
            .union(commitProjects)
            .union(fileProjects)
            .union(pipelineProjects)
            .distinct()
    }

    def getGitlabHostOrigins(): Dataset[GitlabProjectSimpleSchema] = {
        getHostNames()
            .map(
                hostName => GitlabProjectSimpleSchema(
                    project_id = None,
                    project_name = CommonConstants.EmptyString,
                    group_name = CommonConstants.EmptyString,
                    host_name = hostName
                )
            )
    }

    def getGitlabOrigins(): Dataset[GitlabOriginResult] = {
        val projects = getAllGitlabProjects()
            .union(getGitlabHostOrigins())
            .distinct()
            .map(projectSchema => OriginResult.fromGitlabProjectSimpleSchema(projectSchema))

        val projectWithIds: Array[String] =
            projects
                .filter(origin => origin.data.project_id.isDefined)
                .map(origin => origin.id)
                .distinct()
                .collect()

        projects
            .filter(origin => origin.data.project_id.isDefined || !projectWithIds.contains(origin.id))
    }

    def getAplusOrigins(): Dataset[AplusOriginResult] = {
        val courseSchemas: Dataset[CourseSchema] = modelUtils.getCourseSchemas()

        val hostOrigins: Dataset[AplusOriginResult] =
            courseSchemas
                .map(course => course.host_name)
                .distinct()
                .map(hostName => OriginResult.fromAplusHost(hostName))

        courseSchemas
            .map(course => OriginResult.fromAplusCourse(course.host_name, course.id, Some(course.code)))
            .union(hostOrigins)
    }
}
