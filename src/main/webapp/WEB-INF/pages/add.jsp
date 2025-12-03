<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Add Cocktail</title>
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

        .ingredient-row {
            display: flex;
            gap: 10px;
            margin-bottom: 10px;
        }

        .ingredient-row input {
            flex: 1;
            padding: 5px;
        }

        button {
            margin-top: 10px;
        }

        .button {
            display: inline-block;
            margin-top: 15px;
            padding: 6px 12px;
            background-color: #3498db;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            text-decoration: none;
        }

        .button:hover {
            background-color: #2980b9;
        }
    </style>
</head>
<body>

<div class="welcome-container">
    <h2>${currentUser.role == 'CLIENT' ? 'Offer a cocktail' : 'Add a cocktail'}</h2>

    <form action="${pageContext.request.contextPath}/add" method="post">
        <label>Name:</label>
        <input type="text" name="name" required><br><br>

        <label>Description:</label>
        <textarea name="description" rows="3" style="width:100%;"></textarea><br><br>

        <h3>Ingredients:</h3>
        <div id="ingredients-container">
            <div class="ingredient-row">
                <input type="text" name="ingredientName" placeholder="Ingredient Name" required>
                <input type="text" name="ingredientAmount" placeholder="Amount">
                <input type="text" name="ingredientUnit" placeholder="Unit">
            </div>
        </div>

        <button type="submit">${currentUser.role == 'CLIENT' ? 'Offer a cocktail' : 'Add Cocktail'}</button>
    </form>

    <a href="${pageContext.request.contextPath}/welcome" class="button">Back</a>
</div>

<script>
    const container = document.getElementById('ingredients-container');

    container.addEventListener('input', (e) => {
        const lastRow = container.lastElementChild;
        const inputs = lastRow.querySelectorAll('input');

        let anyFilled = false;
        inputs.forEach(input => { if (input.value.trim() !== '') anyFilled = true; });

        if (anyFilled) {
            const newRow = lastRow.cloneNode(true);
            newRow.querySelectorAll('input').forEach(i => i.value = '');
            newRow.querySelector('input[name="ingredientName"]').required = false;
            container.appendChild(newRow);
        }
    });
</script>

</body>
</html>
