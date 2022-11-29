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

import com.fasterxml.jackson.databind.json.JsonMapper;
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
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Objects;

public class AllureReportBuildSummaryExtension extends SimplePageExtension {

    private final SBuildServer server;

    public AllureReportBuildSummaryExtension(@NotNull final SBuildServer server,
                                             @NotNull final WebControllerManager manager,
                                             @NotNull final PluginDescriptor pluginDescriptor) {
        super(manager, PlaceId.BUILD_SUMMARY, pluginDescriptor.getPluginName(), "buildSummary.jsp");
        this.server = server;
        register();
    }

    @Override
    public boolean isAvailable(@NotNull final HttpServletRequest request) {
        final SBuild build = BuildDataExtensionUtil.retrieveBuild(request, server);
        if (Objects.isNull(build)) {
            return false;
        }
        final BuildArtifact artifact = build.getArtifacts(BuildArtifactsViewMode.VIEW_HIDDEN_ONLY)
                .getArtifact(AllureConstants.ALLURE_ARTIFACT_SUMMARY_LOCATION);
        return Objects.nonNull(artifact);
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
            final AllureReportSummary summary = readSummary(artifact);
            model.put("allure_summary", summary);
        } catch (IOException e) {
            model.put("allure_error", e.getMessage());
        }
    }

    private static AllureReportSummary readSummary(final BuildArtifact artifact) throws IOException {
        try (InputStream inputStream = artifact.getInputStream()) {
            final String summaryJson = IOUtils.toString(inputStream, Charset.defaultCharset());
            final JsonMapper mapper = JsonMapper.builder().build();
            return mapper.readValue(summaryJson, AllureReportSummary.class);
        }
    }


}
