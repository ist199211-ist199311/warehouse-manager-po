package ggc.products;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

	@Override
	public String toString() {
		return this.getAggravatingFactor() + "|"
				+ this.getRecipeProducts().stream().map(RecipeProduct::toString).collect(Collectors.joining("#"));
	}
}
