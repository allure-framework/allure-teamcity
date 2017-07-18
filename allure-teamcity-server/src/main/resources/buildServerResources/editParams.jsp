<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="forms" tagdir="/WEB-INF/tags/forms" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>

<tr id="allure.result.directory.container">
    <th><label for="allure.result.directory">Allure result directory:</label></th>
    <td>
        <props:textProperty name="allure.result.directory" className="longField"/>
        <span class="smallNote">
            Specify the directory with allure results relative from build directory. An example
            <strong>allure-results/</strong>
        </span>
    </td>
</tr>

<tr id="allure.report.path.prefix.container">
    <th><label for="allure.report.path.prefix">Report artifact subdirectory:</label></th>
    <td>
        <props:textProperty name="allure.report.path.prefix" className="longField"/>
        <span class="smallNote">
            The subdirectory (or subdirectories) to put generated report into. An example
             <strong>allure-report/</strong>
        </span>
    </td>
</tr>

<props:javaSettings/>