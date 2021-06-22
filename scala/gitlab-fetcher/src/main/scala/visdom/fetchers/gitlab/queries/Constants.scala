package visdom.fetchers.gitlab.queries

object Constants {
    final val FalseString = "false"
    final val TrueString = "true"
    final val BooleanStrings: Set[String] = Set(FalseString, TrueString)

    final val StatusOkCode = "200"
    final val StatusAcceptedCode = "202"
    final val StatusInvalidCode = "400"
    final val StatusUnauthorizedCode = "401"
    final val StatusNotFoundCode = "404"
    final val StatusErrorCode = "500"

    final val QueryAcceptedStatus: String = "Accepted"
    final val QueryInvalidStatus: String = "BadRequest"
    final val QueryUnauthorizedStatus: String = "Unauthorized"
    final val QueryNotFoundStatus: String = "NotFound"
    final val QueryErrorStatus: String = "InternalServerError"

    final val DateTimeFormat = "date-time"

    final val StatusInvalidDescription = "The request contained invalid or missing parameters"
    final val StatusUnauthorizedDescription = "No access allowed to the wanted GitLab project"
    final val StatusNotFoundDescription = "The asked project or reference was not found"
    final val StatusErrorDescription = "Internal server error"

    final val ParameterProjectName = "projectName"
    final val ParameterReference = "reference"
    final val ParameterStartDate = "startDate"
    final val ParameterEndDate = "endDate"
    final val ParameterFilePath = "filePath"
    final val ParameterIncludeStatistics = "includeStatistics"
    final val ParameterIncludeFileLinks = "includeFileLinks"
    final val ParameterIncludeReferenceLinks = "includeReferenceLinks"
    final val ParameterIncludeCommitLinks = "includeCommitLinks"
    final val ParameterRecursive = "recursive"

    final val ParameterDescriptionProjectName = "the GitLab project name"
    final val ParameterDescriptionReference = "the reference (branch or tag) for the project"
    final val ParameterDescriptionStartDate = "the earliest timestamp for the fetched commits given in ISO 8601 format with timezone"
    final val ParameterDescriptionEndDate = "the latest timestamp for the fetched commits given in ISO 8601 format with timezone"
    final val ParameterDescriptionFilePath = "the path for a file or folder to fetch commits for"
    final val ParameterDescriptionIncludeStatistics = "whether statistics information is included or not"
    final val ParameterDescriptionIncludeFileLinks = "whether file links information is included or not"
    final val ParameterDescriptionIncludeReferenceLinks = "whether reference links information is included or not"
    final val ParameterDescriptionIncludeCommitLinks = "whether commit links information is included or not"
    final val ParameterDescriptionRecursive = "whether to use recursive search or not"

    final val ParameterDefaultReference = "master"
    final val ParameterDefaultIncludeStatisticsString = FalseString
    final val ParameterDefaultIncludeFileLinksString = FalseString
    final val ParameterDefaultIncludeReferenceLinksString = FalseString
    final val ParameterDefaultIncludeCommitLinksString = FalseString
    final val ParameterDefaultRecursiveString = TrueString

    final val ParameterExampleProjectName = "group/my-project-name"

    // the example responses and their names for that can common for the various endpoints
    final val ResponseExampleAcceptedName = "Successful response example"

    final val ResponseExampleInvalidName1 = "Missing project name example"
    final val ResponseExampleInvalid1 = """{
        "status": "BadRequest",
        "description": "''' is not a valid project name"
    }"""
    final val ResponseExampleInvalidName2 = "Invalid start time example"
    final val ResponseExampleInvalid2 = """{
        "status": "BadRequest",
        "description": "'2020-13-13T00:00' is not valid datetime in ISO 8601 format with timezone"
    }"""

    final val ResponseExampleUnauthorizedName = "Unauthorized response example"
    final val ResponseExampleUnauthorized = """{
        "status": "Unauthorized",
        "description": "Access to project 'example-project' not allowed"
    }"""

    final val ResponseExampleNotFoundName = "No project found example"
    final val ResponseExampleNotFound = """{
        "status": "NotFound",
        "description": "Project 'example-project' not found"
    }"""

    final val ResponseExampleErrorName = "Timeout response example"
    final val ResponseExampleError = """{
        "status": "InternalServerError",
        "description": "Futures timed out after [10 seconds]"
    }"""
}