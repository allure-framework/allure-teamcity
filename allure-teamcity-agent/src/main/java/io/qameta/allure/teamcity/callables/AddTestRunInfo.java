package io.qameta.allure.teamcity.callables;

import java.util.HashMap;

/**
 * Add testRun info to reports
 */
public class AddTestRunInfo extends AbstractAddInfo {

    public static final String TESTRUN_JSON = "testrun.json";

    private final String name;

    private final long start;

    private final long stop;

    public AddTestRunInfo(String name, long start, long stop) {
        this.name = name;
        this.start = start;
        this.stop = stop;
    }

    @Override
    protected Object getData() {
        HashMap<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("start", start);
        data.put("stop", stop);
        return data;
    }

    @Override
    protected String getFileName() {
        return TESTRUN_JSON;
    }
}
