package io.qameta.allure.teamcity;

import com.fasterxml.jackson.databind.ObjectMapper;
import jetbrains.buildServer.BuildProblemData;
import jetbrains.buildServer.BuildProblemTypes;
import jetbrains.buildServer.controllers.BuildDataExtensionUtil;
import jetbrains.buildServer.serverSide.SBuild;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.artifacts.BuildArtifact;
import jetbrains.buildServer.serverSide.artifacts.BuildArtifactsViewMode;
import jetbrains.buildServer.web.openapi.PlaceId;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.SimplePageExtension;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

public class AllureBuildSummaryExtension extends SimplePageExtension {

    private final SBuildServer server;

    public AllureBuildSummaryExtension(@NotNull final SBuildServer server,
                                       @NotNull final WebControllerManager manager,
                                       @NotNull final PluginDescriptor pluginDescriptor) {
        super(manager, PlaceId.BUILD_SUMMARY, pluginDescriptor.getPluginName(), "buildSummary.jsp");
        this.server = server;
        register();
    }

    @Override
    public void fillModel(@NotNull final Map<String, Object> model, @NotNull final HttpServletRequest request) {
        final SBuild build = BuildDataExtensionUtil.retrieveBuild(request, server);
        if (Objects.isNull(build)) {
            return;
        }
        final BuildArtifact artifact = build.getArtifacts(BuildArtifactsViewMode.VIEW_HIDDEN_ONLY)
                .getArtifact(AllureConstants.ALLURE_ARTIFACT_SUMMARY_LOCATION);
        if (Objects.isNull(artifact)) {
            return;
        }

        try {
            final Summary summary = readSummary(artifact);
            model.put("summary", summary);
        } catch (IOException e) {
            model.put("error", e.getMessage());
        }
    }

    private static Summary readSummary(BuildArtifact artifact) throws IOException {
        try (InputStream inputStream = artifact.getInputStream()) {
            String summaryJson = IOUtils.toString(inputStream);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(summaryJson, Summary.class);
        }
    }


}