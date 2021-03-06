// Copyright 2022 Tampere University
// This software was developed as a part of the VISDOM project: https://iteavisdom.org/
// This source code is licensed under the MIT license. See LICENSE in the repository root directory.
// Author(s): Ville Heikkilä <ville.heikkila@tuni.fi>

package visdom.adapters.general.model.metadata.data

import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.bson.BsonValue
import spray.json.JsObject
import spray.json.JsValue
import visdom.adapters.general.model.base.Data
import visdom.adapters.general.schemas.ExerciseAdditionalSchema
import visdom.adapters.general.schemas.ExerciseSchema
import visdom.json.JsonUtils
import visdom.utils.CommonConstants
import visdom.utils.GeneralUtils
import visdom.utils.SnakeCaseConstants
import visdom.utils.TimeUtils
import visdom.utils.WartRemoverConstants


final case class ExerciseData(
    exercise_id: Int,
    exercise_number: String,
    url: String,
    html_url: String,
    is_submittable: Boolean,
    difficulty: Option[String],
    start_date: Option[String],
    end_date: Option[String],
    max_points: Int,
    max_submissions: Int,
    points_to_pass: Option[Int],
    git_path: Option[String],
    git_is_folder: Option[Boolean],
    course_id: Int,
    module_id: Int
)
extends Data {
    def toBsonValue(): BsonValue = {
        BsonDocument(
            Map(
                SnakeCaseConstants.ExerciseId -> JsonUtils.toBsonValue(exercise_id),
                SnakeCaseConstants.ExerciseNumber -> JsonUtils.toBsonValue(exercise_number),
                SnakeCaseConstants.Url -> JsonUtils.toBsonValue(url),
                SnakeCaseConstants.HtmlUrl -> JsonUtils.toBsonValue(html_url),
                SnakeCaseConstants.IsSubmittable -> JsonUtils.toBsonValue(is_submittable),
                SnakeCaseConstants.Difficulty -> JsonUtils.toBsonValue(difficulty),
                SnakeCaseConstants.StartDate -> JsonUtils.toBsonValue(start_date),
                SnakeCaseConstants.EndDate -> JsonUtils.toBsonValue(end_date),
                SnakeCaseConstants.MaxPoints -> JsonUtils.toBsonValue(max_points),
                SnakeCaseConstants.MaxSubmissions -> JsonUtils.toBsonValue(max_submissions),
                SnakeCaseConstants.PointsToPass -> JsonUtils.toBsonValue(points_to_pass),
                SnakeCaseConstants.GitPath -> JsonUtils.toBsonValue(git_path),
                SnakeCaseConstants.GitIsFolder -> JsonUtils.toBsonValue(git_is_folder),
                SnakeCaseConstants.CourseId -> JsonUtils.toBsonValue(course_id),
                SnakeCaseConstants.ModuleId -> JsonUtils.toBsonValue(module_id)
            )
        )
    }

    @SuppressWarnings(Array(WartRemoverConstants.WartsAny))
    def toJsValue(): JsValue = {
        JsObject(
            Map(
                SnakeCaseConstants.ExerciseId -> JsonUtils.toJsonValue(exercise_id),
                SnakeCaseConstants.ExerciseNumber -> JsonUtils.toJsonValue(exercise_number),
                SnakeCaseConstants.Url -> JsonUtils.toJsonValue(url),
                SnakeCaseConstants.HtmlUrl -> JsonUtils.toJsonValue(html_url),
                SnakeCaseConstants.IsSubmittable -> JsonUtils.toJsonValue(is_submittable),
                SnakeCaseConstants.Difficulty -> JsonUtils.toJsonValue(difficulty),
                SnakeCaseConstants.StartDate -> JsonUtils.toJsonValue(start_date),
                SnakeCaseConstants.EndDate -> JsonUtils.toJsonValue(end_date),
                SnakeCaseConstants.MaxPoints -> JsonUtils.toJsonValue(max_points),
                SnakeCaseConstants.MaxSubmissions -> JsonUtils.toJsonValue(max_submissions),
                SnakeCaseConstants.PointsToPass -> JsonUtils.toJsonValue(points_to_pass),
                SnakeCaseConstants.GitPath -> JsonUtils.toJsonValue(git_path),
                SnakeCaseConstants.GitIsFolder -> JsonUtils.toJsonValue(git_is_folder),
                SnakeCaseConstants.CourseId -> JsonUtils.toJsonValue(course_id),
                SnakeCaseConstants.ModuleId -> JsonUtils.toJsonValue(module_id)
            )
        )
    }
}

object ExerciseData {
    def dateStringToIsoFormat(dateString: String): String = {
        // If the input is of format '{"$date": 1589792400000}' converts it to ISO 8601 string.
        // Otherwise, returns the input unchanged.
        dateString.contains(SnakeCaseConstants.Date) match {
            case true => {
                GeneralUtils.toLongOption(
                    dateString
                        .replaceAll(CommonConstants.CurlyBracketEnd, CommonConstants.EmptyString)
                        .split(CommonConstants.DoubleDot)
                        .lastOption
                        .getOrElse(CommonConstants.EmptyString)
                        .trim()
                ) match {
                    case Some(unixTime: Long) => TimeUtils.fromEpochMilliToString(unixTime)
                    case None => dateString
                }
            }
            case false => dateString
        }
    }

    def fromExerciseSchema(
        exerciseSchema: ExerciseSchema,
        additionalSchema: ExerciseAdditionalSchema
    ): ExerciseData = {
        ExerciseData(
            exercise_id = exerciseSchema.id,
            exercise_number = exerciseSchema.display_name.number.getOrElse(CommonConstants.EmptyString),
            url = exerciseSchema.url,
            html_url = exerciseSchema.html_url,
            is_submittable = exerciseSchema.is_submittable,
            difficulty = additionalSchema.difficulty,
            start_date = additionalSchema.start_date.map(startDate => dateStringToIsoFormat(startDate)),
            end_date = additionalSchema.end_date.map(endDate => dateStringToIsoFormat(endDate)),
            max_points = exerciseSchema.max_points,
            max_submissions = exerciseSchema.max_submissions,
            points_to_pass = additionalSchema.points_to_pass,
            git_path = exerciseSchema.metadata.other.map(other => other.path),
            git_is_folder = exerciseSchema.metadata.other.map(other => other.is_folder),
            course_id = exerciseSchema.course.id,
            module_id = exerciseSchema._links.map(links => links.modules).flatten.getOrElse(0)
        )
    }
}
