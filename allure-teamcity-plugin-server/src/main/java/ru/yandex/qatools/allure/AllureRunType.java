package ru.yandex.qatools.allure;

import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.serverSide.RunType;
import jetbrains.buildServer.serverSide.RunTypeRegistry;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

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
        HashMap<String, String> defaults = new HashMap<>();
        defaults.put(AllureConstants.REPORT_VERSION, "1.4.16");
        defaults.put(AllureConstants.ISSUE_TRACKER_PATTERN, "%s");
        defaults.put(AllureConstants.TMS_PATTERN, "%s");
        defaults.put(AllureConstants.RESULTS_DIRECTORY, "allure-results/");
        return defaults;
    }
}
