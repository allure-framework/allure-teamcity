<%@ include file="/include.jsp" %><%@ page import="ru.yandex.qatools.allure.tc.Constants" %>
<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>

<tr id="<%=Constants.INPUT_DIRECTORY%>.container">
    <th><label for="<%=Constants.INPUT_DIRECTORY%>">Input files directory:</label></th>
    <td>
        <props:textProperty name="<%=Constants.INPUT_DIRECTORY%>"/>
    </td>
</tr>