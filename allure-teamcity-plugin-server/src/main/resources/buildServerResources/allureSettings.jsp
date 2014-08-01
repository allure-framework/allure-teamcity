<%@ include file="/include.jsp" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="ru.yandex.qatools.allure.teamcity.AllureReportConfig" %>
<%@ page import="ru.yandex.qatools.allure.teamcity.ReportBuildPolicy" %>
<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>

<tr id="<%=AllureReportConfig.RESULTS_PATTERN_KEY%>.container">
    <th><label for="<%=AllureReportConfig.RESULTS_PATTERN_KEY%>">Results Directories:</label></th>
    <td>
        <props:textProperty name="<%=AllureReportConfig.RESULTS_PATTERN_KEY%>" className="longField"/>
        <span class="smallNote">
            Specify the path to Allure results directories using <a href="https://ant.apache.org/manual/dirtasks.html">Ant glob syntax</a>.
            <br/>
            You can specify multiple patterns of files separated by commas.
            <br/>
            E.g. <strong>**/allure-results</strong>
        </span>
    </td>
</tr>
<tr id="<%=AllureReportConfig.REPORT_VERSION_KEY%>.container">
    <th><label for="<%=AllureReportConfig.REPORT_VERSION_KEY%>">Report Version:</label></th>
    <td>
        <props:textProperty name="<%=AllureReportConfig.REPORT_VERSION_KEY%>" className="longField"/>
        <span class="smallNote">
            Specify Allure report version using <a href="http://maven.apache.org/enforcer/enforcer-rules/versionRanges.html">Maven version range specification</a>.
            <br>
            E.g. fixed version: <strong>1.4.0</strong> or all new versions:  <strong>[1.3.0, )</strong>
        </span>
    </td>
</tr>

<tr id="<%=AllureReportConfig.REPORT_BUILD_POLICY_KEY%>.container">
    <th><label for="<%=AllureReportConfig.REPORT_BUILD_POLICY_KEY%>">Generate:</label></th>
    <td>
        <div>
            <props:radioButtonProperty name="<%=AllureReportConfig.REPORT_BUILD_POLICY_KEY%>"
                                       value="<%=ReportBuildPolicy.ALWAYS.name()%>"
                                       id="<%=AllureReportConfig.REPORT_BUILD_POLICY_KEY + ReportBuildPolicy.ALWAYS.name()%>"
                                       checked="<%=propertiesBean.getEncryptedPropertyValue(AllureReportConfig.REPORT_BUILD_POLICY_KEY).equals(ReportBuildPolicy.ALWAYS.name())%>"/>
            <label for="<%=AllureReportConfig.REPORT_BUILD_POLICY_KEY + ReportBuildPolicy.ALWAYS.name()%>">
                <%=ReportBuildPolicy.ALWAYS.title()%>
            </label>
        </div>

        <div>
            <props:radioButtonProperty name="<%=AllureReportConfig.REPORT_BUILD_POLICY_KEY%>"
                                       value="<%=ReportBuildPolicy.WITH_PROBLEMS.name()%>"
                                       id="<%=AllureReportConfig.REPORT_BUILD_POLICY_KEY + ReportBuildPolicy.WITH_PROBLEMS.name()%>"
                                       checked="<%=propertiesBean.getEncryptedPropertyValue(AllureReportConfig.REPORT_BUILD_POLICY_KEY).equals(ReportBuildPolicy.WITH_PROBLEMS.name())%>"/>
            <label for="<%=AllureReportConfig.REPORT_BUILD_POLICY_KEY + ReportBuildPolicy.WITH_PROBLEMS.name()%>">
                <%=ReportBuildPolicy.WITH_PROBLEMS.title()%>
            </label>
        </div>

        <div>
            <props:radioButtonProperty name="<%=AllureReportConfig.REPORT_BUILD_POLICY_KEY%>"
                                       value="<%=ReportBuildPolicy.FAILED.name()%>"
                                       id="<%=AllureReportConfig.REPORT_BUILD_POLICY_KEY + ReportBuildPolicy.FAILED.name()%>"
                                       checked="<%=propertiesBean.getEncryptedPropertyValue(AllureReportConfig.REPORT_BUILD_POLICY_KEY).equals(ReportBuildPolicy.FAILED.name())%>"/>
            <label for="<%=AllureReportConfig.REPORT_BUILD_POLICY_KEY + ReportBuildPolicy.FAILED.name()
            %>">
                <%=ReportBuildPolicy.FAILED.title()%>
            </label>
        </div>
</tr>
