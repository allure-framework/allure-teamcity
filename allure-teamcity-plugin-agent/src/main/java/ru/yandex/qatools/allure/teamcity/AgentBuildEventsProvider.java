package ru.yandex.qatools.allure.teamcity;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.agent.*;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.util.EventDispatcher;
import org.jetbrains.annotations.NotNull;
import ru.yandex.qatools.allure.report.AllureReportBuilder;
import ru.yandex.qatools.allure.report.utils.AetherObjectFactory;
import ru.yandex.qatools.allure.report.utils.DependencyResolver;

import java.io.File;

public class AgentBuildEventsProvider extends AgentLifeCycleAdapter {

    private static final Logger LOGGER = Loggers.AGENT;

    public AgentBuildEventsProvider(@NotNull final EventDispatcher<AgentLifeCycleListener> dispatcher) {
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
        String version = buildFeature.getParameters().get(Parameters.REPORT_VERSION);

        File[] allureResultDirectoryList = FileUtils.findFilesByMask(checkoutDirectory, resultsMask);
        File allureReportDirectory = new File(checkoutDirectory, Parameters.RELATIVE_OUTPUT_DIRECTORY);

        try {
            DependencyResolver resolver = AetherObjectFactory.newResolver();
            AllureReportBuilder builder = new AllureReportBuilder(version, allureReportDirectory, resolver);
            builder.processResults(allureResultDirectoryList);
            builder.unpackFace();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private AgentBuildFeature getAllureBuildFeature(final AgentRunningBuild runningBuild) {
        LOGGER.debug("Checking whether Allure build feature is present.");
        for (final AgentBuildFeature buildFeature : runningBuild.getBuildFeatures()) {
            if (Parameters.BUILD_FEATURE_TYPE.equals(buildFeature.getType())) {
                LOGGER.debug("Allure build feature is present. Will publish Allure artifacts.");
                return buildFeature;
            }
        }
        LOGGER.debug("Allure build feature is not present. Will do nothing.");
        return null;
    }
}
