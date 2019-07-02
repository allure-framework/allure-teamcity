package io.qameta.allure.teamcity;

import jetbrains.buildServer.tools.ToolType;
import jetbrains.buildServer.tools.ToolTypeAdapter;
import jetbrains.buildServer.tools.ToolVersion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * represent information about allure tool
 *
 * @author Maxim Zaytsev (maxim.zaytsev@jetbrains.com)
 * @since 2019.1
 */
public class AllureTool implements ToolVersion {

    @NotNull
    @Override
    public ToolType getType() {
        return TOOL_TYPE;
    }

    @NotNull
    @Override
    public String getVersion() {
        return AllureConstants.ALLURE_TOOL_NAME;
    }

    @NotNull
    @Override
    public String getId() {
        return AllureConstants.ALLURE_TOOL_NAME;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Allure CLI tool used by allure report plugin";
    }

    private static final ToolType TOOL_TYPE = new ToolTypeAdapter() {
        @NotNull
        @Override
        public String getType() {
            return "zip-archive";
        }

        @NotNull
        @Override
        public String getDisplayName() {
            return "Zip Archive";
        }

        @Nullable
        @Override
        public String getDescription() {
            return "Tools installed as zip archive";
        }

        @NotNull
        @Override
        public String getShortDisplayName() {
            return ".zip";
        }

        @Override
        public boolean isVersionBased() {
            return false;
        }
    };
}
