<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="parameter">
    Result directory:<strong><props:displayValue name="allure.result.directory"
    emptyValue="none"/></strong>
</div>

<div class="parameter">
    Report artifact subdirectory:<strong><props:displayValue name="allure.report.path.prefix"
    emptyValue="none"/></strong>
</div>