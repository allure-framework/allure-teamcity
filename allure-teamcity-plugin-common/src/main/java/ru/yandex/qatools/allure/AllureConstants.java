package ru.yandex.qatools.allure;

/**
 * The internal class with some constants needed to Allure plugin.
 *
 * @author Dmitry Baev charlie@yandex-team.ru
 *         Date: 06.08.15
 */
public final class AllureConstants {

    public static final String RUN_TYPE = "allureReportGeneratorRunner";

    public static final String REPORT_VERSION = "allure.version";

    public static final String RESULTS_DIRECTORY = "allure.result.directory";

    public static final String ISSUE_TRACKER_PATTERN = "allure.issues.tracker.pattern";

    public static final String TMS_PATTERN = "allure.tests.management.pattern";

    /**
     * The name of Allure commandline tool.
     */
    public static final String ALLURE_TOOL_NAME = "allure-commandline";

    AllureConstants() {
    }
}
