package visdom.http.server.fetcher.aplus

import spray.json.JsObject
import visdom.http.server.QueryOptionsBase
import visdom.http.server.services.constants.APlusServerConstants
import visdom.json.JsonUtils


final case class CourseDataQueryOptions(
    courseId: Option[String],
    parseNames: String,
    includeModules: String,
    includeExercises: String,
    includeSubmissions: String,
    useAnonymization: String,
    gdprExerciseId: Option[String],
    gdprFieldName: String,
    gdprAcceptedAnswer: String
) extends QueryOptionsBase {
    def toJsObject(): JsObject = {
        JsObject(
            Map(
                APlusServerConstants.CourseId -> JsonUtils.toJsonValue(courseId),
                APlusServerConstants.ParseNames -> JsonUtils.toJsonValue(parseNames),
                APlusServerConstants.IncludeModules -> JsonUtils.toJsonValue(includeModules),
                APlusServerConstants.IncludeExercises -> JsonUtils.toJsonValue(includeExercises),
                APlusServerConstants.IncludeSubmissions -> JsonUtils.toJsonValue(includeSubmissions),
                APlusServerConstants.UseAnonymization -> JsonUtils.toJsonValue(useAnonymization),
                APlusServerConstants.GDPRExerciseId -> JsonUtils.toJsonValue((gdprExerciseId)),
                APlusServerConstants.GDPRFieldName -> JsonUtils.toJsonValue((gdprFieldName)),
                APlusServerConstants.GDPRAcceptedAnswer -> JsonUtils.toJsonValue((gdprAcceptedAnswer))
            )
        )
    }
}