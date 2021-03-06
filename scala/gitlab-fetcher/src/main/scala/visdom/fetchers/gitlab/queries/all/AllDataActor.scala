// Copyright 2022 Tampere University
// This software was developed as a part of the VISDOM project: https://iteavisdom.org/
// This source code is licensed under the MIT license. See LICENSE in the repository root directory.
// Author(s): Ville Heikkilä <ville.heikkila@tuni.fi>

package visdom.fetchers.gitlab.queries.all

import akka.actor.Actor
import akka.actor.ActorLogging
import java.time.ZonedDateTime
import scala.concurrent.ExecutionContext
import visdom.fetchers.FetcherUtils
import visdom.fetchers.gitlab.AllDataSpecificFetchParameters
import visdom.fetchers.gitlab.CommitSpecificFetchParameters
import visdom.fetchers.gitlab.FileSpecificFetchParameters
import visdom.fetchers.gitlab.GitlabConstants
import visdom.fetchers.gitlab.PipelinesSpecificFetchParameters
import visdom.fetchers.gitlab.ProjectSpecificFetchParameters
import visdom.fetchers.gitlab.queries.CommonHelpers
import visdom.fetchers.gitlab.queries.Constants
import visdom.fetchers.gitlab.queries.commits.CommitActor
import visdom.fetchers.gitlab.queries.files.FileActor
import visdom.fetchers.gitlab.queries.pipelines.PipelinesActor
import visdom.fetchers.gitlab.queries.project.ProjectActor
import visdom.http.server.fetcher.gitlab.AllDataQueryOptions
import visdom.http.server.response.StatusResponse
import visdom.http.server.ResponseUtils
import visdom.utils.TimeUtils
import visdom.utils.WartRemoverConstants


class AllDataActor extends Actor with ActorLogging {
    implicit val ec: ExecutionContext = ExecutionContext.global

    @SuppressWarnings(Array(WartRemoverConstants.WartsAny))
    def receive: Receive = {
        case queryOptions: AllDataQueryOptions => {
            log.info(s"Received all data query with options: ${queryOptions.toString()}")
            val response: StatusResponse = AllDataActor.getFetchOptions(queryOptions) match {
                case Right(fetchParameters: AllDataSpecificFetchParameters) => {
                    CommonHelpers.checkProjectAvailability(fetchParameters.projectName) match {
                        case GitlabConstants.StatusCodeOk => {
                            // start the data fetching
                            FetcherUtils.handleDataFetchingSequence(AllDataActor.dataFetchers, fetchParameters)
                            // return the accepted response
                            ResponseUtils.getAcceptedResponse(
                                AllDataConstants.AllDataStatusAcceptedDescription,
                                queryOptions.toJsObject()
                            )
                        }
                        case GitlabConstants.StatusCodeUnauthorized => ResponseUtils.getUnauthorizedResponse(
                            s"Access to project '${fetchParameters.projectName}' not allowed"
                        )
                        case GitlabConstants.StatusCodeNotFound => ResponseUtils.getNotFoundResponse(
                            s"Project '${fetchParameters.projectName}' not found"
                        )
                        case _ => ResponseUtils.getErrorResponse(Constants.StatusErrorDescription)
                    }
                }
                case Left(errorDescription: String) => ResponseUtils.getInvalidResponse(errorDescription)
            }
            sender() ! response
        }
    }
}

object AllDataActor {
    implicit val ec: ExecutionContext = ExecutionContext.global

    // scalastyle:off cyclomatic.complexity
    def getFetchOptions(queryOptions: AllDataQueryOptions): Either[String, AllDataSpecificFetchParameters] = {
        val startDate: Option[ZonedDateTime] = TimeUtils.toZonedDateTime(queryOptions.startDate)
        val endDate: Option[ZonedDateTime] = TimeUtils.toZonedDateTime(queryOptions.endDate)

        if (!CommonHelpers.isProjectName(queryOptions.projectName)) {
            Left(s"'${queryOptions.projectName}' is not a valid project name")
        }
        else if (!CommonHelpers.isReference(queryOptions.reference)) {
            Left(s"'${queryOptions.reference}' is not a valid reference for a project")
        }
        else if (queryOptions.startDate.isDefined && !startDate.isDefined) {
            Left(s"'${queryOptions.startDate.getOrElse("")}' is not valid datetime in ISO 8601 format with timezone")
        }
        else if (queryOptions.endDate.isDefined && !endDate.isDefined) {
            Left(s"'${queryOptions.endDate.getOrElse("")}' is not valid datetime in ISO 8601 format with timezone")
        }
        else if (startDate.isDefined && endDate.isDefined && !TimeUtils.lessOrEqual(startDate, endDate)) {
            Left("the endDate must be later than the startDate")
        }
        else if (!Constants.BooleanStrings.contains(queryOptions.useAnonymization)) {
            Left(s"'${queryOptions.useAnonymization}' is not valid value for useAnonymization")
        }
        else {
            Right(AllDataSpecificFetchParameters(
                projectName = queryOptions.projectName,
                reference = queryOptions.reference,
                startDate = startDate,
                endDate = endDate,
                useAnonymization = queryOptions.useAnonymization.toBoolean
            ))
        }
    }
    // scalastyle:on cyclomatic.complexity

    def startProjectFetching(fetchParameters: AllDataSpecificFetchParameters): Unit = {
        val projectFetchParameters = ProjectSpecificFetchParameters(
            projectIdentifier = Left(fetchParameters.projectName),
            useAnonymization = fetchParameters.useAnonymization
        )
        ProjectActor.startProjectFetching(projectFetchParameters)
    }

    def startCommitFetching(fetchParameters: AllDataSpecificFetchParameters): Unit = {
        val commitFetchParameters = CommitSpecificFetchParameters(
            projectName = fetchParameters.projectName,
            reference = fetchParameters.reference,
            startDate = fetchParameters.startDate,
            endDate = fetchParameters.endDate,
            filePath = AllDataConstants.ParameterDefaultFilePath,
            includeStatistics = AllDataConstants.ParameterDefaultIncludeStatistics,
            includeFileLinks = AllDataConstants.ParameterDefaultIncludeFileLinks,
            includeReferenceLinks = AllDataConstants.ParameterDefaultIncludeReferenceLinks,
            useAnonymization = fetchParameters.useAnonymization
        )
        CommitActor.startCommitFetching(commitFetchParameters)
    }

    def startFileFetching(fetchParameters: AllDataSpecificFetchParameters): Unit = {
        val fileFetchParameters = FileSpecificFetchParameters(
            projectName = fetchParameters.projectName,
            reference = fetchParameters.reference,
            filePath = AllDataConstants.ParameterDefaultFilePath,
            recursive = AllDataConstants.ParameterDefaultRecursive,
            includeCommitLinks = AllDataConstants.ParameterDefaultIncludeCommitLinks,
            useAnonymization = fetchParameters.useAnonymization
        )
        FileActor.startFileFetching(fileFetchParameters)
    }

    def startPipelineFetching(fetchParameters: AllDataSpecificFetchParameters): Unit = {
        val pipelineFetchParameters = PipelinesSpecificFetchParameters(
            projectName = fetchParameters.projectName,
            reference = fetchParameters.reference,
            startDate = fetchParameters.startDate,
            endDate = fetchParameters.endDate,
            includeReports = AllDataConstants.ParameterDefaultIncludeReports,
            includeJobs = AllDataConstants.ParameterDefaultIncludeJobs,
            includeJobLogs = AllDataConstants.ParameterDefaultIncludeJobLogs,
            useAnonymization = fetchParameters.useAnonymization
        )
        PipelinesActor.startPipelineFetching(pipelineFetchParameters)
    }

    private val dataFetchers: Seq[AllDataSpecificFetchParameters => Unit] = Seq(
        AllDataActor.startProjectFetching(_),
        AllDataActor.startCommitFetching(_),
        AllDataActor.startFileFetching(_),
        AllDataActor.startPipelineFetching(_)
    )
}
