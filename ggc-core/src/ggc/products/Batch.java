package ggc.products;

import ggc.partners.Partner;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;
import java.util.StringJoiner;

public record Batch(int quantity, double price, Product product,
                    Partner partner) implements Comparable<Batch>,
        Serializable {
  /**
   * Serial number for serialization.
   */
  @Serial
  private static final long serialVersionUID = 202110221420L;

  public Batch cloneWithQuantity(int quantity) {
    return new Batch(quantity, this.price, this.product, this.partner);
  }

  @Override
  public int compareTo(Batch batch) {
    return Comparator.comparing(Batch::product)
            .thenComparing(Batch::partner)
            .thenComparingDouble(Batch::price)
            .thenComparingInt(Batch::quantity)
            .compare(this, batch);
  }

  @Override
  public String toString() {
    return new StringJoiner("|")
            .add(this.product().getId())
            .add(this.partner().getId())
            .add(Long.toString(Math.round(this.price())))
            .add(Integer.toString(this.quantity()))
            .toString();
  }
}
