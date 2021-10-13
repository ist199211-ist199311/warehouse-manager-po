package ggc.products;

import ggc.exceptions.OutOfStockException;
import ggc.partners.Partner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Product implements Comparable<Product> {

    private final String id;
    private final List<Batch> batches = new ArrayList<>();
    private final Set<Partner> subscribers = new HashSet<>();

    public Product(String id) {
        // TODO subscribe all existing
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public List<Batch> getBatches() {
        return batches;
    }

    public int getQuantityInBatches() {
        return this.batches.stream().map(Batch::getQuantity).reduce((i1, i2) -> i1 + i2).orElse(0);
    }

    public int getTotalQuantity() {
        return this.getQuantityInBatches();
    }

    public Collection<Batch> getBatchesForAcquisition(int quantity) {
        this.ensureBatchesSorted();
        List<Batch> batchesForAcquisition = new ArrayList<>();
        int addedQuantity = 0;
        Iterator<Batch> iterator = this.batches.iterator();

        while (addedQuantity < quantity && iterator.hasNext()) {
            Batch batch = iterator.next();
            batchesForAcquisition.add(batch);
            addedQuantity += batch.getQuantity();
        }

        return batchesForAcquisition;
    }

    public double getCheapestPrice() throws OutOfStockException {
        ensureBatchesSorted();
        if (batches.size() == 0) throw new OutOfStockException();
        return batches.get(0).getPrice();
    }

    public void subscribe(Partner partner) {
        this.subscribers.add(partner);
    }

    public void unsubscribe(Partner partner) {
        this.subscribers.remove(partner);
    }

    public boolean isSubscribed(Partner partner) {
        return this.subscribers.contains(partner);
    }

    public void toggleSubscription(Partner partner) {
        if (this.isSubscribed(partner)) {
            this.unsubscribe(partner);
        } else {
            this.subscribe(partner);
        }
    }

    private void ensureBatchesSorted() {
        this.batches.sort((batch1, batch2) -> {
            int priceCmp = Double.compare(batch1.getPrice(), batch2.getPrice());
            if (priceCmp != 0) return priceCmp;
            return Integer.compare(batch1.getQuantity(), batch2.getQuantity());
        });
    }

    @Override
    public int compareTo(Product product) {
        return this.id.compareTo(product.getId());
    }
}
