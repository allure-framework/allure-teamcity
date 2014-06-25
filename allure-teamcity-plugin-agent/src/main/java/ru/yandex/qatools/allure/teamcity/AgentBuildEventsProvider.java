package ru.yandex.qatools.allure.teamcity;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.agent.*;
import jetbrains.buildServer.agent.artifacts.ArtifactsWatcher;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.messages.DefaultMessagesInfo;
import jetbrains.buildServer.util.EventDispatcher;
import org.jetbrains.annotations.NotNull;
import ru.yandex.qatools.allure.report.AllureReportBuilder;
import ru.yandex.qatools.allure.report.utils.DependencyResolver;

import java.io.File;
import java.util.Arrays;

public class AgentBuildEventsProvider extends AgentLifeCycleAdapter {

    public static final String ALLURE_ACTIVITY_NAME = "Allure report generation";

    private static final Logger LOGGER = Loggers.AGENT;

    private final ArtifactsWatcher artifactsWatcher;

    public AgentBuildEventsProvider(@NotNull final EventDispatcher<AgentLifeCycleListener> dispatcher,
                                    @NotNull final ArtifactsWatcher artifactsWatcher) {
        this.artifactsWatcher = artifactsWatcher;
        dispatcher.addListener(this);
    }

    @Override
    public void buildStarted(@NotNull AgentRunningBuild runningBuild) {
        super.buildStarted(runningBuild);
        runningBuild.getBuildLogger();
    }

    @Override
    public void runnerFinished(@NotNull BuildRunnerContext runner, @NotNull BuildFinishedStatus status) {
        super.runnerFinished(runner, status);
        try {
            BuildProgressLogger logger = runner.getBuild().getBuildLogger();
            logger.activityStarted(ALLURE_ACTIVITY_NAME, DefaultMessagesInfo.BLOCK_TYPE_BUILD_STEP);
            AgentRunningBuild runningBuild = runner.getBuild();
            AgentBuildFeature buildFeature = getAllureBuildFeature(runningBuild);
            if (buildFeature == null) {
                return;
            }

            if (BuildFinishedStatus.INTERRUPTED.equals(status)) {
                logger.message("Build was interrupted. Skipping Allure report generation.");
                return;
            }

            File checkoutDirectory = runner.getBuild().getCheckoutDirectory();
            String resultsMask[] = buildFeature.getParameters().get(Parameters.RESULTS_MASK).split(";");
            logger.message(String.format("analyse results mask %s", Arrays.toString(resultsMask)));

            File[] allureResultDirectoryList = FileUtils.findFilesByMask(checkoutDirectory, resultsMask);
            logger.message(String.format("analyse results directories %s",
                    Arrays.toString(allureResultDirectoryList)));

            File tempDirectory = runner.getBuild().getAgentTempDirectory();
            File allureReportDirectory = new File(tempDirectory, Parameters.RELATIVE_OUTPUT_DIRECTORY);
            logger.message(String.format("prepare allure report directory [%s]",
                    allureReportDirectory.getAbsolutePath()));

            try {
                String version = buildFeature.getParameters().get(Parameters.REPORT_VERSION);
                File mavenLocalFolder = new File(runner.getBuild().getAgentTempDirectory(), "repository");

                DependencyResolver resolver = DependencyResolverBuilder.buildDependencyResolver(mavenLocalFolder);

                logger.message(String.format("prepare report generator with version: %s", version));
                AllureReportBuilder builder = new AllureReportBuilder(version, allureReportDirectory, resolver);

                logger.message(String.format("process tests results to directory [%s]",
                        allureReportDirectory.getAbsolutePath()));
                builder.processResults(allureResultDirectoryList);

                logger.message(String.format("unpack report face to directory [%s]",
                        allureReportDirectory.getAbsolutePath()));
                builder.unpackFace();

                artifactsWatcher.addNewArtifactsPath(allureReportDirectory.getAbsolutePath());
            } catch (Exception e) {
                logger.error(e.getMessage());
                logger.exception(e);
            }

            logger.activityFinished(ALLURE_ACTIVITY_NAME, DefaultMessagesInfo.BLOCK_TYPE_BUILD_STEP);
        } catch (Throwable e) {
            runner.getBuild().getBuildLogger().exception(e);
        }
    }

    private boolean isNeedToGenerateReport(BuildRunnerContext runner, BuildFinishedStatus status) {
        return true;
    }

    private AgentBuildFeature getAllureBuildFeature(final AgentRunningBuild runningBuild) {
        for (final AgentBuildFeature buildFeature : runningBuild.getBuildFeatures()) {
            if (Parameters.BUILD_FEATURE_TYPE.equals(buildFeature.getType())) {
                return buildFeature;
            }
        }
        return null;
    }
}
