package ggc.app;

import ggc.partners.Partner;
import ggc.products.Batch;
import ggc.products.DerivedProduct;
import ggc.products.Product;
import ggc.products.Recipe;
import ggc.transactions.AcquisitionTransaction;
import ggc.transactions.BreakdownTransaction;
import ggc.transactions.SaleTransaction;
import ggc.util.Visitor;

import java.util.StringJoiner;
import java.util.stream.Collectors;

public class Stringifier extends Visitor<String> {

  @Override
  public String visit(AcquisitionTransaction transaction) {
    return new StringJoiner("|")
        .add("COMPRA")
        .add(Integer.toString(transaction.getId()))
        .add(transaction.getPartner().getId())
        .add(transaction.getProduct().getId())
        .add(Integer.toString(transaction.getQuantity()))
        .add(Long.toString(Math.round(transaction.totalValue())))
        .add(Integer.toString(transaction.getPaymentDate().orElse(0)))
        .toString();
  }

  @Override
  public String visit(BreakdownTransaction transaction) {
    // TODO
    return "";
  }

  @Override
  public String visit(SaleTransaction transaction) {
    // TODO
    return "";
  }

  @Override
  public String visit(Batch batch) {
    return new StringJoiner("|")
        .add(batch.product().getId())
        .add(batch.partner().getId())
        .add(Long.toString(Math.round(batch.price())))
        .add(Integer.toString(batch.quantity()))
        .toString();
  }

  @Override
  public String visit(Recipe recipe) {
    return recipe.getAggravatingFactor() + "|"
        + recipe.getRecipeComponents()
            .stream()
            .map(comp -> comp.product().getId() + ":" + comp.quantity())
            .collect(Collectors.joining("#"));
  }

  @Override
  public String visit(Product product) {
    return new StringJoiner("|")
        .add(product.getId())
        .add(Long.toString(Math.round(product.getMostExpensivePrice())))
        .add(Integer.toString(product.getQuantityInBatches()))
        .toString();
  }

  @Override
  public String visit(DerivedProduct product) {
    // TODO is there another way to do this?
    return this.visit((Product) product) + "|"
        + product.getRecipe().accept(this);
  }

  @Override
  public String visit(Partner partner) {
    return new StringJoiner("|")
        .add(partner.getId())
        .add(partner.getName())
        .add(partner.getAddress())
        .add("NORMAL") // TODO
        .add("0") // TODO
        .add("0") // TODO
        .add("0") // TODO
        .add("0") // TODO
        .toString();
  }
}
