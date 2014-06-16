package ru.yandex.qatools.allure.teamcity;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.agent.*;
import jetbrains.buildServer.agent.artifacts.ArtifactsWatcher;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.util.EventDispatcher;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import ru.yandex.qatools.allure.data.AllureReportGenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class AgentBuildEventsProvider extends AgentLifeCycleAdapter {

    private final ArtifactsWatcher artifactsWatcher;
    private static final Logger LOGGER = Loggers.AGENT;
    private static final String REPORT_FACE_DIRECTORY = "allure-report-face";

    public AgentBuildEventsProvider(@NotNull final EventDispatcher<AgentLifeCycleListener> dispatcher,
                                    @NotNull final ArtifactsWatcher artifactsWatcher) {
        dispatcher.addListener(this);
        this.artifactsWatcher = artifactsWatcher;
    }

    @Override
    public void buildStarted(@NotNull AgentRunningBuild runningBuild) {
        super.buildStarted(runningBuild);
        runningBuild.getBuildLogger();
    }

    @Override
    public void runnerFinished(@NotNull BuildRunnerContext runner, @NotNull BuildFinishedStatus status) {
        super.runnerFinished(runner, status);
        final BuildProgressLogger logger = runner.getBuild().getBuildLogger();
        final AgentRunningBuild runningBuild = runner.getBuild();
        final AgentBuildFeature buildFeature = getAllureBuildFeature(runningBuild);
        if (buildFeature != null) {
            if (!BuildFinishedStatus.INTERRUPTED.equals(status)) {
                final File checkoutDirectory = runner.getBuild().getCheckoutDirectory();
                final String relativeInputDirectory = buildFeature.getParameters().get(Constants.INPUT_DIRECTORY);
                final File inputAllureDirectory = new File(checkoutDirectory, relativeInputDirectory);
                final File outputAllureDirectory = new File(checkoutDirectory, Constants.RELATIVE_OUTPUT_DIRECTORY);
                logger.message(
                        "Generating Allure report to " + outputAllureDirectory.getAbsolutePath() + " using data from " + inputAllureDirectory.getAbsolutePath() + "."
                );
                try {
                    if (outputAllureDirectory.exists()){
                        FileUtils.deleteDirectory(outputAllureDirectory);
                    }
                    final AllureReportGenerator generator = new AllureReportGenerator(inputAllureDirectory);
                    generator.generate(outputAllureDirectory);
                    final String reportFaceWildcard = REPORT_FACE_DIRECTORY + File.separator + ".*";
                    copyStaticReportData(getCurrentJarFilePath(), outputAllureDirectory, reportFaceWildcard);
                    artifactsWatcher.addNewArtifactsPath(outputAllureDirectory.getAbsolutePath());
                    logger.message("Done");
                } catch (IOException e) {
                    logger.error(
                            "Caught an exception while " + outputAllureDirectory.getAbsolutePath() + " using data from " + inputAllureDirectory.getAbsolutePath() + "."
                    );
                    throw new RuntimeException(e);
                }
            } else {
                logger.message("Build was interrupted. Skipping Allure report generation.");
            }
        }
    }

    private AgentBuildFeature getAllureBuildFeature(final AgentRunningBuild runningBuild) {
        LOGGER.debug("Checking whether Allure build feature is present.");
        for (final AgentBuildFeature buildFeature : runningBuild.getBuildFeatures()) {
            if (Constants.BUILD_FEATURE_TYPE.equals(buildFeature.getType())) {
                LOGGER.debug("Allure build feature is present. Will publish Allure artifacts.");
                return buildFeature;
            }
        }
        LOGGER.debug("Allure build feature is not present. Will do nothing.");
        return null;
    }

    private static void copyStaticReportData(final File currentJarFile, final File outputDirectory, final String wildcard) throws IOException {
        final JarFile jar = new java.util.jar.JarFile(currentJarFile);
        final Enumeration entries = jar.entries();
        while (entries.hasMoreElements()) {
            final JarEntry file = (java.util.jar.JarEntry) entries.nextElement();
            if (file.getName().matches(wildcard)) {
                final String newFileName = file.getName().replace(REPORT_FACE_DIRECTORY + File.separator, "");
                if (newFileName.length() > 0) {
                    final String newFilePath = outputDirectory + File.separator + newFileName;
                    final File f = new File(newFilePath);
                    if (file.isDirectory()) {
                        if (f.exists()) {
                            FileUtils.deleteDirectory(f);
                        }
                        f.mkdir();
                        continue;
                    }
                    if (f.exists()){
                        f.delete();
                    }
                    final InputStream inputStream = jar.getInputStream(file);
                    final FileOutputStream fileOutputStream = new FileOutputStream(f);
                    while (inputStream.available() > 0) {
                        fileOutputStream.write(inputStream.read());
                    }
                    fileOutputStream.close();
                    inputStream.close();
                }
            }
        }
    }

    private File getCurrentJarFilePath() {
        return new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
    }

}
