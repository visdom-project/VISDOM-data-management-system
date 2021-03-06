// Copyright 2022 Tampere University
// This software was developed as a part of the VISDOM project: https://iteavisdom.org/
// This source code is licensed under the MIT license. See LICENSE in the repository root directory.
// Author(s): Ville Heikkilä <ville.heikkila@tuni.fi>

package visdom.database.mongodb

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.Duration

object MongoConstants {
    val ApplicationName: String = "APPLICATION_NAME"
    val MongoHost: String = "MONGODB_HOST"
    val MongoPort: String = "MONGODB_PORT"
    val MongoUserName: String = "MONGODB_USERNAME"
    val MongoPassword: String = "MONGODB_PASSWORD"
    val MongoMetadataDatabase: String = "MONGODB_METADATA_DATABASE"
    val MongoTargetDatabase: String = "MONGO_DATA_DATABASE"

    val DefaultApplicationName: String = "gitlab-fetcher"
    val DefaultMongoHost: String = "localhost"
    val DefaultMongoPort: Int = 27017
    val DefaultMongoUserName: String = ""
    val DefaultMongoPassword: String = ""
    val DefaultMongoMetadataDatabase: String = "metadata"
    val DefaultMongoTargetDatabase: String = "gitlab"

    val CollectionArtifacts: String = "artifacts"
    val CollectionCommits: String = "commits"
    val CollectionCourses: String = "courses"
    val CollectionEvents: String = "events"
    val CollectionExercises: String = "exercises"
    val CollectionFiles: String = "files"
    val CollectionGitCommits: String = "git_commits"
    val CollectionGitCommitsChanges: String = "git_commits_changes"
    val CollectionJiraIssues: String = "jira_issues"
    val CollectionJobs: String = "jobs"
    val CollectionJobLogs: String = "job_logs"
    val CollectionMetadata: String = "metadata"
    val CollectionModules: String = "modules"
    val CollectionOrigins: String = "origins"
    val CollectionPipelineReports: String = "pipeline_reports"
    val CollectionPipelines: String = "pipelines"
    val CollectionPoints: String = "points"
    val CollectionProjects: String = "projects"
    val CollectionSonarMeasures: String = "sonar_measures"
    val CollectionSubmissions: String = "submissions"
    val CollectionTemp: String = "temp"

    val AttributeCount: String = "count"
    val AttributeDefaultId: String = "_id"
    val AttributeDocumentUpdatedCount: String = "documents_updated_count"
    val AttributeHostName: String = "host_name"
    val AttributeOptions: String = "options"
    val AttributeProjectName: String = "project_name"
    val AttributeTimestamp: String = "timestamp"
    val AttributeType: String = "type"

    // the default maximum delay until a MongoDB query will be considered failed
    val DefaultMaxQueryDelaySeconds: Int = 15
    val DefaultMaxQueryDelay: Duration = Duration(DefaultMaxQueryDelaySeconds, TimeUnit.SECONDS)

}
