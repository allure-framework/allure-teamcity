package io.qameta.allure.teamcity;

import jetbrains.buildServer.serverSide.BuildPromotion;
import jetbrains.buildServer.serverSide.BuildPromotionEx;
import jetbrains.buildServer.serverSide.SRunningBuild;
import jetbrains.buildServer.serverSide.impl.SBuildStepDescriptor;
import jetbrains.buildServer.tools.ServerToolManager;
import jetbrains.buildServer.tools.ToolUsagesProvider;
import jetbrains.buildServer.tools.ToolVersion;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class AllureToolUsageProvider implements ToolUsagesProvider {

    private final ServerToolManager toolManager;

    public AllureToolUsageProvider(final ServerToolManager toolManager) {
        this.toolManager = toolManager;
    }

    @Override
    public List<ToolVersion> getRequiredTools(@NotNull final SRunningBuild build) {
        final Optional<? extends SBuildStepDescriptor> allureStep = getEnabledBuildRunners(build).stream()
                .filter(runner -> AllureConstants.RUN_TYPE.equals(runner.getType()))
                .findFirst();
        if (allureStep.isPresent()) {
            return new ArrayList<>(toolManager.getInstalledTools(AllureToolProvider.ALLURE_TOOL_TYPE));
        }
        return Collections.emptyList();
    }

    private Collection<? extends SBuildStepDescriptor> getEnabledBuildRunners(final SRunningBuild build) {
        final BuildPromotion buildPromotion = build.getBuildPromotion();
        if (buildPromotion instanceof BuildPromotionEx) {
            return ((BuildPromotionEx) buildPromotion)
                    .getBuildSettings()
                    .getAllBuildRunners()
                    .getEnabledBuildRunners();
        }
        return Collections.emptyList();
    }

}
