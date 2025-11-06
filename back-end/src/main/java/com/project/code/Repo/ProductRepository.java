package com.project.code.Repo;

import com.project.code.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Retrieve all products (inherited from JpaRepository, but kept for clarity)
    List<Product> findAll();

    // Find products by category
    List<Product> findByCategory(String category);

    // Find products within a price range
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);

    // Find product by SKU
    Product findBySku(String sku);

    // Find product by exact name
    Product findByName(String name);

    // Find products matching name pattern within a specific store
    @Query("SELECT DISTINCT p FROM Product p JOIN p.inventory i WHERE i.store.id = :storeId AND LOWER(p.name) LIKE LOWER(CONCAT('%', :pname, '%'))")
    List<Product> findByNameLike(@Param("storeId") Long storeId, @Param("pname") String pname);

}
