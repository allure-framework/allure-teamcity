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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static java.lang.String.format;

@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AllureReportSummary {

    private String url;

    private Map<String, Integer> statistic;

    public String printStatistic() {
        final StringBuilder builder = new StringBuilder();
        if (Objects.nonNull(statistic)) {
            builder.append(format("Tests passed: %s", count("passed").orElse(0)));
            count("broken").ifPresent(broken -> builder.append(format(", broken: %s", broken)));
            count("failed").ifPresent(broken -> builder.append(format(", failed: %s", broken)));
            count("skipped").ifPresent(broken -> builder.append(format(", skipped: %s", broken)));
            return builder.toString();
        } else {
            return "open report";
        }
    }

    private Optional<Integer> count(final String name) {
        return Optional.ofNullable(statistic.get(name))
                .filter(value -> value > 0);
    }

}
