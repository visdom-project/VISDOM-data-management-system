// Copyright 2022 Tampere University
// This software was developed as a part of the VISDOM project: https://iteavisdom.org/
// This source code is licensed under the MIT license. See LICENSE in the repository root directory.
// Author(s): Ville Heikkilä <ville.heikkila@tuni.fi>

package visdom.adapters.general.model.artifacts

import visdom.adapters.general.model.artifacts.data.PipelineReportData
import visdom.adapters.general.model.artifacts.states.PipelineReportState
import visdom.adapters.general.model.base.Artifact
import visdom.adapters.general.model.base.ItemLink
import visdom.adapters.general.model.events.PipelineEvent
import visdom.adapters.general.model.origins.GitlabOrigin
import visdom.adapters.general.schemas.PipelineReportSchema
import visdom.utils.CommonConstants
import visdom.utils.GeneralUtils


class PipelineReportArtifact(
    pipelineReportSchema: PipelineReportSchema,
    projectName: String
)
extends Artifact {
    def getType: String = PipelineReportArtifact.PipelineReportArtifactType

    val origin: ItemLink =
        new GitlabOrigin(
            pipelineReportSchema.host_name,
            CommonConstants.EmptyString,
            projectName,
            None
        ).link

    val name: String = PipelineReportArtifact.getName(pipelineReportSchema.pipeline_id)
    val description: String = PipelineReportArtifact.getDescription(pipelineReportSchema.pipeline_id)
    // NOTE: all pipeline reports use the same state for now
    val state: String = PipelineReportState.ReportComplete
    val data: PipelineReportData = PipelineReportData.fromPipelineReportSchema(pipelineReportSchema)

    val id: String = PipelineReportArtifact.getId(origin.id, pipelineReportSchema.pipeline_id)

    // add pipeline event to related events
    addRelatedEvent(
        ItemLink(
            PipelineEvent.getId(origin.id, pipelineReportSchema.pipeline_id),
            PipelineEvent.PipelineEventType
        )
    )
    // add test suite artifacts to related constructs
    addRelatedConstructs(
        pipelineReportSchema.test_suites.map(
            testSuite => ItemLink(
                TestSuiteArtifact.getId(origin.id, pipelineReportSchema.pipeline_id, testSuite.name),
                TestSuiteArtifact.TestSuiteArtifactType
            )
        )
    )
}

object PipelineReportArtifact {
    final val PipelineReportArtifactType: String = "pipeline_report"

    def getId(originId: String, pipelineId: Int): String = {
        GeneralUtils.getUuid(originId, PipelineReportArtifactType, pipelineId.toString())
    }

    def getName(pipelineId: Int): String = {
        pipelineId.toString()
    }

    def getDescription(pipelineId: Int): String = {
        s"Report for pipeline ${pipelineId}"
    }

    def fromPipelineReportSchema(
        pipelineReportSchema: PipelineReportSchema,
        projectName: String
    ): PipelineReportArtifact = {
        new PipelineReportArtifact(pipelineReportSchema, projectName)
    }
}
