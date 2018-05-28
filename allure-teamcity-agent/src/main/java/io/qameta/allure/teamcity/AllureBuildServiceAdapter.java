package io.qameta.allure.teamcity;

import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.BuildProgressLogger;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.agent.artifacts.ArtifactsWatcher;
import jetbrains.buildServer.agent.runner.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SystemUtils;
import org.jetbrains.annotations.NotNull;
import io.qameta.allure.teamcity.callables.AddExecutorInfo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;


import static io.qameta.allure.teamcity.utils.ZipUtils.listEntries;
import static java.lang.String.format;
import static io.qameta.allure.teamcity.AllureConstants.*;

/**
 * @author Dmitry Baev charlie@yandex-team.ru
 * Date: 06.08.15
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeProcessStarted() throws RunBuildException {
        if (Files.notExists(resultsDirectory)) {
            throw new RunBuildException(format("The results directory <%s> doesn't exists.", resultsDirectory));
        }
        copyHistory();
        try {
            clearReport();
            addExecutorInfo();
        } catch (Exception e) {
            throw new RunBuildException(e);
        }
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterProcessSuccessfullyFinished() {
        artifactsWatcher.addNewArtifactsPath(reportDirectory.toString());
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

        try {
            Path lastFinishedArtifactZip = Files.createTempFile("artifact", String.valueOf(getBuild().getBuildId()));
            URL lastFinishedArtifactUrl = new URL(getLastFinishedArtifactUrl());
            getLogger().message(lastFinishedArtifactUrl.toString());

            String passwdstring = getServerAuthentication();
            String encoding = Base64.getUrlEncoder().encodeToString(passwdstring.getBytes("utf-8"));

            URLConnection uc = lastFinishedArtifactUrl.openConnection();
            uc.setRequestProperty("Authorization", "Basic " + encoding);
            InputStream inputStream = uc.getInputStream();
            Files.copy(inputStream, lastFinishedArtifactZip, StandardCopyOption.REPLACE_EXISTING);
            try (ZipFile archive = new ZipFile(lastFinishedArtifactZip.toString())) {
                copyHistoryToResultsPath(archive);
            }
        } catch (Exception e) {
            getLogger().message("Cannot copy history file. Reason: " + e.getMessage());
            getLogger().exception(e);
        }

    }

    private void copyHistoryToResultsPath(ZipFile archive) throws IOException {
        for (final ZipEntry historyEntry : listEntries(archive, "history")) {
            final String historyFile = historyEntry.getName();

            if (!historyEntry.isDirectory()) {
                try (InputStream entryStream = archive.getInputStream(historyEntry)) {
                    Files.copy(entryStream, resultsDirectory.resolve(historyFile));
                }
            } else {
                File dir = new File(resultsDirectory.resolve(historyFile).toString());
                dir.mkdir();
            }


        }
    }

    /**
     * Write the test executor info to results directory.
     */
    private void addExecutorInfo() throws IOException {
        String rootUrl = getTeamcityBaseUrl();
        String buildUrl = getBuildUrl();
        String buildNumber = getBuildNumber();
        String reportUrl = getArtifactsUrl();

        String buildName = format("%s / %s # %s",
                getBuild().getProjectName(), getBuild().getBuildTypeName(), buildNumber);
        new AddExecutorInfo(rootUrl, buildName, buildUrl, buildNumber, reportUrl)
                .invoke(resultsDirectory);
    }

    /**
     * Returns the build's artifacts url for the last finished build.
     *
     * @see #getTeamcityBaseUrl()
     */
    @NotNull
    private String getLastFinishedArtifactUrl() {
        StringBuilder artifactUrl = new StringBuilder();
        artifactUrl.append(format(
                "%s/repository/downloadAll/%s/.lastFinished/artifacts.zip",
                getTeamcityBaseUrl(),
                getBuild().getBuildTypeExternalId()));
        String branch = getConfigParameters().get("teamcity.build.branch");
        if (Objects.nonNull(branch)) {
            artifactUrl.append(format("?branch=%s", branch));
        }
        return artifactUrl.toString();
    }

    @NotNull
    private String getServerAuthentication() {
        return format(
                "%s:%s",
                getSystemProperties().get("teamcity.auth.userId"),
                getSystemProperties().get("teamcity.auth.password")
        );
    }

    /**
     * Returns the build's artifacts url for current build.
     *
     * @see #getTeamcityBaseUrl()
     */
    @NotNull
    private String getArtifactsUrl() {
        return format(
                "%srepository/download/%s/%s:id/index.html",
                getTeamcityBaseUrl(),
                getBuild().getBuildTypeExternalId(),
                getBuild().getBuildId()
        );
    }

    private String getBuildNumber() {
        return getBuild().getBuildNumber();
    }

    /**
     * Returns the build url for current build.
     *
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

    private static Path getClientDirectory(Path client) throws RunBuildException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(client, Files::isDirectory)) {
            for (Path path : stream) {
                if (path.getFileName().toString().startsWith("allure")) {
                    return path;
                }
            }
        } catch (IOException e) {
            throw new RunBuildException("Cannot find allure tool folder.", e);
        }
        return client;
    }


    @NotNull
    private static String getExecutableName() {
        return SystemUtils.IS_OS_WINDOWS ? ALLURE_EXEC_NAME + ".bat" : ALLURE_EXEC_NAME;
    }
}
