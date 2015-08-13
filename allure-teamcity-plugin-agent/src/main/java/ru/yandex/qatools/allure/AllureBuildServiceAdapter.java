package ru.yandex.qatools.allure;

import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.artifacts.ArtifactsWatcher;
import jetbrains.buildServer.agent.runner.BuildServiceAdapter;
import jetbrains.buildServer.agent.runner.JavaCommandLineBuilder;
import jetbrains.buildServer.agent.runner.JavaRunnerUtil;
import jetbrains.buildServer.agent.runner.ProgramCommandLine;
import jetbrains.buildServer.runner.JavaRunnerConstants;
import org.jetbrains.annotations.NotNull;
import ru.yandex.qatools.commons.model.Environment;
import ru.yandex.qatools.commons.model.Parameter;

import javax.xml.bind.JAXB;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
    public static final String MAIN_CLASS = "ru.yandex.qatools.allure.AllureMain";

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

        String bundle = Paths.get(getToolPath(TOOL_NAME), BUNDLE).toAbsolutePath().toString();
        getLogger().message("Allure bundle found: " + bundle);

        String resultsDirectory = resolveResultsDirectory(relativeResultsDirectory);
        writeEnvironment(resultsDirectory);

        JavaCommandLineBuilder cliBuilder = new JavaCommandLineBuilder();
        cliBuilder.setJavaHome(getRunnerParameters().get(JavaRunnerConstants.TARGET_JDK_HOME));
        cliBuilder.setBaseDir(getCheckoutDirectory().getAbsolutePath());
        cliBuilder.setWorkingDir(getCheckoutDirectory().getAbsolutePath());
        cliBuilder.setJvmArgs(JavaRunnerUtil.extractJvmArgs(getRunnerParameters()));
        cliBuilder.setMainClass(MAIN_CLASS);
        cliBuilder.setClassPath(bundle);

        cliBuilder.addSystemProperty(ISSUE_TRACKER_PATTERN, issuesPattern);
        cliBuilder.addSystemProperty(TMS_PATTERN, tmsPattern);
        cliBuilder.addProgramArg(resultsDirectory);
        cliBuilder.addProgramArg(getReportDirectory());

        return cliBuilder.build();
    }

    @Override
    public void afterProcessSuccessfullyFinished() throws RunBuildException {
        artifactsWatcher.addNewArtifactsPath(getReportDirectory());
    }

    protected void writeEnvironment(String reportDirectory) throws RunBuildException {
        Environment environment = getEnvironment();
        Path env = Paths.get(reportDirectory).resolve("environment.xml");
        JAXB.marshal(environment, env.toFile());
    }

    protected Environment getEnvironment() {
        String projectName = getBuild().getProjectName();
        String buildNumber = getBuild().getBuildNumber();
        Environment environment = new Environment()
                .withId(buildNumber)
                .withName(projectName);
        for (String key : getConfigParameters().keySet()) {
            String value = getConfigParameters().get(key);
            environment.withParameter(new Parameter().withName(key).withKey(key).withValue(value));
        }
        return environment;
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

    protected String getRunnerParameter(String key) {
        String value = getRunnerParameters().get(key);
        getLogger().message(String.format("%s: %s", key, value));
        return value;
    }
}
