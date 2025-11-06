package com.project.code.Controller;

import com.project.code.Model.CombinedRequest;
import com.project.code.Model.Inventory;
import com.project.code.Model.Product;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.ProductRepository;
import com.project.code.Service.ServiceClass;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final ServiceClass serviceClass;

    public InventoryController(ProductRepository productRepository,
            InventoryRepository inventoryRepository,
            ServiceClass serviceClass) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.serviceClass = serviceClass;
    }

    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> updateInventory(@RequestBody CombinedRequest request) {
        Map<String, Object> resp = new HashMap<>();
        if (request == null || request.getProduct() == null || request.getInventory() == null) {
            resp.put("message", "Invalid request payload");
            return ResponseEntity.badRequest().body(resp);
        }

        Product product = request.getProduct();
        Inventory inventory = request.getInventory();

        // Validate product id
        if (!serviceClass.validateProductId(product.getId())) {
            resp.put("message", "Product id is invalid");
            return ResponseEntity.badRequest().body(resp);
        }

        Inventory existing = inventoryRepository.findByProduct_IdAndStore_Id(product.getId(),
                inventory.getStore().getId());
        if (existing != null) {
            // Update fields (stockLevel) and save
            existing.setStockLevel(inventory.getStockLevel());
            inventoryRepository.save(existing);
            resp.put("message", "Inventory updated successfully");
            resp.put("inventory", existing);
            return ResponseEntity.ok(resp);
        } else {
            resp.put("message", "No data available to update");
            return ResponseEntity.ok(resp);
        }
    }

    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveInventory(@RequestBody Inventory inventory) {
        Map<String, Object> resp = new HashMap<>();
        if (inventory == null) {
            resp.put("message", "Invalid inventory payload");
            return ResponseEntity.badRequest().body(resp);
        }

        boolean allowed = serviceClass.validateInventory(inventory);
        if (!allowed) {
            resp.put("message", "Inventory already exists for this product and store");
            return ResponseEntity.ok(resp);
        }

        Inventory saved = inventoryRepository.save(inventory);
        resp.put("message", "Inventory saved successfully");
        resp.put("inventory", saved);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/store/{storeId}/products")
    public ResponseEntity<Map<String, Object>> getAllProducts(@PathVariable Long storeId) {
        Map<String, Object> resp = new HashMap<>();
        List<Inventory> inventories = inventoryRepository.findByStore_Id(storeId);
        List<Product> products = inventories.stream()
                .map(Inventory::getProduct)
                .collect(Collectors.toList());
        resp.put("products", products);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/filter")
    public ResponseEntity<Map<String, Object>> getProductName(@RequestParam String category,
            @RequestParam String name) {
        Map<String, Object> resp = new HashMap<>();
        List<Product> all = productRepository.findAll();

        List<Product> filtered = all.stream()
                .filter(p -> {
                    boolean catOk = (category == null || "null".equals(category))
                            || p.getCategory().equalsIgnoreCase(category);
                    boolean nameOk = (name == null || "null".equals(name))
                            || p.getName().toLowerCase().contains(name.toLowerCase());
                    return catOk && nameOk;
                })
                .collect(Collectors.toList());

        resp.put("product", filtered);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchProduct(@RequestParam String name, @RequestParam Long storeId) {
        Map<String, Object> resp = new HashMap<>();
        List<Product> results = productRepository.findByNameLike(storeId, name);
        resp.put("product", results);
        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> removeProduct(@PathVariable Long id) {
        Map<String, Object> resp = new HashMap<>();
        if (!productRepository.findById(id).isPresent()) {
            resp.put("message", "Product not found");
            return ResponseEntity.ok(resp);
        }

        productRepository.deleteById(id);
        // remove related inventory entries
        inventoryRepository.deleteByProduct_Id(id);

        resp.put("message", "Product deleted successfully");
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateQuantity(@RequestParam Long productId, @RequestParam Long storeId,
            @RequestParam Integer quantity) {
        Inventory inv = inventoryRepository.findByProduct_IdAndStore_Id(productId, storeId);
        if (inv == null || inv.getStockLevel() == null) {
            return ResponseEntity.ok(false);
        }
        return ResponseEntity.ok(inv.getStockLevel() >= (quantity == null ? 0 : quantity));
    }

}
