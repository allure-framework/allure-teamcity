package ru.yandex.qatools.allure;

import jetbrains.buildServer.serverSide.InvalidProperty;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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

        validateNotEmpty(properties, AllureConstants.RESULTS_DIRECTORY, problems);
        validateNotEmpty(properties, AllureConstants.TMS_PATTERN, problems);
        validateNotEmpty(properties, AllureConstants.ISSUE_TRACKER_PATTERN, problems);

        validatePattern(properties, AllureConstants.TMS_PATTERN, problems);
        validatePattern(properties, AllureConstants.ISSUE_TRACKER_PATTERN, problems);

        return problems;
    }

    protected void validateNotEmpty(Map<String, String> properties, String key, List<InvalidProperty> problems) {
        String value = properties.get(key);
        if (StringUtils.isEmpty(value)) {
            problems.add(new InvalidProperty(key, "The property value should not be empty"));
        }
    }

    /**
     * Validate the value of the property with given key. The validated value should contains
     * exactly one placeholder <code>%s</code>.
     *
     * @param properties the properties to find value by key.
     * @param key        the key to find property value.
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
