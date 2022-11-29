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

import jetbrains.buildServer.agent.AgentBuildRunnerInfo;
import jetbrains.buildServer.agent.BuildAgentConfiguration;
import jetbrains.buildServer.agent.artifacts.ArtifactsWatcher;
import jetbrains.buildServer.agent.runner.CommandLineBuildService;
import jetbrains.buildServer.agent.runner.CommandLineBuildServiceFactory;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import static io.qameta.allure.teamcity.AllureConstants.RUN_TYPE;

/**
 * @author Dmitry Baev charlie@yandex-team.ru
 *         Date: 06.08.15
 */
@RequiredArgsConstructor
public class AllureBuildServiceFactory implements CommandLineBuildServiceFactory {

    /**
     * Can be used to notify agent artifacts publisher about new artifacts to be
     * published during the build.
     */
    private final ArtifactsWatcher artifactsWatcher;

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public CommandLineBuildService createService() {
        return new AllureBuildServiceAdapter(artifactsWatcher);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public AgentBuildRunnerInfo getBuildRunnerInfo() {

        return new AgentBuildRunnerInfo() {

            /** {@inheritDoc} */
            @NotNull
            @Override
            public String getType() {
                return RUN_TYPE;
            }

            /** {@inheritDoc} */
            @Override
            public boolean canRun(final @NotNull BuildAgentConfiguration agentConfiguration) {
                return true;
            }
        };
    }
}
