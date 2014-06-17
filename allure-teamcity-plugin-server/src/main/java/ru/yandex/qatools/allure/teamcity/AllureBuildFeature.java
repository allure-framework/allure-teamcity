package ru.yandex.qatools.allure.teamcity;

import com.google.common.base.Strings;
import jetbrains.buildServer.serverSide.BuildFeature;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

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
        return "Allure report processing";
    }

    @Nullable
    @Override
    public String getEditParametersUrl() {
        return pluginDescriptor.getPluginResourcesPath("allureSettings.jsp");
    }

    @NotNull
    @Override
    public String describeParameters(@NotNull Map<String, String> params) {
        final String inputDirectory = params.get(Parameters.RESULTS_MASK);
        final String message = "<strong>Allure tests results mask</strong> - path to directories with Allure input " +
                "files relative to VCS root in the Ant glob syntax, such as **/target/allure-results. " +
                "You can specify multiple patterns of files separated by commas.";
        return Strings.isNullOrEmpty(inputDirectory) ? message : message +
                "<br />Current results mask is <i>" + inputDirectory + "</i>";
    }
}
