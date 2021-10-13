package ggc.products;

import java.util.ArrayList;
import java.util.List;

public class Recipe {

    private final List<RecipeProduct> recipeProducts = new ArrayList<>();
    private double aggravatingFactor;

    public Recipe(double aggravatingFactor, List<RecipeProduct> products) {
        this.aggravatingFactor = aggravatingFactor;
        recipeProducts.addAll(products);
    }

    public double getAggravatingFactor() {
        return aggravatingFactor;
    }

    public List<RecipeProduct> getRecipeProducts() {
        return recipeProducts;
    }
    
}
