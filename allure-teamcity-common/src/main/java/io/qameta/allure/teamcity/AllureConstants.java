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

    static final String ALLURE_TOOL_VERSION = "allure.version";

    static final String RUN_TYPE = "allureReportGeneratorRunner";

    /**
     * The default directory with allure results.
     */
    static final String RESULTS_DIRECTORY_DEFAULT = "allure-results";

    /**
     * The default subdirectory (or subdirectories) to put generated report into.
     */
    static final String REPORT_PATH_PREFIX_DEFAULT = "allure-report";

    /**
     * The name of Allure commandline tool.
     */
    static final String ALLURE_TOOL_NAME = "allure";


    static final String ALLURE_DEFAULT_TOOL_VERSION = "%teamcity.tool." + ALLURE_TOOL_NAME + ".DEFAULT%";

    /**
     * The default directory with allure meta files.
     */
    static final String ALLURE_ARTIFACT_META_LOCATION = ".teamcity/allure/";

    /**
     * The default directory with allure meta files.
     */
    static final String ALLURE_ARTIFACT_HISTORY_LOCATION = ALLURE_ARTIFACT_META_LOCATION + "history.zip";

    /**
     * The default directory with allure meta files.
     */
    static final String ALLURE_ARTIFACT_SUMMARY_LOCATION = ALLURE_ARTIFACT_META_LOCATION + "summary.json";

    private static final String ALLURE_COMMANDLINE_MAVEN_URL = "http://central.maven.org/" +
            "maven2/io/qameta/allure/allure-commandline/";

    static final String ALLURE_COMMANDLINE_MAVEN_METADATA_URL = ALLURE_COMMANDLINE_MAVEN_URL + "maven-metadata.xml";
    static final String ALLURE_COMMANDLINE_MAVEN_FILENAME = "allure-commandline-%1$s.zip";
    static final String ALLURE_COMMANDLINE_MAVEN_ZIP_URL = ALLURE_COMMANDLINE_MAVEN_URL + "%1$s/"
            + ALLURE_COMMANDLINE_MAVEN_FILENAME;


    public AllureConstants() {
    }

    @NotNull
    public String getAllureToolName() {
        return ALLURE_TOOL_NAME;
    }

    @NotNull
    public String getAllureToolVersion() {
        return ALLURE_TOOL_VERSION;
    }
}
