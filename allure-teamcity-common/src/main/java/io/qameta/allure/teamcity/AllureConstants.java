/*
 *  Copyright 2016-2023 Qameta Software OÜ
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.qameta.allure.teamcity;

import org.jetbrains.annotations.NotNull;

/**
 * The internal class with some constants needed to Allure plugin.
 *
 * @author Dmitry Baev charlie@yandex-team.ru
 *         Date: 06.08.15
 */
public final class AllureConstants {

    public static final String RESULTS_DIRECTORY = "allure.result.directory";

    public static final String REPORT_PATH_PREFIX = "allure.report.path.prefix";

    public static final String PUBLISH_MODE = "allure.publish.mode";

    public static final String ALLURE_TOOL_VERSION = "allure.version";

    public static final String RUN_TYPE = "allureReportGeneratorRunner";

    /**
     * The default directory with allure results.
     */
    public static final String RESULTS_DIRECTORY_DEFAULT = "allure-results";

    /**
     * The default subdirectory (or subdirectories) to put generated report into.
     */
    public static final String REPORT_PATH_PREFIX_DEFAULT = "allure-report";

    /**
     * The name of Allure commandline tool.
     */
    public static final String ALLURE_TOOL_NAME = "allure";


    public static final String ALLURE_DEFAULT_TOOL_VERSION = "%teamcity.tool." + ALLURE_TOOL_NAME + ".DEFAULT%";

    /**
     * The default directory with allure meta files.
     */
    public static final String ALLURE_ARTIFACT_META_LOCATION = ".teamcity/allure/";

    /**
     * The default directory with allure meta files.
     */
    public static final String ALLURE_ARTIFACT_HISTORY_LOCATION = ALLURE_ARTIFACT_META_LOCATION + "history.zip";

    /**
     * The default directory with allure meta files.
     */
    public static final String ALLURE_ARTIFACT_SUMMARY_LOCATION = ALLURE_ARTIFACT_META_LOCATION + "summary.json";

    private static final String ALLURE_COMMANDLINE_MAVEN_URL = "https://repo.maven.apache.org/maven2/"
            + "io/qameta/allure/allure-commandline/";

    public static final String ALLURE_COMMANDLINE_MAVEN_METADATA_URL = ALLURE_COMMANDLINE_MAVEN_URL
            + "maven-metadata.xml";
    public static final String ALLURE_COMMANDLINE_MAVEN_FILENAME = "allure-commandline-%1$s.zip";
    public static final String ALLURE_COMMANDLINE_MAVEN_ZIP_URL = ALLURE_COMMANDLINE_MAVEN_URL + "%1$s/"
            + ALLURE_COMMANDLINE_MAVEN_FILENAME;

    @NotNull
    public String getAllureToolName() {
        return ALLURE_TOOL_NAME;
    }

    @NotNull
    public String getAllureToolVersion() {
        return ALLURE_TOOL_VERSION;
    }
}
