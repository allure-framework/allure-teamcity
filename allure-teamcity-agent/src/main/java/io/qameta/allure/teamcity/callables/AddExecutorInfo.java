package io.qameta.allure.teamcity.callables;

import java.util.HashMap;

/**
 * Add executor info to reports
 */
public class AddExecutorInfo extends AbstractAddInfo {

    private static final String EXECUTOR_JSON = "executor.json";

    private final String url;

    private final String buildUrl;

    private final String buildName;

    private final String buildOrder;

    private final String reportUrl;

    public AddExecutorInfo(String url, String buildName, String buildUrl, String buildOrder, String reportUrl) {
        this.url = url;
        this.buildUrl = buildUrl;
        this.buildName = buildName;
        this.buildOrder = buildOrder;
        this.reportUrl = reportUrl;
    }

    @Override
    protected Object getData() {
        HashMap<String, Object> data = new HashMap<>();
        data.put("name", "TeamCity");
        data.put("type", "teamcity");
        data.put("url", url);
        data.put("buildOrder", buildOrder);
        data.put("buildName", buildName);
        data.put("buildUrl", buildUrl);
        data.put("reportUrl", reportUrl);
        return data;
    }

    @Override
    protected String getFileName() {
        return EXECUTOR_JSON;
    }
}
