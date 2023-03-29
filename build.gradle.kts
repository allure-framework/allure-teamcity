import net.researchgate.release.ReleaseExtension
import java.nio.charset.StandardCharsets.UTF_8

val linkHomepage by extra("https://qameta.io/allure")
val linkCi by extra("https://ci.qameta.io/job/allure-teamcity")
val linkScmUrl by extra("https://github.com/allure-framework/allure-teamcity")
val linkScmConnection by extra("scm:git:git://github.com/allure-framework/allure-teamcity.git")
val linkScmDevConnection by extra("scm:git:ssh://git@github.com:allure-framework/allure-teamcity.git")

val root = rootProject.projectDir
val gradleScriptDir by extra("$root/gradle")
val qualityConfigsDir by extra("$gradleScriptDir/quality-configs")
val spotlessDtr by extra("$qualityConfigsDir/spotless")

val teamcityVersion by extra("2020.2")
val teamcityDir by extra("$root/.teamcity")
val serverOpts by extra("-DTC.res.disableAll=true " +
        "-Dteamcity.development.mode=true " +
        "-Dteamcity.development.shadowCopyClasses=true " +
        "-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=50055")

tasks.wrapper {
    gradleVersion = "7.5.1"
}

plugins {
    java
    signing
    `java-library`
    id("com.diffplug.spotless") version "6.13.0"
    id("com.jfrog.bintray") version "1.8.5"
    id("com.gorylenko.gradle-git-properties") version "2.4.1"
    id("io.spring.dependency-management") version "1.1.0"
    id("ru.vyarus.quality") version "4.7.0"
    id("org.owasp.dependencycheck") version "7.4.4"
    id("net.researchgate.release") version "3.0.2"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

allprojects {
    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }
}

description = "Allure Teamcity"
version = version
group = "io.qameta.allure"

configurations.all {
    resolutionStrategy.eachDependency {
        when (requested.module.toString()) {
            "xml-apis:xml-apis" -> useVersion("1.4.01")
        }
    }
}

allprojects {
    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }
}

apply(plugin = "net.researchgate.release")

configure<ReleaseExtension> {
    tagTemplate.set("$version")
}

tasks.afterReleaseBuild {
    dependsOn(":allure-teamcity-server:publishPlugin")
}


configure(subprojects) {

    version = version

    apply(plugin = "java")
    apply(plugin = "maven-publish")
    apply(plugin = "ru.vyarus.quality")
    apply(plugin = "com.diffplug.spotless")
    apply(plugin = "io.spring.dependency-management")

    dependencyManagement {
        imports {
            mavenBom("com.fasterxml.jackson:jackson-bom:2.14.1")
            mavenBom("org.junit:junit-bom:5.9.2")
        }
        dependencies {

            dependency("com.intellij:openapi:7.0.3")
            dependency("com.github.spotbugs:spotbugs-annotations:4.7.3")

            dependency("commons-logging:commons-logging:1.2")

            dependency("javax.servlet:jstl:1.1.2")

            dependency("org.apache.commons:commons-compress:1.21")
            dependency("org.apache.commons:commons-lang3:3.12.0")
            dependency("commons-io:commons-io:2.11.0")
            dependency("org.apache.httpcomponents.client5:httpclient5:5.2.1")
            dependency("org.apache.maven:maven-artifact:3.6.3")

            dependency("org.jetbrains.teamcity:agent-api:$teamcityVersion")
            dependency("org.jetbrains.teamcity.internal:agent:$teamcityVersion")
            dependency("org.jetbrains.teamcity.internal:server:$teamcityVersion")

            dependency("org.projectlombok:lombok:1.18.24")

            dependencySet("org.slf4j:2.0.3") {
                entry("slf4j-api")
                entry("slf4j-nop")
                entry("slf4j-simple")
            }

            dependency("org.zeroturnaround:zt-zip:1.15")

        }
    }

    tasks.compileTestJava {
        options.compilerArgs.add("-parameters")
    }

    tasks.jar {
        manifest {
            attributes(mapOf(
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version
            ))
        }
    }

    quality {
        configDir = "$gradleScriptDir/quality-configs"
        excludeSources = fileTree("build/generated-sources")
        exclude("**/*.json")
        checkstyleVersion = "8.36.1"
        pmdVersion = "6.28.0"
        spotbugsVersion = "4.1.2"
        codenarcVersion = "1.6"
        spotbugs = true
        codenarc = true
        pmd = true
        checkstyle = true
        htmlReports = false

        afterEvaluate {
            val spotbugs = configurations.findByName("spotbugs")
            if (spotbugs != null) {
                dependencies {
                    spotbugs("org.slf4j:slf4j-simple")
                    spotbugs("com.github.spotbugs:spotbugs:4.7.3")
                }
            }

            tasks.withType(Checkstyle::class).configureEach {
                configDirectory.set(file("$gradleScriptDir/quality-configs/checkstyle"))
            }
        }
    }

    spotless {
        java {
            target("src/**/*.java")
            removeUnusedImports()
            importOrder("", "jakarta", "javax", "java", "\\#")
            licenseHeader(file("$spotlessDtr/allure.java.license").readText(UTF_8))
            endWithNewline()
            replaceRegex("one blank line after package line", "(package .+;)\n+import", "$1\n\nimport")
            replaceRegex("one blank line after import lists", "(import .+;\n\n)\n+", "$1")
            replaceRegex("no blank line between jakarta & javax", "(import jakarta.+;\n)\n+(import javax.+;\n)", "$1$2")
            replaceRegex("no blank line between javax & java", "(import javax.+;\n)\n+(import java.+;\n)", "$1$2")
            replaceRegex("no blank line between jakarta & java", "(import jakarta.+;\n)\n+(import java.+;\n)", "$1$2")
        }
        format("misc") {
            target(
                "*.gradle",
                "*.gitignore",
                "README.md",
                "CONTRIBUTING.md",
                "config/**/*.xml",
                "src/**/*.xml"
            )
            trimTrailingWhitespace()
            endWithNewline()
        }

        encoding("UTF-8")
    }

    java {
        withJavadocJar()
        withSourcesJar()
    }

    tasks.withType<Javadoc>().configureEach {
        (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
    }

    tasks.withType<GenerateModuleMetadata>().configureEach {
        enabled = false
    }

    repositories {
        mavenLocal()
        jcenter()
        maven("https://download.jetbrains.com/teamcity-repository")
    }
}