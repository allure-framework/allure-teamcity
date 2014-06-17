<%@ include file="/include.jsp" %>
<%@ page import="ru.yandex.qatools.allure.teamcity.Parameters" %>
<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>

<tr id="<%=Parameters.RESULTS_MASK%>.container">
    <th><label for="<%=Parameters.RESULTS_MASK%>">Allure tests results mask:</label></th>
    <td>
        <props:textProperty name="<%=Parameters.RESULTS_MASK%>"/>
    </td>
</tr>
<tr id="<%=Parameters.REPORT_VERSION%>.container">
    <th><label for="<%=Parameters.REPORT_VERSION%>">Allure report version:</label></th>
    <td>
        <props:textProperty name="<%=Parameters.REPORT_VERSION%>"/>
    </td>
</tr>