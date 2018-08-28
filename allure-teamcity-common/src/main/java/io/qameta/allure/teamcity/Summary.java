package io.qameta.allure.teamcity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Summary {

    private String url;

    private Map<String, Integer> statistic;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, Integer> getStatistic() {
        return statistic;
    }

    public void setStatistic(Map<String, Integer> statistic) {
        this.statistic = statistic;
    }

    public String printStatistic() {
        final StringBuilder builder = new StringBuilder();
        builder.append(String.format("Tests passed: %s", count("passed").orElse(0)));
        count("broken").ifPresent(broken -> builder.append(String.format(", broken: %s", broken)));
        count("failed").ifPresent(broken -> builder.append(String.format(", failed: %s", broken)));
        count("skipped").ifPresent(broken -> builder.append(String.format(", skipped: %s", broken)));
        return builder.toString();
    }

    private Optional<Integer> count(String name) {
        return Optional.ofNullable(statistic.get(name))
                .filter(value -> value > 0);
    }

}
