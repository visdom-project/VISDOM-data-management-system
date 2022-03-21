package visdom.adapters.general.model.artifacts.data


import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.bson.BsonValue
import spray.json.JsObject
import spray.json.JsValue
import visdom.adapters.general.model.base.Data
import visdom.adapters.general.schemas.PointsModuleSchema
import visdom.json.JsonUtils
import visdom.utils.SnakeCaseConstants


final case class ModulePointsData(
    module_id: Int,
    user_id: Int,
    submission_count: Int,
    points: Int,
    points_by_difficulty: PointsByDifficulty,
    passed: Boolean
)
extends Data {
    def toBsonValue(): BsonValue = {
        BsonDocument(
            Map(
                SnakeCaseConstants.ModuleId -> JsonUtils.toBsonValue(module_id),
                SnakeCaseConstants.UserId -> JsonUtils.toBsonValue(user_id),
                SnakeCaseConstants.SubmissionCount -> JsonUtils.toBsonValue(submission_count),
                SnakeCaseConstants.Points -> JsonUtils.toBsonValue(points),
                SnakeCaseConstants.PointsByDifficulty -> points_by_difficulty.toBsonValue(),
                SnakeCaseConstants.Passed -> JsonUtils.toBsonValue(passed)
            )
        )
    }

    def toJsValue(): JsValue = {
        JsObject(
            Map(
                SnakeCaseConstants.ModuleId -> JsonUtils.toJsonValue(module_id),
                SnakeCaseConstants.UserId -> JsonUtils.toJsonValue(user_id),
                SnakeCaseConstants.SubmissionCount -> JsonUtils.toJsonValue(submission_count),
                SnakeCaseConstants.Points -> JsonUtils.toJsonValue(points),
                SnakeCaseConstants.PointsByDifficulty -> points_by_difficulty.toJsValue(),
                SnakeCaseConstants.Passed -> JsonUtils.toJsonValue(passed)
            )
        )
    }
}

object ModulePointsData {
    def fromPointsSchema(modulePointsSchema: PointsModuleSchema, userId: Int): ModulePointsData = {
        ModulePointsData(
            module_id = modulePointsSchema.id,
            user_id = userId,
            submission_count = modulePointsSchema.submission_count,
            points = modulePointsSchema.points,
            points_by_difficulty = PointsByDifficulty(
                categoryN = modulePointsSchema.points_by_difficulty.category,
                categoryP = modulePointsSchema.points_by_difficulty.categoryP,
                categoryG = modulePointsSchema.points_by_difficulty.categoryG
            ),
            passed = modulePointsSchema.passed
        )
    }
}