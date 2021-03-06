// Copyright 2022 Tampere University
// This software was developed as a part of the VISDOM project: https://iteavisdom.org/
// This source code is licensed under the MIT license. See LICENSE in the repository root directory.
// Author(s): Ville Heikkilä <ville.heikkila@tuni.fi>

package visdom.adapters.general.model.events.data

import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.bson.BsonValue
import spray.json.JsObject
import spray.json.JsValue
import visdom.adapters.general.schemas.CommitStatsSchema
import visdom.adapters.results.BaseResultValue
import visdom.json.JsonUtils
import visdom.utils.SnakeCaseConstants


final case class CommitStats(
    additions: Int,
    deletions: Int,
    total: Int
)
extends BaseResultValue {
    def toBsonValue(): BsonValue = {
        BsonDocument(
            Map(
                SnakeCaseConstants.Additions -> JsonUtils.toBsonValue(additions),
                SnakeCaseConstants.Deletions -> JsonUtils.toBsonValue(deletions),
                SnakeCaseConstants.Total -> JsonUtils.toBsonValue(total)
            )
        )
    }

    def toJsValue(): JsValue = {
        JsObject(
            Map(
                SnakeCaseConstants.Additions -> JsonUtils.toJsonValue(additions),
                SnakeCaseConstants.Deletions -> JsonUtils.toJsonValue(deletions),
                SnakeCaseConstants.Total -> JsonUtils.toJsonValue(total)
            )
        )
    }
}

object CommitStats {
    def getEmpty(): CommitStats = {
        CommitStats(0, 0, 0)
    }

    def fromCommitStatsSchema(commitStatsSchema: CommitStatsSchema): CommitStats = {
        CommitStats(
            additions = commitStatsSchema.additions,
            deletions = commitStatsSchema.deletions,
            total = commitStatsSchema.total
        )
    }
}
