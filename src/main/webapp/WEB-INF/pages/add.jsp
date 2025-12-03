<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Add Cocktail</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <script>
        function addIngredientRow() {
            const container = document.getElementById("ingredients-container");
            const rows = container.querySelectorAll(".ingredient-row");
            const lastRow = rows[rows.length - 1];

            const name = lastRow.querySelector(".ingredient-name").value;
            const amount = lastRow.querySelector(".ingredient-amount").value;
            const unit = lastRow.querySelector(".ingredient-unit").value;

            if (!name || !amount || !unit) return;

            const newRow = lastRow.cloneNode(true);
            newRow.querySelectorAll("input").forEach(input => input.value = "");
            container.appendChild(newRow);
        }

        function initIngredientRows() {
            const container = document.getElementById("ingredients-container");
            container.addEventListener("input", function(e){
                if (e.target.tagName.toLowerCase() === "input") {
                    addIngredientRow();
                }
            });
        }

        window.onload = initIngredientRows;

        function goBack() {
            window.location.href = "${pageContext.request.contextPath}/welcome";
        }
    </script>

    <style>
        .form-container button {
            margin: 5px 0;
        }
    </style>
</head>
<body>
<div class="form-container">
    <h2>Add Cocktail</h2>
    <form action="${pageContext.request.contextPath}/cocktail/add" method="post">
        <input type="text" name="name" placeholder="Cocktail Name" required>
        <textarea name="description" placeholder="Description" rows="3" required></textarea>

        <h3>Ingredients</h3>
        <div id="ingredients-container">
            <div class="ingredient-row">
                <input type="text" name="ingredientName" class="ingredient-name" placeholder="Ingredient Name">
                <input type="text" name="ingredientAmount" class="ingredient-amount" placeholder="Amount">
                <input type="text" name="ingredientUnit" class="ingredient-unit" placeholder="Unit">
            </div>
        </div>

        <button type="submit">Add Cocktail</button>
        <button type="button" onclick="goBack()">Back</button>
    </form>

    <c:if test="${not empty message}">
        <div class="${success ? 'success' : 'error'}">${message}</div>
    </c:if>
</div>
</body>
</html>
