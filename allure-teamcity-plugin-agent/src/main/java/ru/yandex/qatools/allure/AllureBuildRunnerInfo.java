package ru.yandex.qatools.allure;

import jetbrains.buildServer.agent.AgentBuildRunnerInfo;
import jetbrains.buildServer.agent.BuildAgentConfiguration;
import jetbrains.buildServer.log.Loggers;
import org.jetbrains.annotations.NotNull;

import java.io.File;

import static ru.yandex.qatools.allure.AllureConstants.ALLURE_TOOL_NAME;

/**
 * @author Dmitry Baev charlie@yandex-team.ru
 *         Date: 06.08.15
 */
public class AllureBuildRunnerInfo implements AgentBuildRunnerInfo {

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
    @Override
    public boolean canRun(@NotNull BuildAgentConfiguration agentConfiguration) {
        boolean canRun = new File(agentConfiguration.getAgentToolsDirectory(), ALLURE_TOOL_NAME).exists();
        if (!canRun) {
            Loggers.AGENT.warn("Could not run Allure report generation " +
                    "because the Allure tool is not installed.");
        }
        return canRun;
    }
}
