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
        return Parameters.BUILD_FEATURE_TYPE;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Generate Allure Report";
    }

    @Nullable
    @Override
    public String getEditParametersUrl() {
        return pluginDescriptor.getPluginResourcesPath("allureSettings.jsp");
    }

    @org.jetbrains.annotations.Nullable
    public Map<String, String> getDefaultParameters() {
        Map<String, String> defaults = new HashMap<>();
        defaults.put(Parameters.RESULTS_MASK, "**/allure-results");
        defaults.put(Parameters.REPORT_VERSION, "1.3.9");
        defaults.put(Parameters.REPORT_BUILD_POLICY, ReportBuildPolicy.ALWAYS.toString());
        return defaults;
    }

    @NotNull
    @Override
    public String describeParameters(@NotNull Map<String, String> params) {
        String reportBuildPolicy = params.get(Parameters.REPORT_BUILD_POLICY);
        String reportVersion = params.get(Parameters.REPORT_VERSION);
        String resultsMask = params.get(Parameters.RESULTS_MASK);
        StringBuilder builder = new StringBuilder();
        builder.append("<b>Allure Results Directories:</b> ").
                append(isNullOrEmpty(resultsMask) ? "not set" : resultsMask).
                append("<br/>").
                append("<b>Allure Version:</b> ").
                append(isNullOrEmpty(reportVersion) ? "latest" : reportVersion).
                append("<br/>").
                append("<b>Build Allure:</b> ").
                append(ReportBuildPolicy.valueOf(reportBuildPolicy));
        return builder.toString();
    }
}
