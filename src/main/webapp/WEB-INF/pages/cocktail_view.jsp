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

        .cocktail-container h2 {
            text-align: center;
            margin-bottom: 10px;
        }

        .cocktail-info {
            margin: 20px 0;
        }

        .ingredients-list {
            list-style: none;
            padding: 0;
        }

        .ingredients-list li {
            padding: 5px 0;
            border-bottom: 1px solid #eee;
        }

        .button {
            margin: 10px 0;
            display: inline-block;
        }
    </style>
</head>
<body>

<div class="cocktail-container">

    <h2>${cocktail.name}</h2>

    <div class="cocktail-info">
        <p><strong>Description:</strong> ${cocktail.description}</p>
        <p><strong>Author:</strong> ${authorName}</p>
        <p><strong>Created at:</strong> ${cocktail.createdAt}</p>
        <h3>Ingredients:</h3>
        <ul class="ingredients-list">
            <c:forEach var="ingredient" items="${ingredients}">
                <li>${ingredient}</li>
            </c:forEach>
        </ul>
    </div>

    <form action="${pageContext.request.contextPath}/welcome" method="get">
        <button class="button" type="submit">Back</button>
    </form>

</div>

</body>
</html>
