/*
 *  Copyright 2016-2023 Qameta Software OÜ
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.qameta.allure.teamcity;

import jetbrains.buildServer.serverSide.InvalidProperty;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static io.qameta.allure.teamcity.AllureConstants.REPORT_PATH_PREFIX;
import static io.qameta.allure.teamcity.AllureConstants.RESULTS_DIRECTORY;

/**
 * @author Dmitry Baev charlie@yandex-team.ru
 *         Date: 06.08.15
 */
class AllurePropertiesProcessor implements PropertiesProcessor {

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<InvalidProperty> process(final Map<String, String> properties) {
        final List<InvalidProperty> problems = new ArrayList<>();

        validateNotEmpty(properties, RESULTS_DIRECTORY, problems);
        validateNotEmpty(properties, REPORT_PATH_PREFIX, problems);

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
    private void validateNotEmpty(final Map<String, String> properties,
                                  final String key,
                                  final List<InvalidProperty> problems) {
        final String value = properties.get(key);
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
    private void validateRelative(final Map<String, String> properties,
                                  final String key,
                                  final List<InvalidProperty> problems) {
        final String value = properties.get(key);
        if (StringUtils.isEmpty(value) || Paths.get(value).isAbsolute()) {
            problems.add(new InvalidProperty(key, "The path should be relative"));
        }
    }
}
