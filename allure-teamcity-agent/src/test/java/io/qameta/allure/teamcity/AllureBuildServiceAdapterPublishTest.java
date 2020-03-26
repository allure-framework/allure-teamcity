package io.qameta.allure.teamcity;

import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.AgentRunningBuild;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.agent.artifacts.ArtifactsWatcher;
import jetbrains.buildServer.agent.runner.CommandLineBuildService;
import jetbrains.buildServer.agent.runner.CommandLineBuildServiceFactory;


public class AllureBuildServiceAdapterPublishTest {

    public static void main(String[] args) throws RunBuildException {
        ArtifactsWatcher artifactsWatcher = new ArtifactsWatcherStub();
        CommandLineBuildServiceFactory commandLineBuildServiceFactory = new AllureBuildServiceFactory(artifactsWatcher);

        CommandLineBuildService commandLineBuildService = commandLineBuildServiceFactory.createService();

        AgentRunningBuild agentRunningBuild = new AgentRunningBuildStub();
        BuildRunnerContext buildRunnerContext = new BuildRunnerContextStub();

        try {
            commandLineBuildService.initialize(agentRunningBuild, buildRunnerContext);
        } catch (RunBuildException e) {
            throw e;
        }

        try {
            commandLineBuildService.afterProcessSuccessfullyFinished();
        } catch (RunBuildException e) {
            throw e;
        }
    }

}
