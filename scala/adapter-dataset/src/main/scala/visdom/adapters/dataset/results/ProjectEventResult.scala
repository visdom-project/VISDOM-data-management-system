package visdom.adapters.dataset.results

import visdom.adapters.general.model.base.ItemLink
import visdom.adapters.dataset.model.events.ProjectCommitEvent
import visdom.adapters.dataset.model.events.data.ProjectCommitData
import visdom.adapters.general.model.results.EventResult
import visdom.adapters.dataset.schemas.CommitChangeSchema
import visdom.adapters.dataset.schemas.CommitSchema


object ProjectEventResult {
    type ProjectCommitEventResult = EventResult[ProjectCommitData]

    def fromCommitSchema(
        commitSchema: CommitSchema,
        numberOfFiles: Int,
        additions: Int,
        deletions: Int,
        datasetName: String,
        relatedConstructs: Seq[ItemLink]
    ): ProjectCommitEventResult = {
        val commitEvent: ProjectCommitEvent = new ProjectCommitEvent(
            commitSchema,
            numberOfFiles,
            additions,
            deletions,
            datasetName,
            relatedConstructs
        )
        EventResult.fromEvent(commitEvent, commitEvent.data)
    }
}
