<%@ include file="/include.jsp" %>
<%@ page import="ru.yandex.qatools.allure.teamcity.Parameters" %>

<%
    final String buildTypeId = request.getParameter(Parameters.BUILD_TYPE_ID);
    final String buildNumber = request.getParameter(Parameters.BUILD_ID);
%>
<table>
    <tr>
        <td>Allure Report:</td>
        <td><a href="<%="/repository/download/" + buildTypeId + "/" + buildNumber + ":id/allure"%>">open</a></td>
    </tr>
</table>
