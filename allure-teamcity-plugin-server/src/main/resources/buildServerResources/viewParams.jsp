<%@ page import="ru.yandex.qatools.allure.AllureConstants" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="parameter">
    Result directory:<strong><props:displayValue name="<%=AllureConstants.RESULTS_DIRECTORY%>" emptyValue="none"/></strong>
</div>

<div class="parameter">
    Report version:<strong><props:displayValue name="<%=AllureConstants.REPORT_VERSION%>" emptyValue="none"/></strong>
</div>
<div class="parameter">
    Issue tracker pattern:<strong><props:displayValue name="<%=AllureConstants.ISSUE_TRACKER_PATTERN%>" emptyValue="none"/></strong>
</div>
<div class="parameter">
    TMS pattern:<strong><props:displayValue name="<%=AllureConstants.TMS_PATTERN%>" emptyValue="none"/></strong>
</div>