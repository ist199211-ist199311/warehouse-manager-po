package ggc.products;

import ggc.exceptions.UnavailableProductException;
import ggc.partners.Partner;
import ggc.transactions.BreakdownTransaction;
import ggc.util.Visitor;

import java.io.Serial;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class DerivedProduct extends Product {
  /**
   * Serial number for serialization.
   */
  @Serial
  private static final long serialVersionUID = 202110221420L;

  private final Recipe recipe;

  public DerivedProduct(String id, Recipe recipe) {
    super(id);
    this.recipe = recipe;
  }

  /**
   * Calculates how many units of this product need to be built from its recipe
   * to achieve a given quantity.
   */
  private int getMissingQuantity(int totalNeeded) {
    return Math.max(0, totalNeeded - this.getQuantityInBatches());
  }

  /**
   * Calculates whether this product is presently available, either directly or
   * through building (for derived products), recursively checking recipe
   * components.
   *
   * @throws UnavailableProductException if there is not enough of a component,
   *                                     if a build would be necessary (this
   *                                     exception references the first missing
   *                                     component)
   */
  @Override
  public void assertPossibleAvailability(int quantity)
          throws UnavailableProductException {
    Map<Product, Integer> missingStock = new HashMap<>();
    this.assertPossibleAvailability(quantity, missingStock);
    for (Map.Entry<Product, Integer> entry : missingStock.entrySet()) {
      final int availableInBatches = entry.getKey().getQuantityInBatches();
      if (availableInBatches < entry.getValue()) {
        throw new UnavailableProductException(entry.getKey().getId(),
                entry.getValue(), availableInBatches);
      }
    }
  }

  /**
   * Recursively builds up a map of the stock of simple products required to
   * sell a given amount of this product.
   *
   * @param quantity      The quantity required of this product.
   * @param requiredStock The map to store the required stock
   */
  @Override
  protected void assertPossibleAvailability(int quantity,
                                            Map<Product, Integer> requiredStock) {
    final int neededQuantity = this.getMissingQuantity(quantity);
    for (RecipeComponent c : this.getRecipe().getRecipeComponents()) {
      c.product().assertPossibleAvailability(c.quantity() * neededQuantity,
              requiredStock);
    }
  }

  /**
   * Tries to guarantee real availability in product stock of the given
   * quantity, building units from its recipe if necessary.
   *
   * @throws UnavailableProductException if unsuccessful
   */
  @Override
  public void ensureAvailableInBatches(int quantity, Partner partner)
          throws UnavailableProductException {
    final int neededQuantity = this.getMissingQuantity(quantity);
    if (neededQuantity > 0) {
      this.buildFromRecipe(neededQuantity, partner);
    }
  }

  /**
   * Use this product's recipe to create a new batch, consuming the recipe
   * products.
   *
   * @param quantity The quantity to be built
   * @param partner  The partner that wants the product to be built
   * @throws UnavailableProductException if it cannot be built
   */
  private void buildFromRecipe(int quantity, Partner partner)
          throws UnavailableProductException {
    for (int i = 0; i < quantity; i++) {
      double batchPrice = 0D;
      for (RecipeComponent c : this.getRecipe().getRecipeComponents()) {
        c.product().ensureAvailableInBatches(c.quantity(), partner);
        batchPrice += c.product()
                .pollBatchesForSale(c.quantity())
                .stream()
                .map(Batch::totalPrice)
                .reduce(Double::sum)
                .orElse(0D);
      }
      batchPrice += batchPrice * this.getRecipe().getAggravatingFactor();
      this.registerBatch(1, batchPrice, partner);
    }
  }

  public Recipe getRecipe() {
    return this.recipe;
  }

  @Override
  public Optional<BreakdownTransaction> breakdown(int date, Partner partner,
                                                  int quantity,
                                                  Supplier<Integer> idSupplier)
          throws UnavailableProductException {
    final int available = this.getQuantityInBatches();
    if (available < quantity) {
      throw new UnavailableProductException(this.getId(), quantity, available);
    }
    double saleValue = this.sell(
            date,
            partner,
            quantity,
            () -> -1
    ).baseValue();
    final List<Batch> newBatches = this.getRecipe().getRecipeComponents()
            .stream()
            .map(component -> component.product()
                    .acquire(
                            date,
                            partner,
                            quantity * component.quantity(),
                            component.product().getPriceForBreakdown(),
                            () -> -1
                    ).asBatch()
            )
            .collect(Collectors.toList());
    final double acquisitionValue = newBatches.stream()
            .map(Batch::totalPrice)
            .reduce(Double::sum)
            .orElse(0D);
    return Optional.of(new BreakdownTransaction(
            idSupplier.get(),
            date,
            saleValue - acquisitionValue,
            quantity,
            this,
            partner,
            newBatches
    ));
  }

  @Override
  public <T> T accept(Visitor<T> visitor) {
    return visitor.visit(this);
  }
}
