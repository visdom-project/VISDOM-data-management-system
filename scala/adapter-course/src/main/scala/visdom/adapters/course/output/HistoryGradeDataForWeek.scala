package visdom.adapters.course.output

import spray.json.JsObject
import visdom.json.JsonUtils
import visdom.utils.SnakeCaseConstants


final case class HistoryGradeDataForWeek(
    student_counts: Seq[Int],
    avg_points: Seq[Float],
    avg_exercises: Seq[Float],
    avg_submissions: Seq[Float],
    avg_commits: Seq[Float],
    avg_cum_points: Seq[Float],
    avg_cum_exercises: Seq[Float],
    avg_cum_submissions: Seq[Float],
    avg_cum_commits: Seq[Float]
) extends HistoryData(
    avg_points,
    avg_exercises,
    avg_submissions,
    avg_commits,
    avg_cum_points,
    avg_cum_exercises,
    avg_cum_submissions,
    avg_cum_commits
) {
    override def toJsObject(): JsObject = {
        JsObject(
            super.toJsObject().fields ++
            Map(SnakeCaseConstants.StudentCounts -> JsonUtils.toJsonValue(student_counts))
        )
    }
}
