package com.project.code.Controller;

import com.project.code.Model.PlaceOrderRequestDTO;
import com.project.code.Model.Store;
import com.project.code.Repo.StoreRepository;
import com.project.code.Service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/store")
public class StoreController {

    private final StoreRepository storeRepository;
    private final OrderService orderService;

    public StoreController(StoreRepository storeRepository, OrderService orderService) {
        this.storeRepository = storeRepository;
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> addStore(@RequestBody Store store) {
        Map<String, String> resp = new HashMap<>();
        if (store == null) {
            resp.put("message", "Invalid store payload");
            return ResponseEntity.badRequest().body(resp);
        }
        storeRepository.save(store);
        resp.put("message", "Store created successfully");
        return ResponseEntity.ok(resp);
    }

    @GetMapping("validate/{storeId}")
    public ResponseEntity<Boolean> validateStore(@PathVariable Long storeId) {
        boolean exists = storeRepository.findById(storeId) != null;
        return ResponseEntity.ok(exists);
    }

    @PostMapping("/placeOrder")
    public ResponseEntity<Map<String, String>> placeOrder(@RequestBody PlaceOrderRequestDTO placeOrderRequest) {
        Map<String, String> resp = new HashMap<>();
        try {
            orderService.saveOrder(placeOrderRequest);
            resp.put("message", "Order placed successfully");
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("Error", e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }

}
