package ggc.products;

import ggc.exceptions.OutOfStockException;
import ggc.exceptions.UnavailableProductException;
import ggc.partners.Partner;
import ggc.util.Visitor;
import ggc.transactions.BreakdownTransaction;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

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
   * @return the sum of the in-stock quantity and the
   *         {@link DerivedProduct#getBuildableQuantity() buildable quantity}
   */
  @Override
  public int getTotalQuantity() {
    return super.getTotalQuantity() + this.getBuildableQuantity();
  }

  /**
   * Calculate the quantity of the product that can be built using the recipe.
   *
   * @return the quantity of the product that can be built using the recipe
   */
  public int getBuildableQuantity() {
    // TODO
    return 0;
  }

  /**
   * Calculate whether this product is presently available, either directly or
   * through building (for derived products), recursively checking recipe
   * components.
   * 
   * @throws OutOfStockException if there is not enough of a component, if a
   *                             build would be necessary (this exception
   *                             references the first missing component)
   */
  @Override
  public void assertAvailable(int quantity) throws OutOfStockException {
    // FIXME: vv would this look better with a for (int c = ...; c < q; c++)?
    int current = this.getQuantityInBatches();
    while (current < quantity) {
      for (RecipeComponent c : this.getRecipe().getRecipeComponents()) {
        c.product().assertAvailable(c.quantity());
      }
      current++;
    }
  }

  /**
   * Use this product's recipe to create a new batch, consuming the recipe
   * products.
   *
   * @param quantity The quantity to be built
   * @param partner  The partner that wants the product to be built
   */
  public void buildFromRecipe(int quantity, Partner partner) {
    // TODO
  }

  public Recipe getRecipe() {
    return this.recipe;
  }

  @Override
  public void breakdown(int date, Partner partner, int quantity,
      Supplier<Integer> idSupplier,
      Consumer<BreakdownTransaction> saveBreakdownTransaction)
      throws UnavailableProductException {
    final int available = this.getTotalQuantity();
    if (available < quantity) {
      throw new UnavailableProductException(this.getId(), quantity, available);
    }
    AtomicReference<Double> saleValue = new AtomicReference<>(0D);
    this.sell(
        date,
        partner,
        quantity,
        () -> -1,
        t -> saleValue.set(t.baseValue()));
    List<Batch> newBatches = new ArrayList<Batch>();
    for (RecipeComponent c : this.getRecipe().getRecipeComponents()) {
      c.product().acquire(
          date,
          partner,
          quantity * c.quantity(),
          c.product().getPriceForBreakdown(),
          () -> -1,
          t -> newBatches.add(new Batch(
              t.getQuantity(),
              t.baseValue(),
              t.getProduct(),
              t.getPartner())));
    }
    final double acquisitionValue = newBatches.stream()
        .map(b -> b.price())
        .reduce(Double::sum)
        .orElse(0D);
    saveBreakdownTransaction.accept(new BreakdownTransaction(
        idSupplier.get(),
        saleValue.get() - acquisitionValue,
        quantity,
        this,
        partner,
        newBatches));
  }

  @Override
  public <T> T accept(Visitor<T> visitor) {
    return visitor.visit(this);
  }
}
