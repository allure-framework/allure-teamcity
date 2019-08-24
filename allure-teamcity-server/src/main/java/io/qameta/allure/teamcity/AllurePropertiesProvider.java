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
