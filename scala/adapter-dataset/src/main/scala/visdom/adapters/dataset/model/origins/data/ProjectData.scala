// Copyright 2022 Tampere University
// This software was developed as a part of the VISDOM project: https://iteavisdom.org/
// This source code is licensed under the MIT license. See LICENSE in the repository root directory.
// Author(s): Ville Heikkilä <ville.heikkila@tuni.fi>

package visdom.adapters.dataset.model.origins.data

import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.bson.BsonValue
import spray.json.JsObject
import spray.json.JsValue
import visdom.adapters.dataset.schemas.ProjectSchema
import visdom.adapters.general.model.base.Data
import visdom.json.JsonUtils
import visdom.utils.SnakeCaseConstants


final case class ProjectData(
    project_key: Option[String],
    git_link: Option[String],
    jira_link: Option[String],
    sonar_project_key: Option[String]
)
extends Data {
    def toBsonValue(): BsonValue = {
        BsonDocument(
            Map(
                SnakeCaseConstants.ProjectKey -> JsonUtils.toBsonValue(project_key),
                SnakeCaseConstants.GitLink -> JsonUtils.toBsonValue(git_link),
                SnakeCaseConstants.JiraLink -> JsonUtils.toBsonValue(jira_link),
                SnakeCaseConstants.SonarProjectKey -> JsonUtils.toBsonValue(sonar_project_key)
            )
        )
    }

    def toJsValue(): JsValue = {
        JsObject(
            Map(
                SnakeCaseConstants.ProjectKey -> JsonUtils.toJsonValue(project_key),
                SnakeCaseConstants.GitLink -> JsonUtils.toJsonValue(git_link),
                SnakeCaseConstants.JiraLink -> JsonUtils.toJsonValue(jira_link),
                SnakeCaseConstants.SonarProjectKey -> JsonUtils.toJsonValue(sonar_project_key)
            )
        )
    }
}

object ProjectData {
    def getEmpty(): ProjectData = {
        ProjectData(None, None, None, None)
    }

    def fromProjectSchema(projectSchema: ProjectSchema): ProjectData = {
        ProjectData(
            project_key = Some(projectSchema.project_key),
            git_link = Some(projectSchema.git_link),
            jira_link = Some(projectSchema.jira_link),
            sonar_project_key = Some(projectSchema.sonar_project_key)
        )
    }
}
