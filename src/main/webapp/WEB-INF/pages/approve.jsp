<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Approve Cocktails</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .container {
            width: 700px;
            margin: 50px auto;
            background: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 0 12px rgba(0, 0, 0, 0.1);
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }

        th, td {
            border: 1px solid #ddd;
            padding: 10px;
            text-align: left;
        }

        th {
            background-color: #f2f2f2;
        }

        .button {
            padding: 5px 10px;
            margin-right: 5px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            color: white;
        }

        .approve {
            background-color: #2ecc71;
        }

        .reject {
            background-color: #e74c3c;
        }

        .button:hover {
            opacity: 0.8;
        }

        a.cocktail-link {
            color: #2980b9;
            text-decoration: none;
            font-weight: bold;
        }

        a.cocktail-link:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
<div class="container">
    <h2>Cocktails Pending Approval</h2>

    <c:if test="${empty pendingCocktails}">
        <p>No cocktails pending moderation.</p>
    </c:if>

    <c:if test="${not empty pendingCocktails}">
        <table>
            <tr>
                <th>Name</th>
                <th>Description</th>
                <th>Author</th>
                <th>Created At</th>
                <th>Actions</th>
            </tr>
            <c:forEach var="cocktail" items="${pendingCocktails}">
                <tr>
                    <td>
                        <a class="cocktail-link" href="${pageContext.request.contextPath}/view?id=${cocktail.id}">
                                ${cocktail.name}
                        </a>
                    </td>
                    <td>${cocktail.description}</td>
                    <td>${cocktail.author.username}</td>
                    <td>${cocktail.createdAt}</td>
                    <td>
                        <form action="${pageContext.request.contextPath}/approve" method="post" style="display:inline;">
                            <input type="hidden" name="cocktailId" value="${cocktail.id}">
                            <input type="hidden" name="action" value="approve">
                            <button class="button approve" type="submit">Approve</button>
                        </form>
                        <form action="${pageContext.request.contextPath}/approve" method="post" style="display:inline;">
                            <input type="hidden" name="cocktailId" value="${cocktail.id}">
                            <input type="hidden" name="action" value="reject">
                            <button class="button reject" type="submit">Reject</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:if>

    <a href="${pageContext.request.contextPath}/welcome" class="button" style="background-color:#3498db;">Back</a>
</div>
</body>
</html>
