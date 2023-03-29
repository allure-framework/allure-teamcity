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

import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.serverSide.RunType;
import jetbrains.buildServer.serverSide.RunTypeRegistry;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import static io.qameta.allure.teamcity.AllureConstants.ALLURE_DEFAULT_TOOL_VERSION;
import static io.qameta.allure.teamcity.AllureConstants.REPORT_PATH_PREFIX_DEFAULT;
import static io.qameta.allure.teamcity.AllureConstants.RESULTS_DIRECTORY_DEFAULT;

/**
 * @author Dmitry Baev charlie@yandex-team.ru
 *         Date: 06.08.15
 */
public class AllureRunType extends RunType {

    private final PluginDescriptor pluginDescriptor;

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
        final Map<String, String> defaults = new HashMap<>();
        defaults.put(AllureConstants.RESULTS_DIRECTORY, RESULTS_DIRECTORY_DEFAULT);
        defaults.put(AllureConstants.REPORT_PATH_PREFIX, REPORT_PATH_PREFIX_DEFAULT);
        defaults.put(AllureConstants.PUBLISH_MODE, AllurePublishMode.ARCHIVE.toString());
        defaults.put(AllureConstants.ALLURE_TOOL_VERSION, ALLURE_DEFAULT_TOOL_VERSION);
        return defaults;
    }
}
