package ru.yandex.qatools.allure.teamcity;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.agent.*;
import jetbrains.buildServer.agent.artifacts.ArtifactsWatcher;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.util.EventDispatcher;
import org.jetbrains.annotations.NotNull;
import ru.yandex.qatools.allure.report.AllureReportBuilder;
import ru.yandex.qatools.allure.report.utils.DependencyResolver;

import java.io.File;
import java.util.Arrays;

public class AgentBuildEventsProvider extends AgentLifeCycleAdapter {

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
        BuildProgressLogger logger = runner.getBuild().getBuildLogger();

        logger.message("Allure Report: report processing started");
        AgentRunningBuild runningBuild = runner.getBuild();
        AgentBuildFeature buildFeature = getAllureBuildFeature(runningBuild);
        if (buildFeature == null) {
            return;
        }

        if (BuildFinishedStatus.INTERRUPTED.equals(status)) {
            logger.message("Allure Report: Build was interrupted. Skipping Allure report generation.");
            return;
        }

        File checkoutDirectory = runner.getBuild().getCheckoutDirectory();
        String resultsMask[] = buildFeature.getParameters().get(Parameters.RESULTS_MASK).split(";");
        logger.message(String.format("Allure Report: analyse results mask %s", Arrays.toString(resultsMask)));

        File[] allureResultDirectoryList = FileUtils.findFilesByMask(checkoutDirectory, resultsMask);
        logger.message(String.format("Allure Report: analyse results directories %s",
                Arrays.toString(allureResultDirectoryList)));

        File allureReportDirectory = new File(checkoutDirectory, Parameters.RELATIVE_OUTPUT_DIRECTORY);
        logger.warning("Allure Report: prepare allure report directory");
        if (allureReportDirectory.exists() && !allureReportDirectory.delete()) {
            logger.warning("Allure Report: cant clean allure report directory");
            return;
        }

        try {
            String version = buildFeature.getParameters().get(Parameters.REPORT_VERSION);
            File mavenLocalFolder = new File(runner.getBuild().getAgentTempDirectory(), "repository");

            DependencyResolver resolver = DependencyResolverBuilder.buildDependencyResolver(mavenLocalFolder);
            AllureReportBuilder builder = new AllureReportBuilder(version, allureReportDirectory, resolver);

            logger.message(String.format("Allure Report: process tests results to directory [%s]",
                    allureReportDirectory));
            builder.processResults(allureResultDirectoryList);

            logger.message(String.format("Allure Report: unpack report face to directory [%s]",
                    allureReportDirectory));

            builder.unpackFace();

            artifactsWatcher.addNewArtifactsPath(allureReportDirectory.getAbsolutePath());
        } catch (Exception e) {
            logger.exception(e);
        }
    }

    private AgentBuildFeature getAllureBuildFeature(final AgentRunningBuild runningBuild) {
        LOGGER.debug("Allure Report: checking whether Allure build feature is present.");
        for (final AgentBuildFeature buildFeature : runningBuild.getBuildFeatures()) {
            if (Parameters.BUILD_FEATURE_TYPE.equals(buildFeature.getType())) {
                LOGGER.debug("Allure Report: build feature is present. Will publish Allure artifacts.");
                return buildFeature;
            }
        }
        LOGGER.debug("Allure Report: build feature is not present. Will do nothing.");
        return null;
    }
}
