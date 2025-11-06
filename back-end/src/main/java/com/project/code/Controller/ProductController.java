package com.project.code.Controller;

import com.project.code.Model.Product;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.ProductRepository;
import com.project.code.Service.ServiceClass;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/product")
public class ProductController {

	private final ProductRepository productRepository;
	private final InventoryRepository inventoryRepository;
	private final ServiceClass serviceClass;

	public ProductController(ProductRepository productRepository,
							 InventoryRepository inventoryRepository,
							 ServiceClass serviceClass) {
		this.productRepository = productRepository;
		this.inventoryRepository = inventoryRepository;
		this.serviceClass = serviceClass;
	}

	@PostMapping
	public ResponseEntity<Map<String, Object>> addProduct(@RequestBody Product product) {
		Map<String, Object> resp = new HashMap<>();
		try {
			if (!serviceClass.validateProduct(product)) {
				resp.put("message", "Product with same name already exists");
				return ResponseEntity.badRequest().body(resp);
			}
			Product saved = productRepository.save(product);
			resp.put("message", "Product saved successfully");
			resp.put("product", saved);
			return ResponseEntity.ok(resp);
		} catch (DataIntegrityViolationException ex) {
			resp.put("message", "Data integrity violation: " + ex.getMessage());
			return ResponseEntity.badRequest().body(resp);
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<Map<String, Object>> getProductbyId(@PathVariable Long id) {
		Map<String, Object> resp = new HashMap<>();
		Optional<Product> p = productRepository.findById(id);
		if (!p.isPresent()) {
			resp.put("products", null);
			return ResponseEntity.ok(resp);
		}
		resp.put("products", p.get());
		return ResponseEntity.ok(resp);
	}

	@PutMapping
	public ResponseEntity<Map<String, Object>> updateProduct(@RequestBody Product product) {
		Map<String, Object> resp = new HashMap<>();
		Product updated = productRepository.save(product);
		resp.put("message", "Product updated successfully");
		resp.put("product", updated);
		return ResponseEntity.ok(resp);
	}

	@GetMapping("/category/{name}/{category}")
	public ResponseEntity<Map<String, Object>> filterbyCategoryProduct(@PathVariable String name, @PathVariable String category) {
		Map<String, Object> resp = new HashMap<>();
		List<Product> all = productRepository.findAll();
		List<Product> filtered = all.stream()
				.filter(p -> {
					boolean catOk = (category == null || "null".equals(category)) || p.getCategory().equalsIgnoreCase(category);
					boolean nameOk = (name == null || "null".equals(name)) || p.getName().toLowerCase().contains(name.toLowerCase());
					return catOk && nameOk;
				})
				.collect(Collectors.toList());
		resp.put("products", filtered);
		return ResponseEntity.ok(resp);
	}

	@GetMapping
	public ResponseEntity<Map<String, Object>> listProduct() {
		Map<String, Object> resp = new HashMap<>();
		List<Product> all = productRepository.findAll();
		resp.put("products", all);
		return ResponseEntity.ok(resp);
	}

	@GetMapping("/filter/{category}/{storeid}")
	public ResponseEntity<Map<String, Object>> getProductbyCategoryAndStoreId(@PathVariable String category, @PathVariable Long storeid) {
		Map<String, Object> resp = new HashMap<>();
		List<Product> products = productRepository.findByCategory(category);
		resp.put("product", products);
		return ResponseEntity.ok(resp);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable Long id) {
		Map<String, Object> resp = new HashMap<>();
		if (!serviceClass.validateProductId(id)) {
			resp.put("message", "Product does not exist");
			return ResponseEntity.badRequest().body(resp);
		}

		// delete inventories and product
		inventoryRepository.deleteByProduct_Id(id);
		productRepository.deleteById(id);
		resp.put("message", "Product deleted successfully");
		return ResponseEntity.ok(resp);
	}

	@GetMapping("/searchProduct/{name}")
	public ResponseEntity<Map<String, Object>> searchProduct(@PathVariable String name, @RequestParam(required = false) Long storeId) {
		Map<String, Object> resp = new HashMap<>();
		List<Product> results;
		if (storeId != null) {
			results = productRepository.findByNameLike(storeId, name);
		} else {
			results = productRepository.findAll().stream()
					.filter(p -> p.getName().toLowerCase().contains(name.toLowerCase()))
					.collect(Collectors.toList());
		}
		resp.put("products", results);
		return ResponseEntity.ok(resp);
	}

}
