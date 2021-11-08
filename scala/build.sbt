scalaVersion := "2.12.13"

val coreDirectory: String = "core"
val brokerDirectory: String = "broker"

val gitlabFetcherDirectory: String = "gitlab-fetcher"
val aPlusFetcherDirectory: String = "aplus-fetcher"

val coreAdapterDirectory: String = "core-adapter"
val gitlabAdapterDirectory: String = "gitlab-adapter"
val adapterCourseDirectory: String = "adapter-course"

lazy val core = project
    .in(file(coreDirectory))

lazy val dataBroker = project
    .in(file(brokerDirectory))
    .dependsOn(core)

lazy val gitlabFetcher = project
    .in(file(gitlabFetcherDirectory))
    .dependsOn(core)

lazy val aPlusFetcher = project
    .in(file(aPlusFetcherDirectory))
    .dependsOn(core)

lazy val coreAdapter = project
    .in(file(coreAdapterDirectory))
    .dependsOn(core)

lazy val gitlabAdapter = project
    .in(file(gitlabAdapterDirectory))
    .dependsOn(core)

lazy val adapterCourse = project
    .in(file(adapterCourseDirectory))
    .dependsOn(core)
