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

import jetbrains.buildServer.agent.AgentRunningBuild;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.agent.BundledTool;
import jetbrains.buildServer.agent.BundledToolsRegistry;
import jetbrains.buildServer.agent.ToolCannotBeFoundException;
import jetbrains.buildServer.agent.ToolProvider;
import jetbrains.buildServer.agent.ToolProvidersRegistry;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Optional;

/**
 * @author Dmitry Baev charlie@yandex-team.ru
 *         Date: 11.08.15
 */
public class AllureToolProvider implements ToolProvider {

    private final BundledToolsRegistry bundledRegistry;

    public AllureToolProvider(@NotNull final ToolProvidersRegistry toolProvidersRegistry,
                              @NotNull final BundledToolsRegistry bundledRegistry) {
        toolProvidersRegistry.registerToolProvider(this);
        this.bundledRegistry = bundledRegistry;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(@NotNull final String toolName) {
        return AllureConstants.ALLURE_TOOL_NAME.equals(toolName);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public String getPath(@NotNull final String toolName) {
        final BundledTool tool = bundledRegistry.findTool(toolName);
        return Optional.ofNullable(tool)
                .map(BundledTool::getRootPath)
                .map(File::getPath)
                .orElseThrow(() -> new ToolCannotBeFoundException("Could not locate Allure installation."));
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public String getPath(@NotNull final String toolName,
                          @NotNull final AgentRunningBuild build,
                          @NotNull final BuildRunnerContext runner) {
        return runner.getRunnerParameters().get(AllureConstants.ALLURE_TOOL_VERSION);
    }
}
