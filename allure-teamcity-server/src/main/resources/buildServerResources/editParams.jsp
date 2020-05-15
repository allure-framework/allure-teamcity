<%@ include file="/include.jsp" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="forms" tagdir="/WEB-INF/tags/forms" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="io.qameta.allure.teamcity.AllureConstants" %>
<%@ page import="io.qameta.allure.teamcity.AllurePublishMode" %>

<jsp:useBean id="constants" type="io.qameta.allure.teamcity.AllureConstants"
             beanName="io.qameta.allure.teamcity.AllureConstants"/>

<tr id="allure.result.directory.container">
    <th><label for="allure.result.directory">Result directory:</label></th>
    <td>
        <props:textProperty name="<%=AllureConstants.RESULTS_DIRECTORY%>" className="longField"/>
        <span class="smallNote">
            Specify the directory with allure results <b>relative</b> from build directory. An example
            <strong>build/allure-results</strong>
        </span>
    </td>
</tr>

<tr id="allure.report.path.prefix.container">
    <th><label for="allure.report.path.prefix">Report directory:</label></th>
    <td>
        <props:textProperty name="<%=AllureConstants.REPORT_PATH_PREFIX%>" className="longField"/>
        <span class="smallNote">
            The directory <b>relative</b> from build directory to put generated report into. An example
             <strong>allure-report</strong>
        </span>
    </td>
</tr>

<jsp:include page="/tools/editToolUsage.html?toolType=${constants.allureToolName}&versionParameterName=${constants.allureToolVersion}&class=longField"/>

<tr id="allure.report.mode">
    <th><label for="allure.report.path.prefix">Publish mode:</label></th>
    <td>
        <props:radioButtonProperty name="<%=AllureConstants.PUBLISH_MODE%>" value="<%=AllurePublishMode.ARCHIVE.toString()%>"/> Publish archived report (Recommended)
        <br>
        <props:radioButtonProperty name="<%=AllureConstants.PUBLISH_MODE%>" value="<%=AllurePublishMode.PLAIN.toString()%>"/> Publish plain report
        <span class="smallNote">
            Archived report published faster and takes less space but may be slower (to view)
            when you are using external storage (S3, for example)
        </span>
    </td>
</tr>

<props:javaSettings/>