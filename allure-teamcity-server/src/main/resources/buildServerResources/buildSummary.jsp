<%@ taglib prefix="util" uri="/WEB-INF/functions/util" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--@elvariable id="allure_summary" type="io.qameta.allure.teamcity.AllureReportSummary"--%>
<%--@elvariable id="allure_error" type="java.lang.String"--%>
<c:if test="${not empty allure_summary}">
    <tr>
        <td class="st">Allure:</td>
        <td class="st">
            <a target="_blank" href="${util:escapeUrlForQuotes(allure_summary.url)}">
                    ${allure_summary.printStatistic()}
            </a>
        </td>
    </tr>
</c:if>
