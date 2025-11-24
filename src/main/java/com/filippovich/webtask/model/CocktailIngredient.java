package com.filippovich.webtask.model;

import java.io.Serializable;

public class CocktailIngredient implements Serializable {
    private Ingredient ingredient;
    private double amount;

    public CocktailIngredient() {
    }

    public CocktailIngredient(Ingredient ingredient, double amount) {
        this.ingredient = ingredient;
        this.amount = amount;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
