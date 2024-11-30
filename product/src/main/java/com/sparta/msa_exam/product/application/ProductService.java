package com.sparta.msa_exam.product.application;

import com.sparta.msa_exam.product.application.dtos.CreateProductRequest;
import com.sparta.msa_exam.product.application.dtos.ProductResponse;
import com.sparta.msa_exam.product.domain.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductCacheService productCacheService;

    public ProductService(ProductRepository productRepository, ProductCacheService productCacheService) {
        this.productRepository = productRepository;
        this.productCacheService = productCacheService;
    }

    public List<ProductResponse> getProducts(List<Long> productIds) {
        if (productIds != null) {
            return getProductsByIds(productIds);
        } else {
            return getProducts();
        }
    }

    public List<ProductResponse> getProductsByIds(List<Long> productIds) {
        return productRepository.findByIdIn(productIds).stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> getProducts() {
        return productCacheService.getProducts();
    }

    public void createProduct(final CreateProductRequest request) {
        productCacheService.createProduct(request);
    }
}
