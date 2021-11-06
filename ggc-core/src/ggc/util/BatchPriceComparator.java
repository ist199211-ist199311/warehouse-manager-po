package ggc.util;

import ggc.products.Batch;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;

public class BatchPriceComparator implements Comparator<Batch>, Serializable {
  @Serial
  private static final long serialVersionUID = 202111061319L;

  private transient Comparator<Batch> comparator;

  public BatchPriceComparator() {
    setComparator();
  }

  private void setComparator() {
    this.comparator = Comparator
            .comparingDouble(Batch::price)
            .thenComparingInt(Batch::quantity);
  }

  @Serial
  private void readObject(ObjectInputStream ois) throws IOException,
          ClassNotFoundException {
    ois.defaultReadObject();
    setComparator();
  }

  @Override
  public int compare(Batch b1, Batch b2) {
    return comparator.compare(b1, b2);
  }
}