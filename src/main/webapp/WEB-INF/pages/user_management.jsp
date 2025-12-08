<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>User Management</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .user-management-container {
            width: 700px;
            margin: 50px auto;
            background: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 0 12px rgba(0, 0, 0, 0.1);
        }

        h2 {
            text-align: center;
            margin-bottom: 20px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }

        th, td {
            padding: 10px;
            border-bottom: 1px solid #ddd;
            text-align: left;
        }

        th {
            background: #f2f2f2;
        }

        .button {
            padding: 6px 10px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 14px;
            color: white;
            margin-right: 5px;
        }

        .button.promote {
            background: #4CAF50;
        }

        .button.promote:hover {
            background: #45a049;
        }

        .button.demote {
            background: #f44336;
        }

        .button.demote:hover {
            background: #d32f2f;
        }

        .back-button {
            margin-top: 20px;
        }
    </style>
</head>
<body>

<div class="user-management-container">
    <h2>User Management</h2>

    <table>
        <tr>
            <th>Username</th>
            <th>Email</th>
            <th>Role</th>
            <th>Cocktails</th>
            <th>Actions</th>
        </tr>
        <c:forEach var="user" items="${userList}">
            <tr>
                <td>${user.username}</td>
                <td>${user.email}</td>
                <td>${user.role}</td>
                <td>${user.cocktailCount}</td>
                <td>
                    <c:if test="${user.role == 'CLIENT'}">
                        <form style="display:inline;" method="post" action="${pageContext.request.contextPath}/admin/users">
                            <input type="hidden" name="userId" value="${user.id}" />
                            <input type="hidden" name="action" value="promote" />
                            <button class="button promote" type="submit">Promote</button>
                        </form>
                    </c:if>
                    <c:if test="${user.role == 'BARTENDER'}">
                        <form style="display:inline;" method="post" action="${pageContext.request.contextPath}/admin/users">
                            <input type="hidden" name="userId" value="${user.id}" />
                            <input type="hidden" name="action" value="demote" />
                            <button class="button demote" type="submit">Demote</button>
                        </form>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
    </table>

    <form action="${pageContext.request.contextPath}/welcome" method="get" class="back-button">
        <button class="button" type="submit">Back</button>
    </form>
</div>

</body>
</html>
