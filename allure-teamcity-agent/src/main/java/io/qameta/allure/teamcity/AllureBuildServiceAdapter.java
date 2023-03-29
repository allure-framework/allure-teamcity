/*
 *  Copyright 2016-2023 Qameta Software OÜ
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.qameta.allure.teamcity;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.teamcity.callables.AddExecutorInfo;
import io.qameta.allure.teamcity.utils.ZipUtils;
import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.BuildProgressLogger;
import jetbrains.buildServer.agent.artifacts.ArtifactsWatcher;
import jetbrains.buildServer.agent.runner.BuildServiceAdapter;
import jetbrains.buildServer.agent.runner.ProgramCommandLine;
import jetbrains.buildServer.agent.runner.SimpleProgramCommandLine;
import jetbrains.buildServer.runner.JavaRunnerConstants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static io.qameta.allure.teamcity.AllureConstants.ALLURE_ARTIFACT_HISTORY_LOCATION;
import static io.qameta.allure.teamcity.AllureConstants.ALLURE_ARTIFACT_META_LOCATION;
import static io.qameta.allure.teamcity.AllureConstants.ALLURE_TOOL_NAME;
import static io.qameta.allure.teamcity.AllureConstants.PUBLISH_MODE;
import static io.qameta.allure.teamcity.AllureConstants.REPORT_PATH_PREFIX;
import static io.qameta.allure.teamcity.AllureConstants.RESULTS_DIRECTORY;
import static io.qameta.allure.teamcity.utils.ZipUtils.listEntries;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang3.StringUtils.endsWith;

/**
 * @author Dmitry Baev charlie@yandex-team.ru
 * Date: 06.08.15
 */
@SuppressWarnings("PMD.GodClass")
class AllureBuildServiceAdapter extends BuildServiceAdapter {

    private static final String ARCHIVE_NAME = "allure-report.zip";

    private static final String ALLURE_EXEC_NAME = "allure";
    private static final String TEAMCITY_BUILD_BRANCH = "teamcity.build.branch";
    private static final String HISTORY = "history";
    private static final String SUMMARY_JSON = "summary.json";
    private static final String SLASH = "/";

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

    private AllurePublishMode publishMode;

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
        resultsDirectory = getWorkingDirectoryPath().resolve(getRunnerParameters().get(RESULTS_DIRECTORY));
        clientDirectory = getClientDirectory(Paths.get(getToolPath(ALLURE_TOOL_NAME)));
        reportDirectory = getWorkingDirectoryPath().resolve(getRunnerParameters().get(REPORT_PATH_PREFIX));
        publishMode = Optional.ofNullable(getRunnerParameters().get(PUBLISH_MODE)).map(AllurePublishMode::valueOf)
                .orElse(AllurePublishMode.ARCHIVE);
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

        final BuildProgressLogger buildLogger = getLogger();

        final Map<String, String> envVariables = new HashMap<>(getRunnerContext()
                .getBuildParameters()
                .getEnvironmentVariables());
        Optional.ofNullable(getRunnerContext().getRunnerParameters().get(JavaRunnerConstants.TARGET_JDK_HOME))
                .ifPresent(javaHome -> envVariables.put("JAVA_HOME", javaHome));

        final String programPath = getProgramPath();
        final List<String> programArgs = getProgramArgs();

        buildLogger.message("Program environment variables: " + envVariables);
        buildLogger.message("Program working directory: " + workingDirectory);
        buildLogger.message("Program path: " + programPath);
        buildLogger.message("Program args: " + programArgs);

        return new SimpleProgramCommandLine(envVariables, workingDirectory, programPath, programArgs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterProcessSuccessfullyFinished() throws RunBuildException {
        try {
            publishAllureReport();
            publishAllureHistory();
            publishAllureSummary();
        } catch (IOException e) {
            throw new RunBuildException(e);
        }
    }

    private void publishAllureReport() throws IOException {
        if (publishMode.equals(AllurePublishMode.ARCHIVE)) {
            final Path reportArchive = getAgentTempDirectoryPath().resolve(ARCHIVE_NAME);
            final List<Path> reportFiles = Files.walk(reportDirectory)
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());
            ZipUtils.zip(reportArchive, reportDirectory.getParent(), reportFiles);
            artifactsWatcher.addNewArtifactsPath(reportArchive.toString());
        }
        if (publishMode.equals(AllurePublishMode.PLAIN)) {
            final String reportDirectoryName = reportDirectory.toFile().getName();
            artifactsWatcher.addNewArtifactsPath(format("%s => %s",
                    reportDirectory.toString(),
                    reportDirectoryName));
        }
    }

    private void publishAllureHistory() throws IOException {
        final Path historyArchive = getAgentTempDirectoryPath().resolve("history.zip");

        final Path historyDirectory = reportDirectory.resolve(HISTORY);
        final List<Path> historyFiles = Files.walk(historyDirectory)
                .filter(Files::isRegularFile)
                .collect(Collectors.toList());
        ZipUtils.zip(historyArchive, historyDirectory.getParent(), historyFiles);
        artifactsWatcher.addNewArtifactsPath(historyArchive + " => " + ALLURE_ARTIFACT_META_LOCATION);
    }

    private void publishAllureSummary() throws IOException {
        final Path summaryOutputPath = getAgentTempDirectoryPath().resolve(SUMMARY_JSON);

        final Path summaryWidgetPath = reportDirectory.resolve("widgets").resolve(SUMMARY_JSON);
        final ObjectMapper mapper = new ObjectMapper();

        final AllureReportSummary summary = mapper.readValue(summaryWidgetPath.toFile(), AllureReportSummary.class);
        summary.setUrl(getAllureReportUrl());

        final String json = mapper.writeValueAsString(summary);
        Files.write(summaryOutputPath, json.getBytes(UTF_8));

        artifactsWatcher.addNewArtifactsPath(summaryOutputPath + "=>" + ALLURE_ARTIFACT_META_LOCATION);
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
            copyHistoryFormLastFinishedBuild(new URL(getLastFinishedArtifactUrl(ALLURE_ARTIFACT_HISTORY_LOCATION)));
            copyHistoryFormLastFinishedBuild(new URL(getLastFinishedArtifactUrl()));
        } catch (IOException e) {
            getLogger().message("Cannot copy history file. Reason: " + e.getMessage());
            getLogger().exception(e);
        }
    }

    private void copyHistoryFormLastFinishedBuild(final URL url) throws IOException {
        getLogger().message(format("Search allure history information in [%s] ...", url.toString()));

        final Path historyDirectory = resultsDirectory.resolve(HISTORY);
        Files.createDirectories(historyDirectory);

        try (Stream<Path> dirList = Files.list(historyDirectory)) {
            if (dirList.findAny().isPresent()) {
                getLogger().message("Allure history information already exists ...");
                return;
            }
        }

        final String password = getServerAuthentication();
        final String encoding = Base64.getUrlEncoder().encodeToString(password.getBytes(UTF_8));

        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", "Basic " + encoding);
        connection.connect();

        final boolean isArtifactMissing = connection.getResponseCode() != 200;
        if (isArtifactMissing) {
            getLogger().message(format("Allure history information missing in [%s] ...", url));
            return;
        }

        getLogger().message(format("Coping allure history information from [%s] ...", url));

        final Path lastFinishedArtifactZip = Files.createTempFile("artifact", String.valueOf(getBuild().getBuildId()));
        try (InputStream stream = connection.getInputStream()) {
            Files.copy(stream, lastFinishedArtifactZip, StandardCopyOption.REPLACE_EXISTING);
            try (ZipFile archive = new ZipFile(lastFinishedArtifactZip.toString())) {
                copyHistoryToResultsPath(archive, resultsDirectory);
            }
        }
    }

    private void copyHistoryToResultsPath(final ZipFile archive,
                                          final Path resultsDirectory) throws IOException {
        for (final ZipEntry historyEntry : listEntries(archive, HISTORY)) {
            final String historyFile = historyEntry.getName();

            if (historyEntry.isDirectory()) {
                Files.createDirectories(resultsDirectory.resolve(historyFile));
            } else {
                try (InputStream entryStream = archive.getInputStream(historyEntry)) {
                    Files.copy(entryStream, resultsDirectory.resolve(historyFile));
                }
            }
        }
    }

    /**
     * Write the test executor info to results directory.
     */
    private void addExecutorInfo() throws IOException {
        final String rootUrl = getTeamcityBaseUrl();
        final String buildUrl = getBuildUrl();
        final String buildNumber = getBuildNumber();
        final String reportUrl = getAllureReportUrl();

        final String buildName = format("%s / %s # %s",
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
        final StringBuilder artifactUrl = new StringBuilder();
        artifactUrl.append(format(
                "%srepository/downloadAll/%s/.lastFinished/artifacts.zip",
                getTeamcityBaseUrl(),
                getBuild().getBuildTypeExternalId()));
        final String branch = getConfigParameters().get(TEAMCITY_BUILD_BRANCH);
        if (Objects.nonNull(branch)) {
            final String encodedBranch = encodeValue(branch);
            artifactUrl.append(getBranchUrlParam(encodedBranch));
        }
        return artifactUrl.toString();
    }

    private String getLastFinishedArtifactUrl(final String name) {
        return getArtifactUrl(".lastFinished", name);
    }

    private String getArtifactUrl(final String build, final String name) {
        final StringBuilder artifactUrl = new StringBuilder();
        artifactUrl.append(format("%srepository/download/%s/%s/%s",
                getTeamcityBaseUrl(), getBuild().getBuildTypeExternalId(), build, name));
        final String branch = getConfigParameters().get(TEAMCITY_BUILD_BRANCH);
        if (Objects.nonNull(branch)) {
            final String encodedBranch = encodeValue(branch);
            artifactUrl.append(getBranchUrlParam(encodedBranch));
        }
        return artifactUrl.toString();
    }

    @NotNull
    private String getBranchUrlParam(final String encodedBranch) {
        return format("?branch=%s", encodedBranch);
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
    private String getAllureReportUrl() {
        final String reportDirectoryName = reportDirectory.toFile().getName();
        final String artifactPath = publishMode.equals(AllurePublishMode.ARCHIVE)
                ? format("%s!/%s/index.html", ARCHIVE_NAME, reportDirectoryName)
                : format("%s/index.html", reportDirectoryName);
        return getArtifactUrl(format("%s:id", getBuild().getBuildId()), artifactPath);
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
        final String baseUrl = getConfigParameters().get("teamcity.serverUrl");
        return endsWith(baseUrl, SLASH)
                ? baseUrl
                : StringUtils.join(baseUrl, SLASH);
    }

    private List<String> getProgramArgs() {
        final List<String> list = new ArrayList<>();

        list.add("generate");
        list.add(resultsDirectory.toString());
        list.add("-o");
        list.add(reportDirectory.toString());

        return list;
    }

    private String getProgramPath() throws RunBuildException {


        final String path = clientDirectory.toAbsolutePath().toString();
        final String executableName = getExecutableName();

        final String executableFile = path + File.separatorChar + "bin" + File.separatorChar + executableName;
        final File file = new File(executableFile);

        if (!file.exists()) {
            throw new RunBuildException("Cannot find executable \'" + executableFile + "\'");
        }
        if (!file.setExecutable(true)) {
            getLogger().message("Cannot set file: " + executableFile + " executable.");
        }
        return file.getAbsolutePath();

    }

    private static Path getClientDirectory(final Path client) throws RunBuildException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(client, Files::isDirectory)) {
            for (Path path : stream) {
                if (path.getFileName().toString().startsWith(ALLURE_EXEC_NAME)) {
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

    @NotNull
    private Path getWorkingDirectoryPath() {
        return Paths.get(workingDirectory);
    }

    private Path getAgentTempDirectoryPath() {
        return getAgentTempDirectory().toPath();
    }

    private String encodeValue(final String value) {
        try {
            return URLEncoder.encode(value, UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            return value;
        }
    }
}
