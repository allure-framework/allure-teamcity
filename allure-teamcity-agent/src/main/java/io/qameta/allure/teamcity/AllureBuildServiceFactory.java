package io.qameta.allure.teamcity;

import jetbrains.buildServer.agent.AgentBuildRunnerInfo;
import jetbrains.buildServer.agent.BuildAgentConfiguration;
import jetbrains.buildServer.agent.artifacts.ArtifactsWatcher;
import jetbrains.buildServer.agent.runner.CommandLineBuildService;
import jetbrains.buildServer.agent.runner.CommandLineBuildServiceFactory;
import jetbrains.buildServer.log.Loggers;
import org.jetbrains.annotations.NotNull;

import java.io.File;

import static io.qameta.allure.teamcity.AllureConstants.*;

/**
 * @author Dmitry Baev charlie@yandex-team.ru
 *         Date: 06.08.15
 */
public class AllureBuildServiceFactory implements CommandLineBuildServiceFactory {

    /**
     * Can be used to notify agent artifacts publisher about new artifacts to be
     * published during the build.
     */
    private final ArtifactsWatcher artifactsWatcher;

    public AllureBuildServiceFactory(@NotNull final ArtifactsWatcher artifactsWatcher) {
        this.artifactsWatcher = artifactsWatcher;
    }

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
            public String getType() { return RUN_TYPE; }

            /** {@inheritDoc} */
            @Override
            public boolean canRun(@NotNull BuildAgentConfiguration agentConfiguration) {
                return true;
            }
        };
    }
}
