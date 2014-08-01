package ru.yandex.qatools.allure.teamcity;

import jetbrains.buildServer.serverSide.BuildFeature;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;

public class AllureBuildFeature extends BuildFeature {

    private final PluginDescriptor pluginDescriptor;

    public AllureBuildFeature(final PluginDescriptor pluginDescriptor) {
        this.pluginDescriptor = pluginDescriptor;
    }

    @NotNull
    @Override
    public String getType() {
        return AllureReportConfig.BUILD_FEATURE_TYPE;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Allure Report Generation";
    }

    @Nullable
    @Override
    public String getEditParametersUrl() {
        return pluginDescriptor.getPluginResourcesPath("allureSettings.jsp");
    }

    @org.jetbrains.annotations.Nullable
    public Map<String, String> getDefaultParameters() {
        return AllureReportConfig.newDefaultConfig().getParameters();
    }

    @NotNull
    @Override
    public String describeParameters(@NotNull Map<String, String> params) {
        AllureReportConfig config = new AllureReportConfig(params);
        ReportBuildPolicy reportBuildPolicy = config.getReportBuildPolicy();
        String reportVersion = config.getReportVersion();
        String resultsPattern = config.getResultsPattern();
        StringBuilder builder = new StringBuilder();
        builder.append("<b>Results Directories:</b> ").
                append(isNullOrEmpty(resultsPattern) ? "not set" : resultsPattern).
                append("<br/>").
                append("<b>Report Version:</b> ").
                append(isNullOrEmpty(reportVersion) ? "latest" : reportVersion).
                append("<br/>").
                append("<b>Generate:</b> ").
                append(reportBuildPolicy.title());
        return builder.toString();
    }
}
