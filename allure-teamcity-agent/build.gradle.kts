plugins {
    id("io.github.rodm.teamcity-agent") version "1.5"
}
description = "allure-teamcity-plugin-agent"

teamcity {
    version = rootProject.extra["teamcityVersion"] as String
    agent {
        descriptor {
            pluginDeployment {
                useSeparateClassloader = true
            }
        }
    }
}

tasks.agentPlugin{
    archiveBaseName.set(name)
    archiveVersion.set(null as String?) //teamcity plugin file name should be without version suffix
}

dependencies {
    annotationProcessor("org.projectlombok:lombok")
    compileOnly("org.projectlombok:lombok")

    implementation(project(":allure-teamcity-common"))

    implementation("com.fasterxml.jackson.core:jackson-databind")

    implementation("commons-io:commons-io")
    implementation("commons-logging:commons-logging")

    implementation("org.apache.commons:commons-compress")
    implementation("org.apache.commons:commons-lang3")
    implementation("org.apache.httpcomponents.client5:httpclient5")

    provided("com.intellij:openapi")
    provided("org.jetbrains.teamcity:agent-api")
    provided("org.jetbrains.teamcity.internal:agent")

}

