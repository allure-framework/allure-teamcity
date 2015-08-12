package ru.yandex.qatools.allure;

import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.serverSide.RunType;
import jetbrains.buildServer.serverSide.RunTypeRegistry;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dmitry Baev charlie@yandex-team.ru
 *         Date: 06.08.15
 */
public class AllureRunType extends RunType {

    private final PluginDescriptor pluginDescriptor;

    public AllureRunType(
            @NotNull final RunTypeRegistry registry,
            @NotNull final PluginDescriptor descriptor) {
        this.pluginDescriptor = descriptor;
        registry.registerRunType(this);
    }

    @NotNull
    @Override
    public String getType() {
        return AllureConstants.RUN_TYPE;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Allure Report";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Generate the Allure report for your build.";
    }

    @Nullable
    @Override
    public PropertiesProcessor getRunnerPropertiesProcessor() {
        return new AllurePropertiesProcessor();
    }

    @Nullable
    @Override
    public String getEditRunnerParamsJspFilePath() {
        return pluginDescriptor.getPluginResourcesPath("editParams.jsp");
    }

    @Nullable
    @Override
    public String getViewRunnerParamsJspFilePath() {
        return pluginDescriptor.getPluginResourcesPath("viewParams.jsp");
    }

    @Nullable
    @Override
    public Map<String, String> getDefaultRunnerProperties() {
        return new HashMap<>();
    }

    public String getPluginsDirectory() {
        return pluginDescriptor.getPluginRoot().getParent();
    }

    public List<String> installedVersions() throws IOException {
        List<String> result = new ArrayList<>();
        Path path = Paths.get(pluginDescriptor.getPluginRoot().getParent(), ".tools", "allure");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path child : stream) {
                result.add(child.getFileName().toString());
            }
        }
        return result;
    }
}
