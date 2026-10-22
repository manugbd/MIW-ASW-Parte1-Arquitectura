<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Amazin - Seleccionar usuario</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<header>
    <h1 class="header">Amazin.com</h1>
    <h2 class="centered">Seleccionar usuario</h2>
</header>
<section>
    <article>
        <c:if test="${requestScope.error}">
            <p role="alert" style="color: #a00000">
                <c:out value="${requestScope.errorMessage}"/>
            </p>
        </c:if>
        <c:if test="${requestScope.loginSuccess}">
            <p>Usuario seleccionado correctamente.</p>
        </c:if>
        <p>Usuario actual:
            <strong>
                <c:out value="${sessionScope.username}" default="Sin identificar"/>
            </strong>
        </p>
        <form method="post" action="${pageContext.request.contextPath}/Controller">
            <input type="hidden" name="action" value="LoginAction">
            <label for="username">Usuario:</label>
            <select id="username" name="username">
                <c:forEach var="user" items="${requestScope.users}">
                    <option value="<c:out value='${user}'/>"
                        ${user eq requestScope.selectedUser ? 'selected' : ''}>
                        <c:out value="${user}"/>
                    </option>
                </c:forEach>
            </select>
            <button type="submit">Usar este usuario</button>
        </form>
        <p><a href="${pageContext.request.contextPath}/Controller?action=ShowBooksAction">
            Consultar libros
        </a></p>
        <p><a href="${pageContext.request.contextPath}/Controller?action=ShowSpecialOfferAction">
            Consultar oferta del dia
        </a></p>
        <p><a href="${pageContext.request.contextPath}/index.html">Inicio</a></p>
    </article>
</section>
</body>
</html>