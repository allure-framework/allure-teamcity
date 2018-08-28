package io.qameta.allure.teamcity;

import jetbrains.buildServer.serverSide.InvalidProperty;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static io.qameta.allure.teamcity.AllureConstants.*;

/**
 * @author Dmitry Baev charlie@yandex-team.ru
 *         Date: 06.08.15
 */
class AllurePropertiesProcessor implements PropertiesProcessor {

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<InvalidProperty> process(Map<String, String> properties) {
        List<InvalidProperty> problems = new ArrayList<>();

        validateNotEmpty(properties, RESULTS_DIRECTORY, problems);
        validateNotEmpty(properties, REPORT_PATH_PREFIX, problems);
        validateNotEmpty(properties, PUBLISH_MODE, problems);

        validateRelative(properties, RESULTS_DIRECTORY, problems);
        validateRelative(properties, REPORT_PATH_PREFIX, problems);

        return problems;
    }

    /**
     * Validate the property with given key are exists and not empty.
     *
     * @param properties the properties map to find the validated property by key.
     * @param key        the key of property to validate.
     * @param problems   the list of problems to add problem if needed.
     */
    private void validateNotEmpty(Map<String, String> properties, String key, List<InvalidProperty> problems) {
        String value = properties.get(key);
        if (StringUtils.isEmpty(value)) {
            problems.add(new InvalidProperty(key, "The property value should not be empty"));
        }
    }

    /**
     * Validate the value of the property with given key. The validated value should be valid
     * relative path.
     *
     * @param properties the properties map to find the validated property by key.
     * @param key        the key of property to validate.
     * @param problems   the list of problems to add problem if needed.
     */
    private void validateRelative(Map<String, String> properties, String key, List<InvalidProperty> problems) {
        String value = properties.get(key);
        if (StringUtils.isEmpty(value) || Paths.get(value).isAbsolute()) {
            problems.add(new InvalidProperty(key, "The path should be relative"));
        }
    }
}
