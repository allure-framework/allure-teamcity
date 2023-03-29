plugins {
    id("java-library")
    id("io.github.rodm.teamcity-common") version "1.5"
}

description = "allure-teamcity-plugin-common"

teamcity {
    version = rootProject.extra["teamcityVersion"] as String
}

dependencies {
    annotationProcessor("org.projectlombok:lombok")
    compileOnly("org.projectlombok:lombok")
    implementation("com.fasterxml.jackson.core:jackson-databind")

    provided("org.jetbrains.teamcity:common-api")
}


