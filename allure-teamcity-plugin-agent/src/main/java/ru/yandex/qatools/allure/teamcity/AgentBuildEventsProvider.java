package ru.yandex.qatools.allure.teamcity;

import jetbrains.buildServer.agent.artifacts.ArtifactsWatcher;
import ru.yandex.qatools.allure.report.AllureReportBuilder;
import jetbrains.buildServer.messages.DefaultMessagesInfo;
import jetbrains.buildServer.util.EventDispatcher;
import org.jetbrains.annotations.NotNull;
import jetbrains.buildServer.agent.*;
import ru.yandex.qatools.allure.report.utils.AetherObjectFactory;
import ru.yandex.qatools.allure.report.utils.DependencyResolver;

import java.io.File;
import java.util.Arrays;

import static ru.yandex.qatools.allure.report.utils.AetherObjectFactory.newDependencyResolver;

public class AgentBuildEventsProvider extends AgentLifeCycleAdapter {

    public static final String ALLURE_ACTIVITY_NAME = "Allure report generation";

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
    public void beforeBuildFinish(@NotNull AgentRunningBuild build, @NotNull BuildFinishedStatus buildStatus) {
        super.beforeBuildFinish(build, buildStatus);
        BuildProgressLogger logger = build.getBuildLogger();
        logger.activityStarted(ALLURE_ACTIVITY_NAME, DefaultMessagesInfo.BLOCK_TYPE_BUILD_STEP);
        try {
            AgentBuildFeature buildFeature = getAllureBuildFeature(build);
            if (buildFeature == null) {
                return;
            }

            AllureReportConfig config = new AllureReportConfig(buildFeature.getParameters());
            ReportBuildPolicy reportBuildPolicy = config.getReportBuildPolicy();

            if (!isNeedToBuildReport(reportBuildPolicy, buildStatus)) {
                logger.message("Build was interrupted. Skipping Allure report generation.");
                return;
            }

            File checkoutDirectory = build.getCheckoutDirectory();
            String resultsPattern = config.getResultsPattern();
            logger.message(String.format("analyse results pattern %s", resultsPattern));

            File[] allureResultDirectoryList = FileUtils.findFilesByGlob(checkoutDirectory, resultsPattern);
            logger.message(String.format("analyse results directories %s",
                    Arrays.toString(allureResultDirectoryList)));
            
            if ( 
                    (allureResultDirectoryList.length == 0) 
                    && (resultsPattern.length == 1)
                    && (resultsPattern[0] != null)
            ){
                String absolutePath = resultsPattern[0];
                File absolutePathFile = new File(absolutePath);
                if (absolutePathFile.exists()){
                    logger.message(String.format("using results pattern %s as absolute path", absolutePath));
                    allureResultDirectoryList = new File[]{absolutePathFile};
                }
            }

            File tempDirectory = build.getAgentTempDirectory();
            File allureReportDirectory = new File(tempDirectory, AllureReportConfig.REPORT_PATH);
            logger.message(String.format("prepare allure report directory [%s]",
                    allureReportDirectory.getAbsolutePath()));

            String version = config.getReportVersion();

            logger.message(String.format("prepare report generator with version: %s", version));

            File repositoriesDirectory = new File(tempDirectory, AllureReportConfig.REPOSITORY_PATH);
            DependencyResolver dependencyResolver = newDependencyResolver(repositoriesDirectory,
                    AetherObjectFactory.MAVEN_CENTRAL_URL, AetherObjectFactory.SONATYPE_RELEASES_URL);
            AllureReportBuilder builder = new AllureReportBuilder(version, allureReportDirectory, dependencyResolver);

            logger.message(String.format("process tests results to directory [%s]",
                    allureReportDirectory.getAbsolutePath()));
            builder.processResults(allureResultDirectoryList);

            logger.message(String.format("unpack report face to directory [%s]",
                    allureReportDirectory.getAbsolutePath()));
            builder.unpackFace();

            artifactsWatcher.addNewArtifactsPath(allureReportDirectory.getAbsolutePath());
        } catch (Throwable e) {
            build.getBuildLogger().exception(e);
        } finally {
            logger.activityFinished(ALLURE_ACTIVITY_NAME, DefaultMessagesInfo.BLOCK_TYPE_BUILD_STEP);
        }
    }

    private boolean isNeedToBuildReport(ReportBuildPolicy policy, BuildFinishedStatus status) {
        switch (policy) {
            case ALWAYS: {
                return true;
            }

            case WITH_PROBLEMS: {
                return status.equals(BuildFinishedStatus.FINISHED_WITH_PROBLEMS);
            }

            case FAILED: {
                return status.equals(BuildFinishedStatus.FINISHED_FAILED);
            }

            default: {
                return false;
            }
        }
    }

    private AgentBuildFeature getAllureBuildFeature(final AgentRunningBuild runningBuild) {
        for (final AgentBuildFeature buildFeature : runningBuild.getBuildFeatures()) {
            if (AllureReportConfig.BUILD_FEATURE_KEY.equals(buildFeature.getType())) {
                return buildFeature;
            }
        }
        return null;
    }
}
