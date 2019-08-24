package io.qameta.allure.teamcity;

import jetbrains.buildServer.tools.ToolType;
import jetbrains.buildServer.tools.available.DownloadableToolVersion;
import org.jetbrains.annotations.NotNull;

import static io.qameta.allure.teamcity.AllureConstants.ALLURE_COMMANDLINE_MAVEN_FILENAME;
import static io.qameta.allure.teamcity.AllureConstants.ALLURE_COMMANDLINE_MAVEN_ZIP_URL;

/**
 * @author Sergey Khomutinin skhomuti@gmail.com
 *         Date: 14.07.19
 */
public class AllureDownloadableToolVersion implements DownloadableToolVersion {
    private String version;

    AllureDownloadableToolVersion(@NotNull String version) {
        this.version = version;
    }

    @NotNull
    @Override
    public String getDestinationFileName() {
        return String.format(ALLURE_COMMANDLINE_MAVEN_FILENAME, version);
    }

    @NotNull
    @Override
    public String getDownloadUrl() {
        return String.format(ALLURE_COMMANDLINE_MAVEN_ZIP_URL, version);
    }

    @NotNull
    @Override
    public ToolType getType() {
        return AllureToolProvider.ALLURE_TOOL_TYPE;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return getId();
    }

    @NotNull
    @Override
    public String getId() {
        return "allure-" + version;
    }

    @NotNull
    @Override
    public String getVersion() {
        return version;
    }
}
