package com.project.code.Repo;

import com.project.code.Model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    // Find inventory by product id and store id
    Inventory findByProduct_IdAndStore_Id(Long productId, Long storeId);

    // Find all inventories for a given store id
    List<Inventory> findByStore_Id(Long storeId);

    // Delete inventories by product id
    @Modifying
    @Transactional
    void deleteByProduct_Id(Long productId);

}
