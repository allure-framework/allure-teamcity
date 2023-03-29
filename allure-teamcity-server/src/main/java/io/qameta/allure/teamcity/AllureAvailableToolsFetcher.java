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

import jetbrains.buildServer.tools.available.AvailableToolsFetcher;
import jetbrains.buildServer.tools.available.FetchAvailableToolsResult;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Sergey Khomutinin skhomuti@gmail.com
 * Date: 14.07.2019
 */
public class AllureAvailableToolsFetcher implements AvailableToolsFetcher {

    @NotNull
    @Override
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public FetchAvailableToolsResult fetchAvailable() {
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder builder = factory.newDocumentBuilder();
            final Document document = builder.parse(AllureConstants.ALLURE_COMMANDLINE_MAVEN_METADATA_URL);
            final NodeList versions = document.getElementsByTagName("version");
            final Collection<AllureDownloadableToolVersion> allureVersions = new ArrayList<>();
            for (int i = 0; i < versions.getLength(); i++) {
                final Node versionNode = versions.item(i).getFirstChild();
                allureVersions.add(new AllureDownloadableToolVersion(
                        versionNode.getNodeValue()
                ));
            }
            return FetchAvailableToolsResult.createSuccessful(allureVersions);
        } catch (IOException | ParserConfigurationException | SAXException e) {
            return FetchAvailableToolsResult.createError(
                    "Failed to fetch available allure commandline versions from "
                            + AllureConstants.ALLURE_COMMANDLINE_MAVEN_METADATA_URL, e);
        }
    }
}
