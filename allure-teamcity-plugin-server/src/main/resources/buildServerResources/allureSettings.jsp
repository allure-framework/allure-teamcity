<%@ include file="/include.jsp" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="ru.yandex.qatools.allure.teamcity.AllureReportConfig" %>
<%@ page import="ru.yandex.qatools.allure.teamcity.ReportBuildPolicy" %>
<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>

<tr id="<%=AllureReportConfig.RESULTS_PATTERN_KEY%>.container">
    <th><label for="<%=AllureReportConfig.RESULTS_PATTERN_KEY%>">Allure tests results mask:</label></th>
    <td>
        <props:textProperty name="<%=AllureReportConfig.RESULTS_PATTERN_KEY%>" className="longField"/>
        <span class="smallNote">
            Specify the path to Allure results directories  in the Ant glob syntax.
            <br/>
            You can specify multiple patterns of files separated by commas.
            <br/>
            For example: <q>**/allure-results</q>
        </span>
    </td>
</tr>
<tr id="<%=AllureReportConfig.REPORT_VERSION_KEY%>.container">
    <th><label for="<%=AllureReportConfig.REPORT_VERSION_KEY%>">Allure report version:</label></th>
    <td>
        <props:textProperty name="<%=AllureReportConfig.REPORT_VERSION_KEY%>" className="longField"/>
        <span class="smallNote">
            Specify Allure report version in maven version range specification
            <br>
            For example, fixed version: <q>1.4.0</q> or all new versions:  <q>[1.3.0, )</q>
        </span>
    </td>
</tr>

<tr id="<%=AllureReportConfig.REPORT_BUILD_POLICY_KEY%>.container">
    <th><label for="<%=AllureReportConfig.REPORT_BUILD_POLICY_KEY%>">Allure report build policy:</label></th>
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
