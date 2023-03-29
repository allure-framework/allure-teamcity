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

import jetbrains.buildServer.tools.GetPackageVersionResult;
import jetbrains.buildServer.tools.ServerToolProviderAdapter;
import jetbrains.buildServer.tools.ToolException;
import jetbrains.buildServer.tools.ToolType;
import jetbrains.buildServer.tools.ToolVersion;
import jetbrains.buildServer.tools.available.AvailableToolsFetcher;
import jetbrains.buildServer.tools.available.DownloadableToolVersion;
import jetbrains.buildServer.tools.utils.URLDownloader;
import jetbrains.buildServer.util.ArchiveUtil;
import jetbrains.buildServer.util.CollectionsUtil;
import jetbrains.buildServer.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Sergey Khomutinin skhomuti@gmail.com
 * Date: 14.07.2019
 */
@RequiredArgsConstructor
public class AllureToolProvider extends ServerToolProviderAdapter {

    protected static final ToolType ALLURE_TOOL_TYPE = new AllureToolType();

    private final AvailableToolsFetcher fetcher;

    @NotNull
    @Override
    public ToolType getType() {
        return ALLURE_TOOL_TYPE;
    }

    @NotNull
    @Override
    public Collection<? extends ToolVersion> getAvailableToolVersions() {
        return fetcher.fetchAvailable().getFetchedTools();
    }

    @NotNull
    @Override
    public File fetchToolPackage(final @NotNull ToolVersion toolVersion,
                                 final @NotNull File targetDirectory) throws ToolException {
        final String id = toolVersion.getId();
        final DownloadableToolVersion tool =
                CollectionsUtil.findFirst(fetcher.fetchAvailable().getFetchedTools(), data -> data.getId().equals(id));
        if (tool == null) {
            throw new ToolException("Failed to fetch allure-commandline tool " + toolVersion + ".");
        }
        final File allureCommandLine = new File(tool.getDestinationFileName());
        URLDownloader.download(tool.getDownloadUrl(), null, allureCommandLine, b -> { });
        return allureCommandLine;
    }

    @Override
    public void unpackToolPackage(final @NotNull File toolPackage,
                                  final @NotNull File targetDirectory) {
        ArchiveUtil.unpackZip(toolPackage, "", targetDirectory);
    }

    @NotNull
    @Override
    public GetPackageVersionResult tryGetPackageVersion(final @NotNull File toolPackage) {
        final String packageName = FilenameUtils.removeExtension(toolPackage.getName());
        final Pattern pattern = Pattern.compile("allure-commandline-(.+)");

        final Matcher matcher = pattern.matcher(packageName);
        if (!matcher.matches()) {
            return GetPackageVersionResult.error("Not allure-commandline");
        }
        final String toolId = matcher.group(1);
        if (StringUtil.isEmpty(toolId)) {
            return GetPackageVersionResult.error(String.format(
                    "Failed to determine allure-commandline version based on its package file name %s. "
                    + "Checked package %s", toolPackage.getName(), toolPackage.getAbsolutePath()));
        }
        return GetPackageVersionResult.version(new AllureDownloadableToolVersion(toolId));
    }

    @Nullable
    @Override
    public String getDefaultBundledVersionId() {
        return AllureConstants.ALLURE_DEFAULT_TOOL_VERSION;
    }
}
