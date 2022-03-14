package visdom.utils

import java.util.concurrent.TimeoutException
import java.util.concurrent.TimeUnit
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions
import scala.concurrent.Await
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.language.existentials
import visdom.adapters.DefaultAdapterValues
import visdom.adapters.options.BaseQueryOptions
import visdom.adapters.queries.BaseCacheQuery
import visdom.adapters.queries.BaseSparkQuery
import visdom.adapters.results.BaseResultValue
import visdom.adapters.results.NoResult
import visdom.adapters.results.Result


object QueryUtils {
    implicit val ec: ExecutionContext = ExecutionContext.global
    private val log = org.slf4j.LoggerFactory.getLogger("QueryUtils")

    implicit class EnrichedDataSet[DataSetType, FilterType](dataset: Dataset[DataSetType]) {
        def applyContainsFilter(columnName: String, valueOption: Option[FilterType]): Dataset[DataSetType] = {
            valueOption match {
                case Some(filterValue) =>
                    dataset.filter(functions.column(columnName).contains(filterValue))
                case None => dataset
            }
        }

        def applyEqualsFilter(columnName: String, valueOption: Option[FilterType]): Dataset[DataSetType] = {
            valueOption match {
                case Some(filterValue) =>
                    dataset.filter(functions.column(columnName) === filterValue)
                case None => dataset
            }
        }
    }

    def runCacheResultQuery(
        queryCode: Int,
        queryType: Class[_ <: BaseCacheQuery],
        queryOptions: BaseQueryOptions,
        timeoutSeconds: Double
    ): Either[String, BaseResultValue] = {
        try {
            Await.result(
                Future(
                    runCacheResultQueryUsingCache(queryCode, queryType, queryOptions) match {
                        case Some(resultValue: BaseResultValue) => Right(resultValue)
                        case None => Right(NoResult("No results found"))
                    }
                ),
                Duration(timeoutSeconds, TimeUnit.SECONDS)
            )
        } catch  {
            case error: TimeoutException => Left(error.getMessage())
        }
    }

    def runSparkQuery(
        queryType: Class[_ <: BaseSparkQuery],
        queryOptions: BaseQueryOptions,
        timeoutSeconds: Double
    ): Unit = {
        try {
            Await.result(
                Future(runSparkQuery(queryType, queryOptions)),
                Duration(timeoutSeconds, TimeUnit.SECONDS)
            )
        } catch  {
            case error: TimeoutException => log.error(error.getMessage())
        }
    }

    def runSparkResultQuery(
        queryCode: Int,
        queryType: Class[_ <: BaseSparkQuery],
        queryOptions: BaseQueryOptions,
        timeoutSeconds: Double
    ): Either[String, BaseResultValue] = {
        try {
            Await.result(
                Future(runSparkResultQueryUsingCache(queryCode, queryType, queryOptions)),
                Duration(timeoutSeconds, TimeUnit.SECONDS)
            )
        } catch  {
            case error: TimeoutException => Left(error.getMessage())
        }
    }

    def runSparkResultQueryUsingCache(
        queryCode: Int,
        queryType: Class[_ <: BaseSparkQuery],
        queryOptions: BaseQueryOptions
    ): Either[String, BaseResultValue] = {
        DefaultAdapterValues.cache.getResult(queryCode, queryOptions) match {
            case Some(cachedResult: BaseResultValue) => {
                log.info(s"Using result from cache for query ${queryCode} with ${queryOptions}")
                Right(cachedResult)
            }
            case None => {
                val result: Either[String, Result] = runSparkResultQuery(queryType, queryOptions)

                result match {
                    case Right(resultValue: Result) =>
                        DefaultAdapterValues.cache.addResult(queryCode, queryOptions, resultValue)
                    case _ =>
                }

                result
            }
        }
    }

    def runCacheResultQueryUsingCache(
        queryCode: Int,
        queryType: Class[_ <: BaseCacheQuery],
        queryOptions: BaseQueryOptions
    ): Option[BaseResultValue] = {
        DefaultAdapterValues.cache.getResult(queryCode, queryOptions) match {
            case Some(cachedResult: BaseResultValue) => {
                log.info(s"Using result from cache for query ${queryCode} with ${queryOptions}")
                Some(cachedResult)
            }
            case None => {
                val query: BaseCacheQuery = queryType
                    .getDeclaredConstructor(queryOptions.getClass())
                    .newInstance(queryOptions)

                if (!query.cacheCheck()) {
                    val (sparkQueryType, sparkQueryOptions) = query.updateCache()
                    runSparkQuery(sparkQueryType, sparkQueryOptions)
                }

                val result: Option[BaseResultValue] = query.getResults()
                result match {
                    case Some(resultValue: BaseResultValue) =>
                        DefaultAdapterValues.cache.addResult(queryCode, queryOptions, resultValue)
                    case None =>
                }

                result
            }
        }
    }

    def runSparkQuery[QueryOptions <: BaseQueryOptions](
        queryType: Class[_ <: BaseSparkQuery],
        queryOptions: QueryOptions
    ): Unit = {
        val sparkSession: SparkSession = SparkSessionUtils.getSparkSession()

        try {
            queryType
                .getDeclaredConstructor(queryOptions.getClass(), classOf[SparkSession])
                .newInstance(queryOptions, sparkSession)
                .runQuery()
        } catch {
            case error: java.lang.NoSuchMethodException => log.error(error.toString())
        }

        SparkSessionUtils.releaseSparkSession()
    }

    def runSparkResultQuery[QueryOptions <: BaseQueryOptions](
        queryType: Class[_ <: BaseSparkQuery],
        queryOptions: QueryOptions
    ): Either[String, Result] = {
        val sparkSession: SparkSession = SparkSessionUtils.getSparkSession()
        val result: Either[String, Result] =
            try {
                Right(
                    queryType
                        .getDeclaredConstructor(queryOptions.getClass(), classOf[SparkSession])
                        .newInstance(queryOptions, sparkSession)
                        .getResults()
                )
            } catch {
                case error: java.lang.NoSuchMethodException => Left(error.toString())
            }

        SparkSessionUtils.releaseSparkSession()
        result
    }
}