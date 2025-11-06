package com.project.code.Service;

import com.project.code.Model.*;
import com.project.code.Repo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
public class OrderService {

    private final CustomerRepository customerRepository;
    private final StoreRepository storeRepository;
    private final InventoryRepository inventoryRepository;
    private final OrderDetailsRepository orderDetailsRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    public OrderService(CustomerRepository customerRepository,
            StoreRepository storeRepository,
            InventoryRepository inventoryRepository,
            OrderDetailsRepository orderDetailsRepository,
            OrderItemRepository orderItemRepository,
            ProductRepository productRepository) {
        this.customerRepository = customerRepository;
        this.storeRepository = storeRepository;
        this.inventoryRepository = inventoryRepository;
        this.orderDetailsRepository = orderDetailsRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
    }

    /**
     * Procesa una orden: crea/recupera cliente, crea OrderDetails, decrementa
     * inventario
     * y crea OrderItem por cada producto. Operación transaccional para mantener
     * consistencia.
     */
    @Transactional
    public void saveOrder(PlaceOrderRequestDTO placeOrderRequest) {
        if (placeOrderRequest == null) {
            throw new IllegalArgumentException("placeOrderRequest no puede ser null");
        }

        // 1) Obtener o crear cliente
        String email = placeOrderRequest.getCustomerEmail();
        Customer customer = null;
        if (email != null && !email.isEmpty()) {
            customer = customerRepository.findByEmail(email);
        }
        if (customer == null) {
            customer = new Customer();
            customer.setName(placeOrderRequest.getCustomerName());
            customer.setEmail(placeOrderRequest.getCustomerEmail());
            customer.setPhone(placeOrderRequest.getCustomerPhone());
            customer = customerRepository.save(customer);
        }

        // 2) Obtener tienda
        Long storeId = placeOrderRequest.getStoreId();
        if (storeId == null) {
            throw new IllegalArgumentException("storeId is required in placeOrderRequest");
        }
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found with id: " + storeId));

        // 3) Crear OrderDetails
        OrderDetails order = new OrderDetails();
        order.setCustomer(customer);
        order.setStore(store);
        order.setTotalPrice(placeOrderRequest.getTotalPrice());
        order.setDate(LocalDateTime.now());
        order.setOrderItems(new ArrayList<>());

        order = orderDetailsRepository.save(order);

        // 4) Por cada producto en la petición: validar inventario, decrementar stock y
        // crear OrderItem
        if (placeOrderRequest.getPurchaseProduct() != null) {
            for (PurchaseProductDTO p : placeOrderRequest.getPurchaseProduct()) {
                if (p == null || p.getId() == null)
                    continue;

                // Obtener inventario para el producto y tienda
                Inventory inventory = inventoryRepository.findByProduct_IdAndStore_Id(p.getId(), storeId);
                if (inventory == null) {
                    throw new RuntimeException("Inventory not found for product " + p.getId() + " in store " + storeId);
                }

                Integer requestedQty = p.getQuantity() == null ? 0 : p.getQuantity();
                if (inventory.getStockLevel() == null) {
                    throw new RuntimeException("Inventory stock level undefined for product " + p.getId());
                }
                if (inventory.getStockLevel() < requestedQty) {
                    throw new RuntimeException("Insufficient stock for product " + p.getId() + ": requested="
                            + requestedQty + ", available=" + inventory.getStockLevel());
                }

                // Decrementar stock y persistir
                inventory.setStockLevel(inventory.getStockLevel() - requestedQty);
                inventoryRepository.save(inventory);

                // Obtener entidad Product
                Product product = productRepository.findById(p.getId()).orElse(null);
                if (product == null) {
                    throw new RuntimeException("Product not found with id: " + p.getId());
                }

                // Crear OrderItem
                OrderItem item = new OrderItem();
                item.setOrder(order);
                item.setProduct(product);
                item.setQuantity(requestedQty);
                item.setPrice(p.getPrice());

                item = orderItemRepository.save(item);

                // Asociar al OrderDetails en memoria
                order.getOrderItems().add(item);
            }

            // Guardar order actualizado (con items asociados)
            orderDetailsRepository.save(order);
        }
    }

}
