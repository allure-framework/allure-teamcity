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

import jetbrains.buildServer.serverSide.BuildStartContext;
import jetbrains.buildServer.serverSide.BuildStartContextProcessor;
import jetbrains.buildServer.serverSide.SRunnerContext;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public class AllurePropertiesProvider implements BuildStartContextProcessor {

    @Override
    public void updateParameters(@NotNull final BuildStartContext context) {
        context.getRunnerContexts().stream()
                .filter(runner -> runner.getType().equals(AllureConstants.RUN_TYPE))
                .forEach(this::updateAllureParameters);
    }

    private void updateAllureParameters(@NotNull final SRunnerContext runnerContext) {
        if (StringUtils.isBlank(runnerContext.getParameters().get(AllureConstants.ALLURE_TOOL_VERSION))) {
            runnerContext.addRunnerParameter(
                    AllureConstants.ALLURE_TOOL_VERSION,
                    AllureConstants.ALLURE_DEFAULT_TOOL_VERSION
            );
        }
    }

}
