package io.qameta.allure.teamcity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

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
        return String.format("Tests passed: %s, broken: %s, failed: %s, skipped: %s",
                statistic.get("passed"),
                statistic.get("broken"),
                statistic.get("failed"),
                statistic.get("skipped"));
    }

}
