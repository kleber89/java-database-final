package com.project.code.Service;

import com.project.code.Model.Inventory;
import com.project.code.Model.Product;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class ServiceClass {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    public ServiceClass(InventoryRepository inventoryRepository, ProductRepository productRepository) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
    }

    /**
     * Checks if an inventory record exists for the product-store combination.
     * Returns false if inventory exists, otherwise true.
     */
    public boolean validateInventory(Inventory inventory) {
        if (inventory == null) {
            throw new IllegalArgumentException("inventory cannot be null");
        }
        if (inventory.getProduct() == null || inventory.getStore() == null) {
            throw new IllegalArgumentException("inventory.product and inventory.store must be set");
        }

        Long productId = inventory.getProduct().getId();
        Long storeId = inventory.getStore().getId();
        if (productId == null || storeId == null) {
            throw new IllegalArgumentException("product id and store id must be present");
        }

        Inventory found = inventoryRepository.findByProduct_IdAndStore_Id(productId, storeId);
        // return false if inventory exists, true otherwise
        return found == null;
    }

    /**
     * Checks if a product with the same name exists.
     * Returns false if a product with the same name exists, otherwise true.
     */
    public boolean validateProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("product cannot be null");
        }
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("product.name cannot be null or empty");
        }

        Product existing = productRepository.findByName(product.getName());
        return existing == null;
    }

    /**
     * Checks if a product exists by its id.
     * Returns false if the product does not exist, otherwise true.
     */
    public boolean validateProductId(long id) {
        return productRepository.findById(id).isPresent();
    }

    /**
     * Fetches the inventory record for the product-store combination.
     */
    public Inventory getInventoryId(Inventory inventory) {
        if (inventory == null) {
            throw new IllegalArgumentException("inventory cannot be null");
        }
        if (inventory.getProduct() == null || inventory.getStore() == null) {
            throw new IllegalArgumentException("inventory.product and inventory.store must be set");
        }

        Long productId = inventory.getProduct().getId();
        Long storeId = inventory.getStore().getId();
        if (productId == null || storeId == null) {
            throw new IllegalArgumentException("product id and store id must be present");
        }

        return inventoryRepository.findByProduct_IdAndStore_Id(productId, storeId);
    }

}
