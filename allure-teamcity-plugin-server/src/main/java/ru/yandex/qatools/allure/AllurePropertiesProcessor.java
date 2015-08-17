package ru.yandex.qatools.allure;

import jetbrains.buildServer.serverSide.InvalidProperty;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static ru.yandex.qatools.allure.AllureConstants.ISSUE_TRACKER_PATTERN;
import static ru.yandex.qatools.allure.AllureConstants.RESULTS_DIRECTORY;
import static ru.yandex.qatools.allure.AllureConstants.TMS_PATTERN;

/**
 * @author Dmitry Baev charlie@yandex-team.ru
 *         Date: 06.08.15
 */
public class AllurePropertiesProcessor implements PropertiesProcessor {

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<InvalidProperty> process(Map<String, String> properties) {
        List<InvalidProperty> problems = new ArrayList<>();

        validateNotEmpty(properties, RESULTS_DIRECTORY, problems);
        validateNotEmpty(properties, TMS_PATTERN, problems);
        validateNotEmpty(properties, ISSUE_TRACKER_PATTERN, problems);

        validateRelative(properties, RESULTS_DIRECTORY, problems);

        validatePattern(properties, TMS_PATTERN, problems);
        validatePattern(properties, ISSUE_TRACKER_PATTERN, problems);

        return problems;
    }

    /**
     * Validate the property with given key are exists and not empty.
     *
     * @param properties the properties map to find the validated property by key.
     * @param key        the key of property to validate.
     * @param problems   the list of problems to add problem if needed.
     */
    protected void validateNotEmpty(Map<String, String> properties, String key, List<InvalidProperty> problems) {
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
    protected void validateRelative(Map<String, String> properties, String key, List<InvalidProperty> problems) {
        String value = properties.get(key);
        if (StringUtils.isEmpty(value) || Paths.get(value).isAbsolute()) {
            problems.add(new InvalidProperty(key, "The path should be relative from checkout directory"));
        }
    }

    /**
     * Validate the value of the property with given key. The validated value should contains
     * exactly one placeholder <code>%s</code>.
     *
     * @param properties the properties map to find the validated property by key.
     * @param key        the key of property to validate.
     * @param problems   the list of problems to add problem if needed.
     */
    protected void validatePattern(Map<String, String> properties, String key, List<InvalidProperty> problems) {
        String value = properties.get(key);
        int matches = StringUtils.countMatches(value, "%s");
        if (matches != 1) {
            problems.add(new InvalidProperty(key, "The pattern should " +
                    "contains exactly one placeholder <%s>"));
        }
    }

}
