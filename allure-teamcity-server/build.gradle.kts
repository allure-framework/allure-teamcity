plugins {
    id("io.github.rodm.teamcity-server") version "1.5"
    id("io.github.rodm.teamcity-environments") version "1.5"
}

description = "allure-teamcity-plugin-server"

val teamcityVersion = (rootProject.extra["teamcityVersion"] as String)
val teamcityDir = (rootProject.extra["teamcityDir"] as String)
val teamcityFullDir = teamcityDir + "/TeamCity-" + teamcityVersion


teamcity {
    version = rootProject.extra["teamcityVersion"] as String
    server {
        descriptor {
            name = rootProject.name
            displayName = "Allure Report"
            version = rootProject.version as String?

            description = "Plugin adds support for generate Allure report based on tests results"

            vendorName = "qameta"
            vendorUrl = "https://qameta.io"
            email = "team@qameta.io"

            useSeparateClassloader = true
            nodeResponsibilitiesAware = true
        }
        publish {
            token = System.getenv("JETBRAINS_TOKEN")
        }
    }

    environments {
        create("Teamcity2017") {
            version = "2017.1.5"
            homeDir = "$teamcityDir/TeamCity-$version"
            dataDir = "$teamcityDir/data/$version"
            serverOptions = rootProject.extra["serverOpts"] as String
        }
        create("Teamcity20202") {
            version = "2020.2"
            homeDir = "$teamcityDir/TeamCity-$version"
            dataDir = "$teamcityDir/data/$version"
            serverOptions = rootProject.extra["serverOpts"] as String
        }
    }

}

tasks.serverPlugin{
    archiveBaseName.set(project.parent?.name)
    archiveVersion.set(null as String?) //teamcity plugin file name should be without version suffix
}
//configurations.archives.artifacts.removeAll { it.archiveTask.is jar }

dependencies {

    annotationProcessor("org.projectlombok:lombok")
    compileOnly("org.projectlombok:lombok")

    agent(project(path = ":allure-teamcity-agent", configuration = "plugin"))
    implementation(project(":allure-teamcity-common"))

    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("commons-io:commons-io")
    implementation("org.apache.commons:commons-lang3")

    provided("javax.servlet:jstl")
    provided("org.jetbrains.teamcity.internal:server")

    provided(files("$teamcityFullDir/webapps/ROOT/WEB-INF/lib/server-tools.jar"))
    provided(files("$teamcityFullDir/webapps/ROOT/WEB-INF/lib/common-tools.jar"))
}
