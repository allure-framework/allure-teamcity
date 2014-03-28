<%@ include file="/include.jsp" %>
<%@ page import="ru.yandex.qatools.allure.tc.Constants" %>

<%
    final String buildTypeId = request.getParameter(Constants.BUILD_TYPE_ID);
    final String buildNumber = request.getParameter(Constants.BUILD_ID);
%>
<table>
    <tr>
        <td>Report:</td>
        <td><a href="<%="/repository/download/" + buildTypeId + "/" + buildNumber + ":id/allure"%>">open</a></td>
    </tr>
</table>
