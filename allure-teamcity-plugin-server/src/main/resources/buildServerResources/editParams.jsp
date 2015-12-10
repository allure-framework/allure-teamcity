<%@ page import="ru.yandex.qatools.allure.AllureConstants" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="forms" tagdir="/WEB-INF/tags/forms" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>

<tr id="<%=AllureConstants.RESULTS_DIRECTORY%>.container">
    <th><label for="<%=AllureConstants.RESULTS_DIRECTORY%>">Allure result directory:</label></th>
    <td>
        <props:textProperty name="<%=AllureConstants.RESULTS_DIRECTORY%>" className="longField"/>
        <span class="smallNote">
            Specify the directory with allure results relative from build directory. An example
            <strong>allure-results/</strong>
        </span>
    </td>
</tr>

<tr id="<%=AllureConstants.REPORT_PATH_PREFIX%>.container">
    <th><label for="<%=AllureConstants.REPORT_PATH_PREFIX%>">Report artifact subdirectory:</label></th>
    <td>
        <props:textProperty name="<%=AllureConstants.REPORT_PATH_PREFIX%>" className="longField"/>
        <span class="smallNote">
            The subdirectory (or subdirectories) to put generated report into. An example <strong>allure-report/</strong>
        </span>
    </td>
</tr>

<tr id="<%=AllureConstants.ISSUE_TRACKER_PATTERN%>.container">
    <th><label for="<%=AllureConstants.ISSUE_TRACKER_PATTERN%>">Issue tracker pattern:</label></th>
    <td>
        <props:textProperty name="<%=AllureConstants.ISSUE_TRACKER_PATTERN%>" className="longField"/>
        <span class="smallNote">
            Specify the issue tracker pattern. E.g. <strong>http://bugtracker.yourcompany.com/issues/%s</strong>
            For more information you can see <a href="https://github.com/allure-framework/allure-core/wiki/Issues">this wiki
            page.</a>
        </span>
    </td>
</tr>

<tr id="<%=AllureConstants.TMS_PATTERN%>.container">
    <th><label for="<%=AllureConstants.TMS_PATTERN%>">Test management pattern:</label></th>
    <td>
        <props:textProperty name="<%=AllureConstants.TMS_PATTERN%>" className="longField"/>
        <span class="smallNote">
            Specify the test management system pattern. E.g. <strong>http://tms.yourcompany.com/tests/%s</strong>
            For more information you can see <a href="https://github.com/allure-framework/allure-core/wiki/Test-Case-ID">this
            wiki page.</a>
        </span>
    </td>
</tr>

<props:javaSettings/>