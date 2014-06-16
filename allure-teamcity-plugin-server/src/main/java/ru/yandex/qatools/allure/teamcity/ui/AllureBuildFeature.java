package ru.yandex.qatools.allure.teamcity.ui;

import com.google.common.base.Strings;
import jetbrains.buildServer.serverSide.BuildFeature;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.yandex.qatools.allure.teamcity.Constants;

import java.util.Map;

public class AllureBuildFeature extends BuildFeature {
    
    private final PluginDescriptor pluginDescriptor;

    public AllureBuildFeature(final PluginDescriptor pluginDescriptor) {
        this.pluginDescriptor = pluginDescriptor;
    }

    @NotNull
    @Override
    public String getType() {
        return Constants.BUILD_FEATURE_TYPE;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Allure Report Build Feature";
    }

    @Nullable
    @Override
    public String getEditParametersUrl() {
        return pluginDescriptor.getPluginResourcesPath("allureSettings.jsp");
    }

    @NotNull
    @Override
    public String describeParameters(@NotNull Map<String, String> params) {
        final String inputDirectory = params.get(Constants.INPUT_DIRECTORY);
        final String message = "<strong>Input files directory</strong> - path to directory with Allure input files relative to VCS root, e.g. <b>target/allure-results</b>";
        return Strings.isNullOrEmpty(inputDirectory) ? message : message + "<br />Current input directory is <i>" + inputDirectory + "</i>";
    }
}
