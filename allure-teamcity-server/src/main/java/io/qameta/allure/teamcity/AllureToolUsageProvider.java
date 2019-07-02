package io.qameta.allure.teamcity;

import jetbrains.buildServer.serverSide.BuildPromotion;
import jetbrains.buildServer.serverSide.BuildPromotionEx;
import jetbrains.buildServer.serverSide.SRunningBuild;
import jetbrains.buildServer.serverSide.impl.SBuildStepDescriptor;
import jetbrains.buildServer.tools.ToolUsagesProvider;
import jetbrains.buildServer.tools.ToolVersion;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Provide requirement for allure tool if allure runner registered and enabled in the build
 *
 * @author Maxim Zaytsev (maxim.zaytsev@jetbrains.com)
 * @since 2019.1
 */
public class AllureToolUsageProvider implements ToolUsagesProvider {

    private final AllureTool allureTool;

    public AllureToolUsageProvider(AllureTool allureTool) {
        this.allureTool = allureTool;
    }

    @Override
    public List<ToolVersion> getRequiredTools(@NotNull SRunningBuild build) {
        for (SBuildStepDescriptor runner : getEnabledBuildRunners(build)) {
            if (AllureConstants.RUN_TYPE.equals(runner.getType())) {
                return Collections.singletonList(allureTool);
            }
        }
        return Collections.emptyList();
    }

    private Collection<? extends SBuildStepDescriptor> getEnabledBuildRunners(SRunningBuild build) {
        BuildPromotion bp = build.getBuildPromotion();
        if (!(bp instanceof BuildPromotionEx)) {
            return Collections.emptyList();
        }

        BuildPromotionEx buildPromotion = (BuildPromotionEx) bp;

        return buildPromotion.getBuildSettings().getAllBuildRunners().getEnabledBuildRunners();
    }
}
