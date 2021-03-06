// Copyright 2022 Tampere University
// This software was developed as a part of the VISDOM project: https://iteavisdom.org/
// This source code is licensed under the MIT license. See LICENSE in the repository root directory.
// Author(s): Ville Heikkilä <ville.heikkila@tuni.fi>

package visdom.adapters.general.model.metadata

import visdom.adapters.general.model.base.ItemLink
import visdom.adapters.general.model.base.Metadata
import visdom.adapters.general.model.metadata.data.ModuleData
import visdom.adapters.general.model.origins.AplusOrigin
import visdom.adapters.general.schemas.ModuleAdditionalSchema
import visdom.adapters.general.schemas.ModuleSchema
import visdom.utils.GeneralUtils


class ModuleMetadata(
    moduleSchema: ModuleSchema,
    moduleAdditionalSchema: ModuleAdditionalSchema
)
extends Metadata {
    def getType: String = ModuleMetadata.ModuleMetadataType

    val origin: ItemLink =
        new AplusOrigin(
            moduleSchema.host_name,
            moduleSchema.course_id,
            None
        ).link

    val name: String = moduleSchema.display_name.en match {
        case Some(englishName: String) => englishName
        case None => moduleSchema.display_name.fi match {
            case Some(finnishName: String) => finnishName
            case None => moduleSchema.display_name.raw
        }
    }
    val description: String = moduleSchema.display_name.raw

    val data: ModuleData = ModuleData.fromModuleSchema(moduleSchema, moduleAdditionalSchema)

    val id: String = ModuleMetadata.getId(origin.id, data.module_id)

    // add links to the related exercise and course metadata
    addRelatedConstructs(
        data.exercises.map(
            exerciseId => ItemLink(
                ExerciseMetadata.getId(origin.id, exerciseId),
                ExerciseMetadata.ExerciseMetadataType
            )
        ) :+
        ItemLink(
            CourseMetadata.getId(origin.id, data.course_id),
            CourseMetadata.CourseMetadataType
        )
    )
}

object ModuleMetadata {
    final val ModuleMetadataType: String = "module"

    def getId(originId: String, moduleId: Int): String = {
        GeneralUtils.getUuid(originId, ModuleMetadataType, moduleId.toString())
    }

    def fromModuleSchema(moduleSchema: ModuleSchema, additionalSchema: ModuleAdditionalSchema): ModuleMetadata = {
        new ModuleMetadata(moduleSchema, additionalSchema)
    }
}
