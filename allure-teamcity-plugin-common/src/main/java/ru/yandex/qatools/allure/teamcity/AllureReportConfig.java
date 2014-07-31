package ru.yandex.qatools.allure.teamcity;

import java.util.HashMap;
import java.util.Map;

public class AllureReportConfig {

    public static final String REPORT_PATH = "allure";

    public static final String REPOSITORY_PATH = "repository";

    public static final String BUILD_FEATURE_TYPE = "allureTeamCityPlugin.buildFeature";

    public static final String BUILD_ID_KEY = "allureTeamCityPlugin.buildId";
    public static final String BUILD_TYPE_ID_KEY = "allureTeamCityPlugin.buildTypeId";
    public static final String BUILD_FEATURE_KEY = "allureTeamCityPlugin.buildFeature";

    public static final String RESULTS_PATTERN_KEY = "allureTeamCityPlugin.resultsPattern";
    public static final String REPORT_VERSION_KEY = "allureTeamCityPlugin.reportVersion";
    public static final String REPORT_BUILD_POLICY_KEY = "allureTeamCityPlugin.reportBuildPolicy";

    public static final String DEFAULT_REPORT_VERSION = "1.3.9";

    public static final String DEFAULT_RESULTS_PATTERN = "**/allure-results";

    private final Map<String, String> parameters;

    public AllureReportConfig() {
        this.parameters = new HashMap<>();
    }

    public AllureReportConfig(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public String getResultsPattern() {
        return parameters.get(RESULTS_PATTERN_KEY);
    }

    public void setResultsPattern(String resultsPattern) {
        parameters.put(RESULTS_PATTERN_KEY, resultsPattern);
    }

    public String getReportVersion() {
        return parameters.get(REPORT_VERSION_KEY);
    }

    public void setReportVersion(String reportVersion) {
        parameters.put(REPORT_VERSION_KEY, reportVersion);
    }

    public ReportBuildPolicy getReportBuildPolicy() {
        return ReportBuildPolicy.valueOf(parameters.get(REPORT_BUILD_POLICY_KEY));
    }

    public void setReportBuildPolicy(ReportBuildPolicy reportBuildPolicy) {
        parameters.put(REPORT_BUILD_POLICY_KEY, reportBuildPolicy.name());
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public static AllureReportConfig newDefaultConfig() {
        AllureReportConfig config = new AllureReportConfig();
        config.setResultsPattern(DEFAULT_RESULTS_PATTERN);
        config.setReportVersion(DEFAULT_REPORT_VERSION);
        config.setReportBuildPolicy(ReportBuildPolicy.ALWAYS);
        return config;
    }

}
