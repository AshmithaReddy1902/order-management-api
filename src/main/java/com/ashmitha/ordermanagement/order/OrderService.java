package com.ashmitha.ordermanagement.order;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ashmitha.ordermanagement.inventory.Inventory;
import com.ashmitha.ordermanagement.inventory.InventoryRepository;
import com.ashmitha.ordermanagement.order.dto.CreateOrderRequest;
import com.ashmitha.ordermanagement.product.Product;
import com.ashmitha.ordermanagement.product.ProductRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        InventoryRepository inventoryRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
    }

    // ✅ PLACE ORDER
    @Transactional
    public Order placeOrder(CreateOrderRequest req) {

        Order order = new Order();
        order.setStatus(OrderStatus.PLACED);

        BigDecimal total = BigDecimal.ZERO;

        for (CreateOrderRequest.OrderLineRequest line : req.getItems()) {

            Long productId = line.getProductId();
            Integer quantityObj = line.getQuantity();

            if (productId == null) {
                throw new IllegalArgumentException("productId cannot be null");
            }
            if (quantityObj == null || quantityObj <= 0) {
                throw new IllegalArgumentException("quantity must be > 0");
            }

            int qty = quantityObj;

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

            Inventory inv = inventoryRepository.findById(product.getId())
                    .orElseThrow(() -> new IllegalStateException("Inventory missing for product: " + product.getId()));

            if (inv.getAvailableQty() < qty) {
                throw new IllegalArgumentException("Not enough stock for product " + product.getId()
                        + ". Available=" + inv.getAvailableQty() + ", requested=" + qty);
            }

            // Reserve stock
            inv.setAvailableQty(inv.getAvailableQty() - qty);
            inv.setReservedQty(inv.getReservedQty() + qty);
            inventoryRepository.save(inv);

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(qty);

            BigDecimal unitPrice = product.getPrice();
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(qty));

            item.setUnitPrice(unitPrice);
            item.setLineTotal(lineTotal);

            order.getItems().add(item);

            total = total.add(lineTotal);
        }

        order.setTotalAmount(total);
        return orderRepository.save(order);
    }

    // ✅ CANCEL ORDER
    @Transactional
    public Order cancelOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            return order; // already cancelled
        }

        // Release reserved stock back to available
        for (OrderItem item : order.getItems()) {
            Long productId = item.getProduct().getId();

            Inventory inv = inventoryRepository.findById(productId)
                    .orElseThrow(() -> new IllegalStateException("Inventory missing for product: " + productId));

            int qty = item.getQuantity();

            inv.setReservedQty(inv.getReservedQty() - qty);
            inv.setAvailableQty(inv.getAvailableQty() + qty);

            inventoryRepository.save(inv);
        }

        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }
}
