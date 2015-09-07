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

import javax.xml.bind.JAXB;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import static java.lang.String.format;
import static ru.yandex.qatools.allure.AllureConstants.ALLURE_TOOL_NAME;
import static ru.yandex.qatools.allure.AllureConstants.ISSUE_TRACKER_PATTERN;
import static ru.yandex.qatools.allure.AllureConstants.RESULTS_DIRECTORY;
import static ru.yandex.qatools.allure.AllureConstants.TMS_PATTERN;

/**
 * @author Dmitry Baev charlie@yandex-team.ru
 *         Date: 06.08.15
 */
public class AllureBuildServiceAdapter extends BuildServiceAdapter {

    /**
     * The prefix of directory in build temporary directory witch will be
     * used as report directory.
     */
    public static final String ALLURE_REPORT = "allure-report";

    /**
     * The prefix of directory in build temporary directory witch will be
     * used to hold the allure.properties file.
     */
    public static final String ALLURE_CONFIG = "allure-config";

    /**
     * The name of Allure configuration file. The file with such name
     * will be created in {@link #ALLURE_CONFIG} temporary directory and
     * provided to allure commandline tool.
     */
    public static final String ALLURE_PROPERTIES = "allure.properties";

    /**
     * The name of main class of Allure commandline tool.
     */
    public static final String MAIN_CLASS = "ru.yandex.qatools.allure.CommandLine";

    /**
     * Can be used to notify agent artifacts publisher about new artifacts to be
     * published during the build.
     */
    private final ArtifactsWatcher artifactsWatcher;

    /**
     * The absolute path to agent working directory.
     */
    private String workingDirectory;

    /**
     * The absolute path to the directory to generate report into.
     */
    private Path reportDirectory;

    /**
     * The absolute path to the directory with Allure results.
     */
    private Path resultsDirectory;

    /**
     * The absolute path to the directory with Allure commandline tool.
     */
    private Path clientDirectory;

    /**
     * The absolute path to the Allure configuration file.
     */
    private Path propertiesFile;

    /**
     * Creates an instance of adapter.
     */
    public AllureBuildServiceAdapter(@NotNull final ArtifactsWatcher artifactsWatcher) {
        this.artifactsWatcher = artifactsWatcher;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterInitialized() throws RunBuildException {
        Path tempDirectory = Paths.get(getBuildTempDirectory().getAbsolutePath());

        workingDirectory = getCheckoutDirectory().getAbsolutePath();
        resultsDirectory = Paths.get(workingDirectory, getRunnerParameters().get(RESULTS_DIRECTORY));
        clientDirectory = Paths.get(getToolPath(ALLURE_TOOL_NAME));

        try {
            reportDirectory = Files.createTempDirectory(tempDirectory, ALLURE_REPORT);
            Path configDirectory = Files.createTempDirectory(tempDirectory, ALLURE_CONFIG);
            propertiesFile = configDirectory.resolve(ALLURE_PROPERTIES);
        } catch (IOException e) {
            throw new RunBuildException("Initialization error: ", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeProcessStarted() throws RunBuildException {
        if (Files.notExists(resultsDirectory)) {
            throw new RunBuildException(format(
                    "The results directory <%s> doesn't exists.",
                    resultsDirectory
            ));
        }

        writeEnvironment();
        writeProperties();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public ProgramCommandLine makeProgramCommandLine() throws RunBuildException {
        String lib = format("%s/*", clientDirectory.resolve("lib"));
        String conf = clientDirectory.resolve("conf").toString();

        JavaCommandLineBuilder cliBuilder = new JavaCommandLineBuilder();

        String javaHome = getRunnerParameters().get(JavaRunnerConstants.TARGET_JDK_HOME);
        cliBuilder.setJavaHome(javaHome);
        cliBuilder.setBaseDir(workingDirectory);
        cliBuilder.setWorkingDir(workingDirectory);
        cliBuilder.setJvmArgs(JavaRunnerUtil.extractJvmArgs(getRunnerParameters()));
        cliBuilder.setMainClass(MAIN_CLASS);
        cliBuilder.setClassPath(formatClassPath(lib, conf));

        cliBuilder.addSystemProperty("allure.home", clientDirectory.toString());
        cliBuilder.addSystemProperty("allure.config", propertiesFile.toString());

        cliBuilder.addProgramArg("-v");
        cliBuilder.addProgramArg("generate");
        cliBuilder.addProgramArg(resultsDirectory.toString());
        cliBuilder.addProgramArg("--output");
        cliBuilder.addProgramArg(reportDirectory.toString());

        return cliBuilder.build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterProcessSuccessfullyFinished() throws RunBuildException {
        artifactsWatcher.addNewArtifactsPath(reportDirectory.toString());
    }

    /**
     * Write the environment bean to results directory.
     */
    protected void writeEnvironment() {
        Environment environment = getEnvironment();
        Path env = resultsDirectory.resolve("environment.xml");
        JAXB.marshal(environment, env.toFile());
    }

    /**
     * Write the Allure configuration file to {@link #propertiesFile}.
     */
    protected void writeProperties() throws RunBuildException {
        try (OutputStream stream = Files.newOutputStream(propertiesFile)) {
            createPropertiesWith(ISSUE_TRACKER_PATTERN, TMS_PATTERN)
                    .store(stream, "the configuration file provided by Teamcity");
        } catch (IOException e) {
            throw new RunBuildException("Could not store Allure configuration file", e);
        }
    }

    /**
     * Creates an instance of {@link Properties} with values from
     * {@link #getRunnerParameters()} with given keys.
     */
    @NotNull
    protected Properties createPropertiesWith(String... keys) {
        Properties properties = new Properties();
        for (String key : keys) {
            properties.put(key, getRunnerParameters().get(key));
        }
        return properties;
    }

    /**
     * Returns an environment bean with information about build.
     */
    @NotNull
    protected Environment getEnvironment() {
        String buildTypeName = getBuild().getBuildTypeName();
        String buildNumber = getBuild().getBuildNumber();
        return new Environment()
                .withId(buildNumber)
                .withUrl(getBuildUrl())
                .withName(buildTypeName);
    }

    /**
     * Returns the build url for current build.
     *
     * @see #getTeamcityBaseUrl()
     */
    @NotNull
    protected String getBuildUrl() {
        return format(
                "%sviewLog.html?tab=buildResultsDiv&buildId=%s&buildTypeId=%s",
                getTeamcityBaseUrl(),
                getBuild().getBuildId(),
                getBuild().getBuildTypeExternalId()
        );
    }

    /**
     * Returns the base url of teamcity server.
     */
    @NotNull
    protected String getTeamcityBaseUrl() {
        String baseUrl = getConfigParameters().get("teamcity.serverUrl");
        return baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
    }

    /**
     * Format the classpath string from given classpath elements.
     */
    @NotNull
    protected String formatClassPath(@NotNull String first, @NotNull String... others) {
        String result = first;
        for (String other : others) {
            result += getClassPathSeparator() + other;
        }
        return result;
    }

    /**
     * Returns the platform-depended classpath separator.
     * @return semicolon for windows OS and colon for others.
     */
    @NotNull
    protected String getClassPathSeparator() {
        return getConfigParameters().get("teamcity.agent.jvm.path.separator");
    }
}
