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
    public FetchAvailableToolsResult fetchAvailable() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(AllureConstants.ALLURE_COMMANDLINE_MAVEN_METADATA_URL);
            NodeList versions = document.getElementsByTagName("version");
            final Collection<AllureDownloadableToolVersion> allureVersions = new ArrayList<>();
            for (int i = 0; i < versions.getLength(); i++) {
                Node versionNode = versions.item(i).getFirstChild();
                allureVersions.add(new AllureDownloadableToolVersion(
                        versionNode.getNodeValue()
                ));
            }
            return FetchAvailableToolsResult.createSuccessful(allureVersions);
        }
        catch (IOException | ParserConfigurationException | SAXException e) {
            return FetchAvailableToolsResult.createError("Failed to fetch available allure commandline versions " +
                    "from " + AllureConstants.ALLURE_COMMANDLINE_MAVEN_METADATA_URL, e);
        }
    }
}
