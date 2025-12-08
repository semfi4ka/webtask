<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Cocktail Details</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">

    <style>
        .cocktail-container {
            width: 600px;
            margin: 50px auto;
            background: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 0 12px rgba(0, 0, 0, 0.1);
        }

        .ingredients-list {
            list-style: none;
            padding: 0;
        }

        .ingredients-list li {
            padding: 5px 0;
            border-bottom: 1px solid #eee;
        }

        /* Контейнер для кнопок */
        .button-row {
            display: flex;
            align-items: center;
            margin-top: 20px;
        }

        /* Back — растягивается */
        .button-back {
            flex: 1;
            margin-right: 10px;
        }

        /* Delete — маленькая */
        .button-delete {
            width: 100px;
            padding: 10px;
        }
    </style>
</head>
<body>

<div class="cocktail-container">

    <h2>${cocktail.name}</h2>

    <p><strong>Description:</strong> ${cocktail.description}</p>
    <p><strong>Author:</strong> ${authorName}</p>
    <p><strong>Created at:</strong> ${cocktail.createdAt}</p>

    <h3>Ingredients:</h3>
    <ul class="ingredients-list">
        <c:forEach var="ingredient" items="${ingredients}">
            <li>${ingredient}</li>
        </c:forEach>
    </ul>

    <!-- Кнопки -->
    <div class="button-row">

        <!-- Back -->
        <a href="${pageContext.request.contextPath}/welcome"
           class="button button-back">
            Back
        </a>

        <!-- Delete только для ADMIN -->
        <c:if test="${currentUser.role == 'ADMIN'}">
            <form action="${pageContext.request.contextPath}/delete"
                  method="post" style="margin:0;">
                <input type="hidden" name="id" value="${cocktail.id}">
                <button type="submit"
                        class="button danger button-delete"
                        onclick="return confirm('Delete cocktail?');">
                    Delete
                </button>
            </form>
        </c:if>

    </div>

</div>

</body>
</html>
