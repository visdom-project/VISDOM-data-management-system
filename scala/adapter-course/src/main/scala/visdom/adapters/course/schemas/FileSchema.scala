// Copyright 2022 Tampere University
// This software was developed as a part of the VISDOM project: https://iteavisdom.org/
// This source code is licensed under the MIT license. See LICENSE in the repository root directory.
// Author(s): Ville Heikkilä <ville.heikkila@tuni.fi>

package visdom.adapters.course.schemas

import visdom.spark.FieldDataType
import visdom.utils.GeneralUtils.toStringOption
import visdom.utils.SnakeCaseConstants
import visdom.utils.TupleUtils.EnrichedWithToTuple
import visdom.utils.TupleUtils.toOption
import visdom.utils.WartRemoverConstants


final case class FileSchema(
    path: String,
    project_name: String,
    host_name: String,
    _links: Option[CommitIdListSchema]
)
extends BaseSchema

object FileSchema extends BaseSchemaTrait[FileSchema] {
    def fields: Seq[FieldDataType] = Seq(
        FieldDataType(SnakeCaseConstants.Path, false),
        FieldDataType(SnakeCaseConstants.ProjectName, false),
        FieldDataType(SnakeCaseConstants.HostName, false),
        FieldDataType(SnakeCaseConstants.Links, true)
    )

    @SuppressWarnings(Array(WartRemoverConstants.WartsAny))
    def transformValues(valueOptions: Seq[Option[Any]]): Option[FileSchema] = {
        val (pathOption, projectNameOption, hostNameOption, linksOption) = valueOptions.toTuple4
        toOption(
            (pathOption, projectNameOption, hostNameOption),
            (
                (value: Any) => toStringOption(value),
                (value: Any) => toStringOption(value),
                (value: Any) => toStringOption(value)
            )
        ) match {
            case Some((path: String, project_name: String, host_name: String)) =>
                Some(
                    FileSchema(
                        path,
                        project_name,
                        host_name,
                        CommitIdListSchema.fromAny(linksOption)
                    )
                )
            case _ => None
        }
    }
}
