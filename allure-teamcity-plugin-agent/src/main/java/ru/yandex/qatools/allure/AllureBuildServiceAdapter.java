package ru.yandex.qatools.allure;

import com.intellij.openapi.util.SystemInfo;
import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.artifacts.ArtifactsWatcher;
import jetbrains.buildServer.agent.runner.BuildServiceAdapter;
import jetbrains.buildServer.agent.runner.ProgramCommandLine;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static ru.yandex.qatools.allure.AllureConstants.ISSUE_TRACKER_PATTERN;
import static ru.yandex.qatools.allure.AllureConstants.REPORT_VERSION;
import static ru.yandex.qatools.allure.AllureConstants.RESULTS_DIRECTORY;
import static ru.yandex.qatools.allure.AllureConstants.TMS_PATTERN;

/**
 * @author Dmitry Baev charlie@yandex-team.ru
 *         Date: 06.08.15
 */
public class AllureBuildServiceAdapter extends BuildServiceAdapter {

    public static final String BUNDLE = "allure-bundle.jar";

    public static final String JAVA_HOME = "JAVA_HOME";

    public static final String ALLURE_REPORT = "allure-report";

    public static final String TOOL_NAME = "allure";

    private final ArtifactsWatcher artifactsWatcher;

    public AllureBuildServiceAdapter(@NotNull final ArtifactsWatcher artifactsWatcher) {
        this.artifactsWatcher = artifactsWatcher;
    }

    @NotNull
    private String getReportDirectory() {
        return new File(getBuildTempDirectory(), ALLURE_REPORT).getAbsolutePath();
    }

    @NotNull
    @Override
    public ProgramCommandLine makeProgramCommandLine() throws RunBuildException {
        getLogger().message("Allure report generator runner parameters:");

        String relativeResultsDirectory = getRunnerParameter(RESULTS_DIRECTORY);
        getRunnerParameter(REPORT_VERSION);

        String issuesPattern = getRunnerParameter(ISSUE_TRACKER_PATTERN);
        String tmsPattern = getRunnerParameter(TMS_PATTERN);

        String javaExecutable = getJavaExecutable();
        getLogger().message("Java executable found: " + javaExecutable);

        String bundle = Paths.get(getToolPath(TOOL_NAME), BUNDLE).toAbsolutePath().toString();
        getLogger().message("Allure bundle found: " + bundle);

        List<String> args = Arrays.asList(
                createSystemPropertyArgument(ISSUE_TRACKER_PATTERN, issuesPattern),
                createSystemPropertyArgument(TMS_PATTERN, tmsPattern),
                "-jar",
                bundle,
                resolveResultsDirectory(relativeResultsDirectory),
                getReportDirectory()
        );

        return createProgramCommandline(javaExecutable, args);
    }

    @Override
    public void afterProcessSuccessfullyFinished() throws RunBuildException {
        artifactsWatcher.addNewArtifactsPath(getReportDirectory());
    }

    protected String resolveResultsDirectory(String resultsDirectory) throws RunBuildException {
        Path resultsDirectoryPath = Paths.get(resultsDirectory);
        if (resultsDirectoryPath.isAbsolute()) {
            throw new RunBuildException("The results directory path should be relative.");
        }

        Path checkoutDirectory = Paths.get(getBuild().getCheckoutDirectory().getAbsolutePath());
        Path resolved = checkoutDirectory.resolve(resultsDirectoryPath);

        if (Files.notExists(resolved)) {
            throw new RunBuildException(String.format(
                    "The results directory <%s> doesn't exists.",
                    resultsDirectory
            ));
        }

        return resolved.toAbsolutePath().toString();
    }

    protected String createSystemPropertyArgument(String key, String value) {
        return String.format("-D%s=%s", key, value);
    }

    protected String getRunnerParameter(String key) {
        String value = getRunnerParameters().get(key);
        getLogger().message(String.format("%s: %s", key, value));
        return value;
    }

    protected String getJavaExecutable() throws RunBuildException {
        if (!getEnvironmentVariables().containsKey(JAVA_HOME)) {
            throw new RunBuildException("Could not find java installation: " +
                    "environment variable " + JAVA_HOME + " is not installed.");
        }
        String javaHome = getEnvironmentVariables().get(JAVA_HOME);

        String javaExecutableName = SystemInfo.isWindows ? "java.exe" : "java";
        return Paths.get(javaHome, "bin", javaExecutableName).toAbsolutePath().toString();
    }
}
