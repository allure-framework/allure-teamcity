package io.qameta.allure.teamcity;

import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.serverSide.RunType;
import jetbrains.buildServer.serverSide.RunTypeRegistry;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import static io.qameta.allure.teamcity.AllureConstants.*;

/**
 * @author Dmitry Baev charlie@yandex-team.ru
 *         Date: 06.08.15
 */
public class AllureRunType extends RunType {

    private final PluginDescriptor pluginDescriptor;

    /**
     * {@inheritDoc}
     */
    public AllureRunType(
            @NotNull final RunTypeRegistry registry,
            @NotNull final PluginDescriptor descriptor) {
        this.pluginDescriptor = descriptor;
        registry.registerRunType(this);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public String getType() {
        return AllureConstants.RUN_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public String getDisplayName() {
        return "Allure Report";
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public String getDescription() {
        return "Generate the Allure report for your build.";
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public PropertiesProcessor getRunnerPropertiesProcessor() {
        return new AllurePropertiesProcessor();
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public String getEditRunnerParamsJspFilePath() {
        return pluginDescriptor.getPluginResourcesPath("editParams.jsp");
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public String getViewRunnerParamsJspFilePath() {
        return pluginDescriptor.getPluginResourcesPath("viewParams.jsp");
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public Map<String, String> getDefaultRunnerProperties() {
        Map<String, String> defaults = new HashMap<>();
        defaults.put(AllureConstants.RESULTS_DIRECTORY, RESULTS_DIRECTORY_DEFAULT);
        defaults.put(AllureConstants.REPORT_PATH_PREFIX, REPORT_PATH_PREFIX_DEFAULT);
        defaults.put(AllureConstants.PUBLISH_MODE, AllurePublishMode.ARCHIVE.toString());
        defaults.put(AllureConstants.ALLURE_TOOL_VERSION, ALLURE_DEFAULT_TOOL_VERSION_ID);
        return defaults;
    }
}
