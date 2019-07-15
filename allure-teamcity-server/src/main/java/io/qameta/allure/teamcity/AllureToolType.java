package io.qameta.allure.teamcity;

import jetbrains.buildServer.tools.ToolTypeAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static io.qameta.allure.teamcity.AllureConstants.ALLURE_TOOL_NAME;

/**
 * @author Sergey Khomutinin skhomuti@gmail.com
 * Date: 14.07.2019
 */
public class AllureToolType extends ToolTypeAdapter {

    @NotNull
    @Override
    public String getType() {
        return ALLURE_TOOL_NAME;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Allure Plugin";
    }

    @NotNull
    @Override
    public String getShortDisplayName() {
        return getDisplayName();
    }

    @Nullable
    @Override
    public String getDescription() {
        return "Allure commandline";
    }

    @Override
    public boolean isSupportUpload() {
        return true;
    }

    @Override
    public boolean isSupportDownload() {
        return true;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    @Override
    public boolean isServerOnly() {
        return false;
    }

    @Override
    public boolean isCountUsages() {
        return false;
    }

}

