package com.ashmitha.ordermanagement.product;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ashmitha.ordermanagement.inventory.InventoryRepository;
import com.ashmitha.ordermanagement.product.dto.CreateProductRequest;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    public ProductService(ProductRepository productRepository,
                          InventoryRepository inventoryRepository) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    public Product createProduct(CreateProductRequest req) {

        productRepository.findBySku(req.getSku()).ifPresent(p -> {
            throw new IllegalArgumentException("SKU already exists: " + req.getSku());
        });

        Product product = new Product();
        product.setSku(req.getSku());
        product.setName(req.getName());
        product.setPrice(req.getPrice());

        Product saved = productRepository.save(product);

        com.ashmitha.ordermanagement.inventory.Inventory inv =
                new com.ashmitha.ordermanagement.inventory.Inventory();
        inv.setProduct(saved);
        inv.setAvailableQty(req.getInitialStock());
        inv.setReservedQty(0);

        inventoryRepository.save(inv);

        return saved;
    }

}

