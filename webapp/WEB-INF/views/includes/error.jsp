<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${not empty error}">
    <div class="alert alert-danger">
        <c:out value="${error}"/>
    </div>
</c:if>
<c:if test="${not empty message}">
    <div class="alert alert-info">
        <c:out value="${message}"/>
    </div>
</c:if>
<c:if test="${not empty success}">
    <div class="alert alert-success">
        <c:out value="${success}"/>
    </div>
</c:if>
<c:if test="${not empty errors}">
    <div class="alert alert-warning">
        <ul>
            <c:forEach items="${errors}" var="error">
                <li><c:out value="${error}"/></li>
            </c:forEach>
        </ul>
    </div>
</c:if>
