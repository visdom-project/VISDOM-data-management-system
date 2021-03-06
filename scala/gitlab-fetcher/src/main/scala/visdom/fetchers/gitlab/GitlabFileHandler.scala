// Copyright 2022 Tampere University
// This software was developed as a part of the VISDOM project: https://iteavisdom.org/
// This source code is licensed under the MIT license. See LICENSE in the repository root directory.
// Author(s): Ville Heikkilä <ville.heikkila@tuni.fi>

package visdom.fetchers.gitlab

import scalaj.http.Http
import scalaj.http.HttpConstants.utf8
import scalaj.http.HttpConstants.urlEncode
import scalaj.http.HttpRequest
import scala.collection.JavaConverters.seqAsJavaListConverter
import org.mongodb.scala.bson.BsonArray
import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.bson.BsonString
import org.mongodb.scala.bson.BsonValue
import org.mongodb.scala.bson.Document
import visdom.database.mongodb.MongoConstants
import visdom.json.JsonUtils.EnrichedBsonDocument
import visdom.json.JsonUtils.toBsonValue
import visdom.utils.CommonConstants
import visdom.utils.GeneralUtils
import visdom.utils.WartRemoverConstants


class GitlabFileHandler(options: GitlabFileOptions)
    extends GitlabDataHandler(options) {

    def getFetcherType(): String = GitlabConstants.FetcherTypeFiles
    def getCollectionName(): String = MongoConstants.CollectionFiles

    override def getOptionsDocument(): BsonDocument = {
        BsonDocument(
            GitlabConstants.AttributeReference -> options.reference,
            GitlabConstants.AttributeRecursive -> options.recursive,
            GitlabConstants.AttributeIncludeLinksCommits -> options.includeCommitLinks,
            GitlabConstants.AttributeUseAnonymization -> options.useAnonymization
        )
        .appendOption(
            GitlabConstants.AttributeFilePath,
            options.filePath.map(stringValue => toBsonValue(stringValue))
        )
    }

    def getRequest(): HttpRequest = {
        // https://docs.gitlab.com/ee/api/repositories.html#list-repository-tree
        val uri: String = List(
            options.hostServer.baseAddress,
            GitlabConstants.PathProjects,
            urlEncode(options.projectName, utf8),
            GitlabConstants.PathRepository,
            GitlabConstants.PathTree
        ).mkString(CommonConstants.Slash)

        val commitRequest: HttpRequest = processOptionalParameters(
            Http(uri)
                .param(GitlabConstants.ParamRef, options.reference)
                .param(GitlabConstants.ParamRecursive, options.recursive.toString())
        )
        options.hostServer.modifyRequest(commitRequest)
    }

    override def getIdentifierAttributes(): Array[String] = {
        Array(
            GitlabConstants.ParamPath,
            GitlabConstants.AttributeProjectName,
            GitlabConstants.AttributeHostName
        )
    }

    override def getHashableAttributes(): Option[Seq[Seq[String]]] = {
        options.useAnonymization match {
            case true => Some(
                Seq(
                    Seq(GitlabConstants.AttributeProjectName)
                )
            )
            case false => None
        }
    }

    override def processDocument(document: BsonDocument): BsonDocument = {
        val resultFailCheck: Boolean = options.filePath match {
            case Some(filePath: String) => document.getStringOption(GitlabConstants.AttributePath) match {
                case Some(documentPath: String) => !documentPath.startsWith(filePath)
                case None => true  // result document did not contain string valued path attribute
            }
            case None => false  // all documents are accepted if filePath options is not used
        }

        if (resultFailCheck) {
            BsonDocument()  // empty result will be discarded
        }
        else {
            processDocumentInternal(document)
        }
    }

    private def processDocumentInternal(document: BsonDocument): BsonDocument = {
        val filePathOption: Option[String] = document.getStringOption(GitlabConstants.AttributePath)
        val linkDocumentOption: Option[BsonDocument] = filePathOption match {
            case Some(filePath: String) => collectData(Seq(
                (GitlabConstants.AttributeCommits, options.includeCommitLinks match {
                    case true => {
                        fetchLinkData(filePath) match {
                            case Some(value) => Some(
                                // only include the commit id from the commit data
                                value.map(
                                    linkDocument => simplifyCommitLink(linkDocument)
                                ).flatten
                            )
                            case None => None
                        }
                    }
                    case _ => None
                })
            ))
            case None => None
        }

        val documentWithMetadata: BsonDocument = addIdentifierAttributes(document).append(
            GitlabConstants.AttributeMetadata, getMetadata()
        )
        linkDocumentOption match {
            case Some(linkDocument: BsonDocument) => documentWithMetadata.append(
                GitlabConstants.AttributeLinks, linkDocument
            )
            case None => documentWithMetadata
        }
    }

    private def getMetadata(): BsonDocument = {
        getMetadataBase()
            .append(GitlabConstants.AttributeRecursive, toBsonValue(options.recursive))
            .append(GitlabConstants.AttributeIncludeLinksCommits, toBsonValue(options.includeCommitLinks))
            .append(GitlabConstants.AttributeUseAnonymization, toBsonValue(options.useAnonymization))
    }

    private def simplifyCommitLink(document: Document): Option[BsonString] = {
        document.containsKey(GitlabConstants.AttributeId) match {
            case true => document.get(GitlabConstants.AttributeId) match {
                case Some(idAttribute: BsonValue) => idAttribute.isString() match {
                    case true => Some(idAttribute.asString())
                    case false => None
                }
                case None => None
            }
            case false => None
        }
    }

    def collectData(documentData: Seq[(String, Option[Array[BsonString]])]): Option[BsonDocument] = {
        def collectDataInternal(
            documentInternal: Option[BsonDocument],
            dataInternal: Seq[(String, Option[BsonArray])]
        ): Option[BsonDocument] = {
            dataInternal.headOption match {
                case Some(dataElement: (String, Option[BsonArray])) => collectDataInternal(
                    (dataElement._2 match {
                        case Some(actualData: BsonArray) => documentInternal match {
                            case Some(internalDocument: BsonDocument) =>
                                Some(internalDocument.append(dataElement._1, actualData))
                            case None =>
                                Some(new BsonDocument(dataElement._1, actualData))
                        }
                        case None => documentInternal
                    }),
                    dataInternal.drop(1)
                )
                case None => documentInternal
            }
        }

        collectDataInternal(
            None,
            documentData.map(
                documentElement => (
                    documentElement._1,
                    documentElement._2 match {
                        case Some(stringArray: Array[BsonString]) => Some(
                            new BsonArray(stringArray.toList.asJava)
                        )
                        case None => None
                    }
                )
            )
        )
    }

    private def fetchLinkData(filePath: String): Option[Array[Document]] = {
        val commitOptions: GitlabCommitOptions = GitlabCommitOptions(
            hostServer = options.hostServer,
            mongoDatabase = None,
            projectName = options.projectName,
            reference = options.reference,
            startDate = None,
            endDate = None,
            filePath = Some(filePath),
            includeStatistics = false,
            includeFileLinks = false,
            includeReferenceLinks = false,
            useAnonymization = false
        )
        val commitFetcher: GitlabCommitHandler = new GitlabCommitHandler(commitOptions)
        commitFetcher.process()
    }

    private def processOptionalParameters(request: HttpRequest): HttpRequest = {
        val paramMap: Seq[(String, String)] =  options.filePath match {
            case Some(filePath: String) => {
                // use the upper folder (and result filtering) to also get the target file or folder as a response
                Seq((GitlabConstants.ParamPath, GeneralUtils.getUpperFolder(filePath)))
            }
            case None => Seq.empty
        }

        request.params(paramMap)
    }
}
