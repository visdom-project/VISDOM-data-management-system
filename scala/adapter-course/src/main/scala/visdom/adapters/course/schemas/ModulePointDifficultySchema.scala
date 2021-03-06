// Copyright 2022 Tampere University
// This software was developed as a part of the VISDOM project: https://iteavisdom.org/
// This source code is licensed under the MIT license. See LICENSE in the repository root directory.
// Author(s): Ville Heikkilä <ville.heikkila@tuni.fi>

package visdom.adapters.course.schemas

import visdom.spark.FieldDataType
import visdom.utils.CommonConstants
import visdom.utils.GeneralUtils.toIntOption
import visdom.utils.SnakeCaseConstants
import visdom.utils.TupleUtils.EnrichedWithToTuple
import visdom.utils.WartRemoverConstants


final case class ModulePointDifficultySchema(
    category: Option[Int],
    categoryG: Option[Int],
    categoryP: Option[Int]
)
extends BaseSchema

object ModulePointDifficultySchema extends BaseSchemaTrait[ModulePointDifficultySchema] {
    def fields: Seq[FieldDataType] = Seq(
        FieldDataType(SnakeCaseConstants.Category, true),
        FieldDataType(SnakeCaseConstants.CategoryG, true),
        FieldDataType(SnakeCaseConstants.CategoryP, true)
    )

    @SuppressWarnings(Array(WartRemoverConstants.WartsAny))
    def transformValues(valueOptions: Seq[Option[Any]]): Option[ModulePointDifficultySchema] = {
        val (emptyOption, gOption, pOption) =
            valueOptions
                .map(valueOption => toIntOption(valueOption))
                .toTuple3
        Some(ModulePointDifficultySchema(emptyOption, gOption, pOption))
    }
}
