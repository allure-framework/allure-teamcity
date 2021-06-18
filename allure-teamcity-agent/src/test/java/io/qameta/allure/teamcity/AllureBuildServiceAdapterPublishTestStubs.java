package io.qameta.allure.teamcity;

import jetbrains.buildServer.BuildProblemData;
import jetbrains.buildServer.agent.*;
import jetbrains.buildServer.agent.artifacts.ArtifactsWatcher;
import jetbrains.buildServer.agentServer.AgentBuild;
import jetbrains.buildServer.artifacts.ArtifactDependencyInfo;
import jetbrains.buildServer.messages.BuildMessage1;
import jetbrains.buildServer.parameters.ValueResolver;
import jetbrains.buildServer.util.Option;
import jetbrains.buildServer.util.PasswordReplacer;
import jetbrains.buildServer.vcs.VcsChangeInfo;
import jetbrains.buildServer.vcs.VcsRoot;
import jetbrains.buildServer.vcs.VcsRootEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;


class ArtifactsWatcherStub implements ArtifactsWatcher {

    @Override
    public void addNewArtifactsPath(@NotNull String artifactsPath) {

    }
}


class BuildRunnerContextStub implements BuildRunnerContext {

    @Override
    public String getId() {
        return null;
    }

    @NotNull
    @Override
    public AgentRunningBuild getBuild() {
        return null;
    }

    @NotNull
    @Override
    public File getWorkingDirectory() {
        return null;
    }

    @NotNull
    @Override
    public String getRunType() {
        return null;
    }

    @NotNull
    @Override
    public String getName() {
        return null;
    }

    @NotNull
    @Override
    public BuildParametersMap getBuildParameters() {
        return null;
    }

    @NotNull
    @Override
    public Map<String, String> getConfigParameters() {
        Map<String, String> map = new HashMap<>();
        map.put("teamcity.serverUrl", "");
        return map;
    }

    @NotNull
    @Override
    public Map<String, String> getRunnerParameters() {
        Map<String, String> map = new HashMap<>();
        map.put("allure.result.directory", "output/build/0/xcresults");
        map.put("allure.publish.mode", "ARCHIVE");
        map.put("allure.report.path.prefix", "output/build/0/allure-report");
        return map;
    }

    @Override
    public void addSystemProperty(@NotNull String key, @NotNull String value) {

    }

    @Override
    public void addEnvironmentVariable(@NotNull String key, @NotNull String value) {

    }

    @Override
    public void addConfigParameter(@NotNull String key, @NotNull String value) {

    }

    @Override
    public void addRunnerParameter(@NotNull String key, @NotNull String value) {

    }

    @NotNull
    @Override
    public ValueResolver getParametersResolver() {
        return null;
    }

    @NotNull
    @Override
    public String getToolPath(@NotNull String toolName) throws ToolCannotBeFoundException {
        return "";
    }

    @Override
    public boolean parametersHaveReferencesTo(@NotNull Collection<String> keys) {
        return false;
    }
}


class AgentRunningBuildStub implements AgentRunningBuild {

    @NotNull
    @Override
    public BuildParametersMap getMandatoryBuildParameters() {
        return null;
    }

    @NotNull
    @Override
    public File getCheckoutDirectory() {
        return new File("/users/{USER}/{PATH}");
    }

    @Nullable
    @Override
    public AgentCheckoutMode getEffectiveCheckoutMode() {
        return null;
    }

    @NotNull
    @Override
    public File getWorkingDirectory() {
        return null;
    }

    @Nullable
    @Override
    public String getArtifactsPaths() {
        return null;
    }

    @Override
    public boolean getFailBuildOnExitCode() {
        return false;
    }

    @NotNull
    @Override
    public ResolvedParameters getResolvedParameters() {
        return null;
    }

    @NotNull
    @Override
    public String getRunType() {
        return null;
    }

    @NotNull
    @Override
    public UnresolvedParameters getUnresolvedParameters() {
        return null;
    }

    @NotNull
    @Override
    public BuildParametersMap getBuildParameters() {
        return null;
    }

    @NotNull
    @Override
    public Map<String, String> getRunnerParameters() {
        return null;
    }

    @NotNull
    @Override
    public String getBuildNumber() {
        return null;
    }

    @NotNull
    @Override
    public Map<String, String> getSharedConfigParameters() {
        return null;
    }

    @Override
    public void addSharedConfigParameter(@NotNull String key, @NotNull String value) {

    }

    @Override
    public void addSharedSystemProperty(@NotNull String key, @NotNull String value) {

    }

    @Override
    public void addSharedEnvironmentVariable(@NotNull String key, @NotNull String value) {

    }

    @NotNull
    @Override
    public BuildParametersMap getSharedBuildParameters() {
        return null;
    }

    @NotNull
    @Override
    public ValueResolver getSharedParametersResolver() {
        return null;
    }

    @NotNull
    @Override
    public Collection<AgentBuildFeature> getBuildFeatures() {
        return null;
    }

    @NotNull
    @Override
    public Collection<AgentBuildFeature> getBuildFeaturesOfType(@NotNull String type) {
        return null;
    }

    @Override
    public void stopBuild(@NotNull String reason) {

    }

    @Nullable
    @Override
    public BuildInterruptReason getInterruptReason() {
        return null;
    }

    @Override
    public boolean isBuildFailingOnServer() throws InterruptedException {
        return false;
    }

    @Override
    public boolean isInAlwaysExecutingStage() {
        return false;
    }

    @NotNull
    @Override
    public PasswordReplacer getPasswordReplacer() {
        return null;
    }

    @NotNull
    @Override
    public Map<String, String> getArtifactStorageSettings() {
        return null;
    }

    @Override
    public String getProjectName() {
        return null;
    }

    @NotNull
    @Override
    public String getBuildTypeId() {
        return null;
    }

    @NotNull
    @Override
    public String getBuildTypeExternalId() {
        return null;
    }

    @Override
    public String getBuildTypeName() {
        return null;
    }

    @Override
    public long getBuildId() {
        return 0;
    }

    @Override
    public boolean isCleanBuild() {
        return false;
    }

    @Override
    public boolean isPersonal() {
        return false;
    }

    @Override
    public boolean isPersonalPatchAvailable() {
        return false;
    }

    @Override
    public boolean isCheckoutOnAgent() {
        return false;
    }

    @Override
    public boolean isCheckoutOnServer() {
        return false;
    }

    @NotNull
    @Override
    public AgentBuild.CheckoutType getCheckoutType() {
        return null;
    }

    @Override
    public long getExecutionTimeoutMinutes() {
        return 0;
    }

    @NotNull
    @Override
    public List<ArtifactDependencyInfo> getArtifactDependencies() {
        return null;
    }

    @NotNull
    @Override
    public String getAccessUser() {
        return null;
    }

    @NotNull
    @Override
    public String getAccessCode() {
        return null;
    }

    @NotNull
    @Override
    public List<VcsRootEntry> getVcsRootEntries() {
        return null;
    }

    @Override
    public String getBuildCurrentVersion(@NotNull VcsRoot vcsRoot) {
        return null;
    }

    @Override
    public String getBuildPreviousVersion(@NotNull VcsRoot vcsRoot) {
        return null;
    }

    @Override
    public boolean isCustomCheckoutDirectory() {
        return false;
    }

    @NotNull
    @Override
    public List<VcsChangeInfo> getVcsChanges() {
        return null;
    }

    @NotNull
    @Override
    public List<VcsChangeInfo> getPersonalVcsChanges() {
        return null;
    }

    @NotNull
    @Override
    public File getBuildTempDirectory() {
        return null;
    }

    @NotNull
    @Override
    public File getAgentTempDirectory() {
        return null;
    }

    @NotNull
    @Override
    public BuildProgressLogger getBuildLogger() {
        return new BuildProgressLoggerStub();
    }

    @NotNull
    @Override
    public BuildAgentConfiguration getAgentConfiguration() {
        return null;
    }

    @Override
    public <T> T getBuildTypeOptionValue(@NotNull Option<T> option) {
        return null;
    }

    @NotNull
    @Override
    public File getDefaultCheckoutDirectory() {
        return null;
    }

    @NotNull
    @Override
    public String getVcsSettingsHashForCheckoutMode(AgentCheckoutMode agentCheckoutMode) {
        return null;
    }

    @NotNull
    @Override
    public String describe(boolean verbose) {
        return null;
    }
}


class BuildProgressLoggerStub implements BuildProgressLogger {

    @Override
    public void activityStarted(String activityName, String activityType) {

    }

    @Override
    public void activityStarted(String activityName, String activityDescription, String activityType) {

    }

    @Override
    public void activityFinished(String activityName, String activityType) {

    }

    @Override
    public void targetStarted(String targetName) {

    }

    @Override
    public void targetFinished(String targetName) {

    }

    @Override
    public void buildFailureDescription(String message) {

    }

    @Override
    public void internalError(String type, String message, Throwable throwable) {

    }

    @Override
    public void progressStarted(String message) {

    }

    @Override
    public void progressFinished() {

    }

    @Override
    public void logMessage(BuildMessage1 message) {

    }

    @Override
    public void logTestStarted(String name) {

    }

    @Override
    public void logTestStarted(String name, Date timestamp) {

    }

    @Override
    public void logTestFinished(String name) {

    }

    @Override
    public void logTestFinished(String name, Date timestamp) {

    }

    @Override
    public void logTestIgnored(String name, String reason) {

    }

    @Override
    public void logSuiteStarted(String name) {

    }

    @Override
    public void logSuiteStarted(String name, Date timestamp) {

    }

    @Override
    public void logSuiteFinished(String name) {

    }

    @Override
    public void logSuiteFinished(String name, Date timestamp) {

    }

    @Override
    public void logTestStdOut(String testName, String out) {

    }

    @Override
    public void logTestStdErr(String testName, String out) {

    }

    @Override
    public void logTestFailed(String testName, Throwable e) {

    }

    @Override
    public void logComparisonFailure(String testName, Throwable e, String expected, String actual) {

    }

    @Override
    public void logTestFailed(String testName, String message, String stackTrace) {

    }

    @Override
    public void flush() {

    }

    @Override
    public void ignoreServiceMessages(Runnable runnable) {

    }

    @Override
    public FlowLogger getFlowLogger(String flowId) {
        return new FlowLoggerStub();
    }

    @Override
    public FlowLogger getThreadLogger() {
        return null;
    }

    @Override
    public String getFlowId() {
        return null;
    }

    @Override
    public void logBuildProblem(BuildProblemData buildProblem) {

    }

    @Override
    public void message(String message) {

    }

    @Override
    public void error(String message) {

    }

    @Override
    public void warning(String message) {

    }

    @Override
    public void exception(Throwable th) {

    }

    @Override
    public void progressMessage(String message) {

    }
}


class FlowLoggerStub implements FlowLogger {

    @Override
    public void startFlow() {

    }

    @Override
    public void disposeFlow() {

    }

    @Override
    public void activityStarted(String activityName, String activityType) {

    }

    @Override
    public void activityStarted(String activityName, String activityDescription, String activityType) {

    }

    @Override
    public void activityFinished(String activityName, String activityType) {

    }

    @Override
    public void targetStarted(String targetName) {

    }

    @Override
    public void targetFinished(String targetName) {

    }

    @Override
    public void buildFailureDescription(String message) {

    }

    @Override
    public void internalError(String type, String message, Throwable throwable) {

    }

    @Override
    public void progressStarted(String message) {

    }

    @Override
    public void progressFinished() {

    }

    @Override
    public void logMessage(BuildMessage1 message) {

    }

    @Override
    public void logTestStarted(String name) {

    }

    @Override
    public void logTestStarted(String name, Date timestamp) {

    }

    @Override
    public void logTestFinished(String name) {

    }

    @Override
    public void logTestFinished(String name, Date timestamp) {

    }

    @Override
    public void logTestIgnored(String name, String reason) {

    }

    @Override
    public void logSuiteStarted(String name) {

    }

    @Override
    public void logSuiteStarted(String name, Date timestamp) {

    }

    @Override
    public void logSuiteFinished(String name) {

    }

    @Override
    public void logSuiteFinished(String name, Date timestamp) {

    }

    @Override
    public void logTestStdOut(String testName, String out) {

    }

    @Override
    public void logTestStdErr(String testName, String out) {

    }

    @Override
    public void logTestFailed(String testName, Throwable e) {

    }

    @Override
    public void logComparisonFailure(String testName, Throwable e, String expected, String actual) {

    }

    @Override
    public void logTestFailed(String testName, String message, String stackTrace) {

    }

    @Override
    public void flush() {

    }

    @Override
    public void ignoreServiceMessages(Runnable runnable) {

    }

    @Override
    public FlowLogger getFlowLogger(String flowId) {
        return null;
    }

    @Override
    public FlowLogger getThreadLogger() {
        return null;
    }

    @Override
    public String getFlowId() {
        return null;
    }

    @Override
    public void logBuildProblem(BuildProblemData buildProblem) {

    }

    @Override
    public void message(String message) {

    }

    @Override
    public void error(String message) {

    }

    @Override
    public void warning(String message) {

    }

    @Override
    public void exception(Throwable th) {

    }

    @Override
    public void progressMessage(String message) {

    }
}
