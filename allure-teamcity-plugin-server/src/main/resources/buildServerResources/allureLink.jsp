<%@ include file="/include.jsp" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ page import="ru.yandex.qatools.allure.teamcity.AllureReportConfig" %>

<%
    final String buildTypeId = request.getParameter(AllureReportConfig.BUILD_TYPE_ID_KEY);
    final String buildNumber = request.getParameter(AllureReportConfig.BUILD_ID_KEY);
%>
<table>
    <tr>
        <td>Allure Report:</td>
        <td><a href="<%="/repository/download/" + buildTypeId + "/" + buildNumber + ":id/allure"%>">open</a></td>
    </tr>
</table>
