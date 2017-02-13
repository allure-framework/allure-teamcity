package ru.yandex.qatools.allure;

import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.BuildProgressLogger;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.agent.artifacts.ArtifactsWatcher;
import jetbrains.buildServer.agent.runner.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SystemUtils;
import org.jetbrains.annotations.NotNull;
import ru.yandex.qatools.allure.callables.AddExecutorInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import static java.lang.String.format;
import static ru.yandex.qatools.allure.AllureConstants.*;

/**
 * @author Dmitry Baev charlie@yandex-team.ru
 *         Date: 06.08.15
 */
class AllureBuildServiceAdapter extends BuildServiceAdapter {

    private static final String ALLURE_EXEC_NAME = "allure";

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
     * Creates an instance of adapter.
     */
    AllureBuildServiceAdapter(@NotNull final ArtifactsWatcher artifactsWatcher) {
        this.artifactsWatcher = artifactsWatcher;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterInitialized() throws RunBuildException {

        workingDirectory = getCheckoutDirectory().getAbsolutePath();
        resultsDirectory = Paths.get(workingDirectory, getRunnerParameters().get(RESULTS_DIRECTORY));
        clientDirectory = getClientDirectory(Paths.get(getToolPath(ALLURE_TOOL_NAME)));
        reportDirectory = Paths.get(workingDirectory, getRunnerParameters().get(REPORT_PATH_PREFIX));

    }


    private static Path getClientDirectory(Path client) throws RunBuildException {
        DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept(Path file) throws IOException {
                return Files.isDirectory(file);
            }
        };
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(client, filter)) {
            for (Path path : stream)
                if (path.getFileName().toString().startsWith("allure"))
                    return path;
        } catch (IOException e) {
            throw new RunBuildException("Cannot find allure tool folder.", e);
        }
        return client;
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
        copyHistory();
        try {
            clearReport();
            addExecutorInfo();
        } catch (Exception e) {
            throw new RunBuildException(e);
        }
    }

    private void clearReport() throws IOException {
        if (Files.exists(reportDirectory)) {
            FileUtils.deleteDirectory(reportDirectory.toFile());
        }
    }

    /**
     * Write the history file to results directory.
     */
    private void copyHistory() {

        Path source = Paths.get(reportDirectory.toAbsolutePath().toString() + "/data/history.json");
        if (Files.exists(source)) {
            Path destination = Paths.get(resultsDirectory.toString() + "/history.json");
            try {
                Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                getLogger().message("Cannot copy history file. Reason: " + e.getMessage());
                getLogger().exception(e);
            }
        }

    }

    /**
     * Write the test executor info to results directory.
     */
    private void addExecutorInfo() throws IOException {
        String rootUrl = getTeamcityBaseUrl();
        String buildUrl = getBuildUrl();
        String reportUrl = getArtifactsUrl();
        new AddExecutorInfo(rootUrl, "#"+getBuild().getBuildId(), buildUrl, reportUrl).invoke(resultsDirectory);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterProcessSuccessfullyFinished() throws RunBuildException {
        artifactsWatcher.addNewArtifactsPath(reportDirectory.toString());
    }

    /**
     * Returns the build's artifacts url for current build.
     * @see #getTeamcityBaseUrl()
     */
    @NotNull
    private String getArtifactsUrl() {
        return format(
                "%sviewLog.html?tab=artifacts&buildId=%s&buildTypeId=%s",
                getTeamcityBaseUrl(),
                getBuild().getBuildId(),
                getBuild().getBuildTypeExternalId()
        );
    }

    /**
     * Returns the build url for current build.
     * @see #getTeamcityBaseUrl()
     */
    @NotNull
    private String getBuildUrl() {
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
    private String getTeamcityBaseUrl() {
        String baseUrl = getConfigParameters().get("teamcity.serverUrl");
        return baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public ProgramCommandLine makeProgramCommandLine() throws RunBuildException {

        BuildProgressLogger buildLogger = getLogger();
        BuildRunnerContext buildRunnerContext = getRunnerContext();
        Map<String, String> programEnvironmentVariables = buildRunnerContext.getBuildParameters().getEnvironmentVariables();
        String programPath = getProgramPath();
        List<String> programArgs = getProgramArgs();

        buildLogger.message("Program environment variables: " + programEnvironmentVariables.toString());
        buildLogger.message("Program working directory: " + workingDirectory);
        buildLogger.message("Program path: " + programPath);
        buildLogger.message("Program args: " + programArgs.toString());

        return new SimpleProgramCommandLine(programEnvironmentVariables, workingDirectory, programPath, programArgs);
    }

    private List<String> getProgramArgs() {
        List<String> list = new ArrayList<>();

        list.add("generate");
        list.add(resultsDirectory.toString());
        list.add("-o");
        list.add(reportDirectory.toString());

        return list;
    }

    private String getProgramPath() throws RunBuildException {

        BuildProgressLogger buildLogger = getLogger();
        String path = clientDirectory.toAbsolutePath().toString();
        String executableName = getExecutableName();

        String executableFile = path + File.separatorChar + "bin" + File.separatorChar + executableName;
        File file = new File(executableFile);

        if (!file.exists())
            throw new RunBuildException("Cannot find executable \'" + executableFile + "\'");
        if (!file.setExecutable(true))
            buildLogger.message("Cannot set file: " + executableFile + " executable.");

        return file.getAbsolutePath();

    }

    @NotNull
    private static String getExecutableName() {
        return SystemUtils.IS_OS_WINDOWS ? ALLURE_EXEC_NAME + ".bat" : ALLURE_EXEC_NAME;
    }
}
