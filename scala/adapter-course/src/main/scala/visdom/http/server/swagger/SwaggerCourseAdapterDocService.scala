package visdom.http.server.swagger

import com.github.swagger.akka.model.Info
import visdom.adapters.course.AdapterValues
import visdom.http.server.services.CourseAdapterInfoService
import visdom.http.server.services.DataQueryService
import visdom.http.server.services.HistoryQueryService
import visdom.http.server.services.UsernameQueryService


object SwaggerCourseAdapterDocService extends SwaggerDocService {
    override val host: String = AdapterValues.apiAddress
    override val info: Info = Info(version = AdapterValues.Version)
    override val apiClasses: Set[Class[_]] = Set(
        classOf[CourseAdapterInfoService],
        classOf[DataQueryService],
        classOf[HistoryQueryService],
        classOf[UsernameQueryService]
    )
}
