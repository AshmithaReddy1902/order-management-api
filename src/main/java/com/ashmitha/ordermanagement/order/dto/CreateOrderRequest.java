
package com.ashmitha.ordermanagement.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class CreateOrderRequest {

    @NotEmpty
    @Valid
    private List<OrderLineRequest> items;

    public List<OrderLineRequest> getItems() { return items; }
    public void setItems(List<OrderLineRequest> items) { this.items = items; }

    public static class OrderLineRequest {
        private Long productId;
        private Integer quantity;

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
}
