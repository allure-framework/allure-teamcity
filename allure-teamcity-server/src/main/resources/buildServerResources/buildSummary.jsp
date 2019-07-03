<%@ taglib prefix="util" uri="/WEB-INF/functions/util" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--@elvariable id="summary" type="io.qameta.allure.teamcity.AllureReportSummary"--%>
<%--@elvariable id="error" type="java.lang.String"--%>
<c:if test="${not empty summary}">
    <tr>
        <td class="st">Allure:</td>
        <td class="st">
            <a target="_blank" href="${util:escapeUrlForQuotes(summary.url)}">${summary.printStatistic()}</a>
        </td>
    </tr>
</c:if>
