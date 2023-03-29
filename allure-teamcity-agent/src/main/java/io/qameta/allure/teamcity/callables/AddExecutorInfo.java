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
package io.qameta.allure.teamcity.callables;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;

/**
 * Add executor info to reports.
 */
@RequiredArgsConstructor
public class AddExecutorInfo extends AbstractAddInfo {

    private static final String EXECUTOR_JSON = "executor.json";

    private final String url;

    private final String buildName;

    private final String buildUrl;

    private final String buildOrder;

    private final String reportUrl;

    @Override
    protected Object getData() {
        final HashMap<String, Object> data = new HashMap<>();
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
