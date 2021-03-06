// Copyright 2022 Tampere University
// This software was developed as a part of the VISDOM project: https://iteavisdom.org/
// This source code is licensed under the MIT license. See LICENSE in the repository root directory.
// Author(s): Ville Heikkilä <ville.heikkila@tuni.fi>

package visdom.adapters.course.structs


final case class GradeCumulativeDataCounts(
    students: Int,
    weeks: Map[String, ModuleCumulativeDataCounts[Float]]
)

object GradeCumulativeDataCounts {
    def fromGradeDataCounts(gradeDataCounts: GradeDataCounts): GradeCumulativeDataCounts = {
        val gradeDataCountsWithIndex: Map[String,(ModuleDataCounts[Float], Int)] = gradeDataCounts
            .weeks
            .toSeq
            .sortBy({case (week, _) => week})
            .zipWithIndex
            .map({case ((week, data), index) => (week, (data, index))})
            .toMap

        GradeCumulativeDataCounts(
            students = gradeDataCounts.students,
            weeks = gradeDataCountsWithIndex
                .mapValues({
                    case (data, index) => (
                        data,
                        gradeDataCountsWithIndex
                            .filter({case (_, (_, otherIndex)) => otherIndex < index})
                            .map({case (_, (otherData, _)) => otherData})
                            .reduceOption((data1, data2) => data1.add(data2))
                    )
                })
                .mapValues({
                    case (currentData, previousData) =>
                        ModuleCumulativeDataCounts.fromModuleDataCounts(currentData, previousData)
                })
        )
    }
}
