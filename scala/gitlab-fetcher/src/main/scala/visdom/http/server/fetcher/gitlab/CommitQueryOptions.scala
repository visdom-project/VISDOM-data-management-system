package visdom.http.server.fetcher.gitlab

import spray.json.JsObject
import visdom.http.server.AttributeConstants
import visdom.http.server.QueryOptionsBase
import visdom.json.JsonUtils


final case class CommitQueryOptions(
    projectName: String,
    reference: String,
    startDate: Option[String],
    endDate: Option[String],
    filePath: Option[String],
    includeStatistics: String,
    includeFileLinks: String,
    includeReferenceLinks: String,
    useAnonymization: String
) extends QueryOptionsBase {
    def toJsObject(): JsObject = {
        JsObject(
            Map(
                AttributeConstants.ProjectName -> JsonUtils.toJsonValue(projectName),
                AttributeConstants.Reference -> JsonUtils.toJsonValue(reference),
                AttributeConstants.StartDate -> JsonUtils.toJsonValue(startDate),
                AttributeConstants.EndDate -> JsonUtils.toJsonValue(endDate),
                AttributeConstants.FilePath -> JsonUtils.toJsonValue(filePath),
                AttributeConstants.IncludeStatistics -> JsonUtils.toJsonValue(includeStatistics),
                AttributeConstants.IncludeFileLinks -> JsonUtils.toJsonValue(includeFileLinks),
                AttributeConstants.IncludeReferenceLinks -> JsonUtils.toJsonValue(includeReferenceLinks),
                AttributeConstants.UseAnonymization -> JsonUtils.toJsonValue(useAnonymization)
            )
        )
    }
}
