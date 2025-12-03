<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Welcome</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .welcome-container {
            width: 600px;
            margin: 50px auto;
            background: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 0 12px rgba(0, 0, 0, 0.1);
        }

        .welcome-container h2 {
            text-align: center;
            margin-bottom: 10px;
        }

        .actions {
            text-align: center;
            margin-bottom: 20px;
        }

        .cocktail-list {
            list-style: none;
            padding: 0;
        }

        .cocktail-item {
            background: #f9f9f9;
            padding: 10px 15px;
            margin: 8px 0;
            border-radius: 6px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
        }

        .cocktail-item strong {
            display: block;
            font-size: 16px;
            margin-bottom: 5px;
        }

        .button {
            margin: 5px 0;
        }
    </style>
</head>
<body>

<div class="welcome-container">

    <h2>Welcome, ${currentUser.username}!</h2>
    <p style="text-align:center;">Your role: <strong>${currentUser.role}</strong></p>

    <div class="actions">

        <!-- CLIENT -->
        <c:if test="${currentUser.role == 'CLIENT'}">
            <form action="${pageContext.request.contextPath}/cocktail/add" method="get">
                <button class="button" type="submit">Offer a cocktail</button>
            </form>
            <form action="${pageContext.request.contextPath}/logout" method="post">
                <button class="button danger" type="submit">Log out</button>
            </form>

            <h3>Cocktail list</h3>
            <ul class="cocktail-list">
                <c:forEach var="cocktail" items="${cocktailList}">
                    <li class="cocktail-item">
                        <a href="${pageContext.request.contextPath}/cocktail/view?id=${cocktail.id}">
                            <strong>${cocktail.name}</strong>
                        </a>
                        <span>${cocktail.description}</span>
                    </li>
                </c:forEach>
            </ul>

        </c:if>

        <!-- BARTENDER -->
        <c:if test="${currentUser.role == 'BARTENDER'}">
            <form action="${pageContext.request.contextPath}/cocktail/add" method="get">
                <button class="button" type="submit">Add a cocktail</button>
            </form>
            <form action="${pageContext.request.contextPath}/cocktail/approve" method="get">
                <button class="button" type="submit">Approve cocktail</button>
            </form>
            <form action="${pageContext.request.contextPath}/logout" method="post">
                <button class="button danger" type="submit">Log out</button>
            </form>
            <h3>Cocktail list</h3>
            <ul class="cocktail-list">
                <c:forEach var="cocktail" items="${cocktailList}">
                    <li class="cocktail-item">
                        <a href="${pageContext.request.contextPath}/cocktail/view?id=${cocktail.id}">
                            <strong>${cocktail.name}</strong>
                        </a>
                        <span>${cocktail.description}</span>
                    </li>
                </c:forEach>
            </ul>

        </c:if>

        <!-- ADMIN -->
        <c:if test="${currentUser.role == 'ADMIN'}">
            <form action="${pageContext.request.contextPath}/cocktail/add" method="get">
                <button class="button" type="submit">Add a cocktail</button>
            </form>
            <form action="${pageContext.request.contextPath}/cocktail/approve" method="get">
                <button class="button" type="submit">Approve cocktail</button>
            </form>
            <form action="${pageContext.request.contextPath}/admin/users" method="get">
                <button class="button" type="submit">User management</button>
            </form>
            <form action="${pageContext.request.contextPath}/logout" method="post">
                <button class="button danger" type="submit">Log out</button>
            </form>
            <h3>Cocktail list</h3>
            <ul class="cocktail-list">
                <c:forEach var="cocktail" items="${cocktailList}">
                    <li class="cocktail-item">
                        <a href="${pageContext.request.contextPath}/cocktail/view?id=${cocktail.id}">
                            <strong>${cocktail.name}</strong>
                        </a>
                        <span>${cocktail.description}</span>
                    </li>
                </c:forEach>
            </ul>

        </c:if>

    </div>

</div>

</body>
</html>
