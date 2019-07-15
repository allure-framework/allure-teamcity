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
public class AllureServerToolProvider extends ServerToolProviderAdapter {
    private AvailableToolsFetcher fetcher;

    public AllureServerToolProvider(AvailableToolsFetcher fetcher) {
        this.fetcher = fetcher;
    }

    static final ToolType ALLURE_TOOL_TYPE = new AllureToolType();

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
    public File fetchToolPackage(@NotNull ToolVersion toolVersion, @NotNull File targetDirectory) throws ToolException {
        String id = toolVersion.getId();
        DownloadableToolVersion tool = CollectionsUtil.findFirst(fetcher.fetchAvailable().getFetchedTools(), data -> data.getId().equals(id));
        if (tool == null) {
            throw new ToolException("Failed to fetch allure-commandline tool " + toolVersion + ".");
        }
        File allureCommandLine = new File(tool.getDestinationFileName());
        URLDownloader.download(tool.getDownloadUrl(), allureCommandLine);
        return allureCommandLine;
    }

    @Override
    public void unpackToolPackage(@NotNull File toolPackage, @NotNull File targetDirectory) throws ToolException {
        ArchiveUtil.unpackZip(toolPackage, "", targetDirectory);
    }

    @NotNull
    @Override
    public GetPackageVersionResult tryGetPackageVersion(@NotNull File toolPackage) {
        final String packageName = FilenameUtils.removeExtension(toolPackage.getName());
        Pattern pattern = Pattern.compile("allure-commandline-([\\d\\.]+)");

        Matcher matcher = pattern.matcher(packageName);
        if (!matcher.matches()) {
            return GetPackageVersionResult.error("Not allure-commandline");
        }
        final String toolId = matcher.group(1);
        if (StringUtil.isEmpty(toolId)) {
            return GetPackageVersionResult.error(String.format("Failed to determine allure-commandline version based on its package file name %s. Checked package %s", toolPackage.getName(), toolPackage.getAbsolutePath()));
        }
        return GetPackageVersionResult.version(new AllureDownloadableToolVersion(toolId));
    }

    @Nullable
    @Override
    public String getDefaultBundledVersionId() {
        return AllureConstants.ALLURE_DEFAULT_TOOL_VERSION_ID;
    }
}
