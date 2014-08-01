<%@ include file="/include.jsp" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="ru.yandex.qatools.allure.teamcity.Parameters" %>
<%@ page import="ru.yandex.qatools.allure.teamcity.ReportBuildPolicy" %>
<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>

<tr id="<%=Parameters.RESULTS_MASK%>.container">
    <th><label for="<%=Parameters.RESULTS_MASK%>">Allure Results Directories:</label></th>
    <td>
        <props:textProperty name="<%=Parameters.RESULTS_MASK%>" className="longField"/>
        <span class="smallNote">
            Specify the path to Allure results directories using <a href="https://ant.apache.org/manual/dirtasks.html">Ant glob syntax</a>.
            <br/>
            You can specify multiple patterns of files separated by commas.
            <br/>
            For example: <strong>**/allure-results</strong>
        </span>
    </td>
</tr>
<tr id="<%=Parameters.REPORT_VERSION%>.container">
    <th><label for="<%=Parameters.REPORT_VERSION%>">Allure Version:</label></th>
    <td>
        <props:textProperty name="<%=Parameters.REPORT_VERSION%>" className="longField"/>
        <span class="smallNote">
            Specify Allure report version using <a href="http://maven.apache.org/enforcer/enforcer-rules/versionRanges.html">Maven version range specification</a>.
            <br>
            E.g. fixed version: <strong>1.4.0</strong> or versions newer than 1.3.0: <strong>[1.3.0, )</strong>
        </span>
    </td>
</tr>
<tr id="<%=Parameters.REPORT_BUILD_POLICY%>.container">
    <th><label for="<%=Parameters.REPORT_BUILD_POLICY%>">Build Allure:</label></th>
    <td>
        <div>
            <props:radioButtonProperty name="<%=Parameters.REPORT_BUILD_POLICY%>"
                                       value="<%=ReportBuildPolicy.ALWAYS.toString()%>"
                                       id="<%=Parameters.REPORT_BUILD_POLICY + ReportBuildPolicy.ALWAYS.toString()%>"
                                       checked="<%=propertiesBean.getEncryptedPropertyValue(Parameters.REPORT_BUILD_POLICY).equals(ReportBuildPolicy.ALWAYS.toString())%>"/>
            <label for="<%=Parameters.REPORT_BUILD_POLICY + ReportBuildPolicy.ALWAYS.toString()%>">
                For all builds
            </label>
        </div>

        <div>
            <props:radioButtonProperty name="<%=Parameters.REPORT_BUILD_POLICY%>"
                                       value="<%=ReportBuildPolicy.TEST_FAILED.toString()%>"
                                       id="<%=Parameters.REPORT_BUILD_POLICY + ReportBuildPolicy.TEST_FAILED.toString()%>"
                                       checked="<%=propertiesBean.getEncryptedPropertyValue(Parameters.REPORT_BUILD_POLICY).equals(ReportBuildPolicy.TEST_FAILED.toString())%>"/>
            <label for="<%=Parameters.REPORT_BUILD_POLICY + ReportBuildPolicy.TEST_FAILED.toString()%>">
                For builds with failed tests
            </label>
        </div>

        <div>
            <props:radioButtonProperty name="<%=Parameters.REPORT_BUILD_POLICY%>"
                                       value="<%=ReportBuildPolicy.BUILD_FAILED.toString()%>"
                                       id="<%=Parameters.REPORT_BUILD_POLICY + ReportBuildPolicy.BUILD_FAILED.toString()%>"
                                       checked="<%=propertiesBean.getEncryptedPropertyValue(Parameters.REPORT_BUILD_POLICY).equals(ReportBuildPolicy.BUILD_FAILED.toString())%>"/>
            <label for="<%=Parameters.REPORT_BUILD_POLICY + ReportBuildPolicy.BUILD_FAILED.toString()%>">
                For all unsuccessful builds
            </label>
        </div>
</tr>
