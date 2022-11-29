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

import jetbrains.buildServer.tools.ToolType;
import jetbrains.buildServer.tools.available.DownloadableToolVersion;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import static io.qameta.allure.teamcity.AllureConstants.ALLURE_COMMANDLINE_MAVEN_FILENAME;
import static io.qameta.allure.teamcity.AllureConstants.ALLURE_COMMANDLINE_MAVEN_ZIP_URL;

/**
 * @author Sergey Khomutinin skhomuti@gmail.com
 *         Date: 14.07.19
 */
@RequiredArgsConstructor
public class AllureDownloadableToolVersion implements DownloadableToolVersion {

    private final String version;

    @NotNull
    @Override
    public String getDestinationFileName() {
        return String.format(ALLURE_COMMANDLINE_MAVEN_FILENAME, version);
    }

    @NotNull
    @Override
    public String getDownloadUrl() {
        return String.format(ALLURE_COMMANDLINE_MAVEN_ZIP_URL, version);
    }

    @NotNull
    @Override
    public ToolType getType() {
        return AllureToolProvider.ALLURE_TOOL_TYPE;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return getId();
    }

    @NotNull
    @Override
    public String getId() {
        return "allure-" + version;
    }

    @NotNull
    @Override
    public String getVersion() {
        return version;
    }
}
