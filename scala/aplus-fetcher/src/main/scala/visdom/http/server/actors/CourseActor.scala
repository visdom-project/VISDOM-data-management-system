// Copyright 2022 Tampere University
// This software was developed as a part of the VISDOM project: https://iteavisdom.org/
// This source code is licensed under the MIT license. See LICENSE in the repository root directory.
// Author(s): Ville Heikkilä <ville.heikkila@tuni.fi>

package visdom.http.server.actors

import akka.actor.Actor
import akka.actor.ActorLogging
import java.time.ZonedDateTime
import org.mongodb.scala.bson.collection.immutable.Document
import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import visdom.fetchers.aplus.APlusCourseOptions
import visdom.fetchers.aplus.CoursesFetcher
import visdom.fetchers.aplus.CourseSpecificFetchParameters
import visdom.fetchers.aplus.FetcherValues
import visdom.fetchers.aplus.GdprOptions
import visdom.http.server.CommonHelpers
import visdom.http.server.ResponseUtils
import visdom.http.server.fetcher.aplus.CourseDataQueryOptions
import visdom.http.server.response.StatusResponse
import visdom.http.server.services.constants.APlusFetcherDescriptions
import visdom.http.server.services.constants.APlusServerConstants
import visdom.utils.WartRemoverConstants


class CourseActor extends Actor with ActorLogging {
    implicit val ec: ExecutionContext = ExecutionContext.global

    @SuppressWarnings(Array(WartRemoverConstants.WartsAny))
    def receive: Receive = {
        case queryOptions: CourseDataQueryOptions => {
            log.info(s"Received courses query with options: ${queryOptions.toString()}")
            val response: StatusResponse = CourseActor.getFetchOptions(queryOptions) match {
                case Right(fetchParameters: CourseSpecificFetchParameters) => {
                    // start the course data fetching
                    val courseFetching = Future(CourseActor.startCourseFetching(fetchParameters))

                    ResponseUtils.getAcceptedResponse(
                        APlusFetcherDescriptions.StatusAcceptedDescription,
                        queryOptions.toJsObject()
                    )
                }
                case Left(errorDescription: String) => ResponseUtils.getInvalidResponse(errorDescription)
            }
            sender() ! response
        }
    }
}

object CourseActor {
    def checkQueryOptions(queryOptions: CourseDataQueryOptions): Option[String] = {
        val nonBooleanParameter: Option[(String, String)] = CommonHelpers.getNonBooleanParameter(
            Seq(
                (APlusServerConstants.ParseNames, queryOptions.parseNames),
                (APlusServerConstants.IncludeModules, queryOptions.includeModules),
                (APlusServerConstants.IncludeExercises, queryOptions.includeExercises),
                (APlusServerConstants.IncludeSubmissions, queryOptions.includeSubmissions),
                (APlusServerConstants.IncludeGitlabData, queryOptions.includeGitlabData),
                (APlusServerConstants.IncludePoints, queryOptions.includePoints),
                (APlusServerConstants.UseAnonymization, queryOptions.useAnonymization)
            )
        )

        if (!CommonHelpers.isCourseId(queryOptions.courseId)) {
            Some(s"'${queryOptions.courseId}' is not a valid course id")
        }
        else if (nonBooleanParameter.isDefined) {
            nonBooleanParameter.map({case (name, value) => s"'${value}' is not a valid value for ${name}"})
        }
        else if (!CommonHelpers.areGdprOptions(
            queryOptions.gdprExerciseId,
            queryOptions.gdprFieldName
        )) {
            Some(
                s"'${queryOptions.gdprExerciseId}', '${queryOptions.gdprFieldName}' " +
                s"and '${queryOptions.gdprAcceptedAnswer}' are not a valid values for the GDPR parameters"
            )
        }
        else {
            None
        }
    }

    def getFetchOptions(queryOptions: CourseDataQueryOptions): Either[String, CourseSpecificFetchParameters] = {
        checkQueryOptions(queryOptions) match {
            case Some(errorMessage: String) => Left(errorMessage)
            case None =>
                Right(
                    CourseSpecificFetchParameters(
                        courseId = queryOptions.courseId match {
                            case Some(courseIdString: String) => Some(courseIdString.toInt)
                            case None => None
                        },
                        parseNames = queryOptions.parseNames.toBoolean,
                        includeModules = queryOptions.includeModules.toBoolean,
                        includeExercises = queryOptions.includeExercises.toBoolean,
                        includeSubmissions = queryOptions.includeSubmissions.toBoolean,
                        includeGitlabData = queryOptions.includeGitlabData.toBoolean,
                        includePoints = queryOptions.includePoints.toBoolean,
                        useAnonymization = queryOptions.useAnonymization.toBoolean,
                        gdprOptions = queryOptions.gdprExerciseId match {
                            case Some(gdprExerciseId: String) => Some(
                                GdprOptions(
                                    exerciseId = gdprExerciseId.toInt,
                                    fieldName = queryOptions.gdprFieldName,
                                    acceptedAnswer = queryOptions.gdprAcceptedAnswer,
                                    users = None
                                )
                            )
                            case None => None
                        }
                    )
                )
        }
    }

    def startCourseFetching(fetchParameters: CourseSpecificFetchParameters): Unit = {
        val courseFetcherOptions: APlusCourseOptions = APlusCourseOptions(
            hostServer = FetcherValues.targetServer,
            mongoDatabase = Some(FetcherValues.targetDatabase),
            courseId = fetchParameters.courseId,
            parseNames = fetchParameters.parseNames,
            includeModules = fetchParameters.includeModules,
            includeExercises = fetchParameters.includeExercises,
            includeSubmissions = fetchParameters.includeSubmissions,
            includeGitlabData = fetchParameters.includeGitlabData,
            includePoints = fetchParameters.includePoints,
            useAnonymization = fetchParameters.useAnonymization,
            gdprOptions = fetchParameters.gdprOptions
        )
        FetcherValues.fetcherList.addFetcher(new CoursesFetcher(courseFetcherOptions))
    }
}
