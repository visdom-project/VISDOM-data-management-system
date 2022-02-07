package visdom.adapters.general.model.origins

import visdom.adapters.general.model.base.Origin
import visdom.adapters.general.model.origins.data.GitlabOriginData
import visdom.utils.GeneralUtils
import visdom.utils.CommonConstants


class GitlabOrigin(hostName: String, projectGroup: String, projectName: String)
extends Origin {
    def getType: String = GitlabOrigin.GitlabOriginType
    val source: String = hostName
    val context: String = projectName

    val data: GitlabOriginData = GitlabOriginData(projectGroup)
}

object GitlabOrigin {
    final val GitlabOriginType: String = "GitLab"

    def getGitlabOriginFromHost(hostName: String): GitlabOrigin = {
        new GitlabOrigin(hostName, CommonConstants.EmptyString, CommonConstants.EmptyString)
    }

    def getId(hostName: String, projectName: String): String = {
        GeneralUtils.getUuid(GitlabOriginType, hostName, projectName)
    }
}
