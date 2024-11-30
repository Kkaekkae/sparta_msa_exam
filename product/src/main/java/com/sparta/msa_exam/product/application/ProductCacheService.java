package com.sparta.msa_exam.product.application;

import com.sparta.msa_exam.product.application.dtos.CreateProductRequest;
import com.sparta.msa_exam.product.application.dtos.ProductResponse;
import com.sparta.msa_exam.product.domain.Product;
import com.sparta.msa_exam.product.domain.ProductRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductCacheService {
    private final ProductRepository productRepository;

    public ProductCacheService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // 상품 목록 조회 비즈니스 로직 - 필수 과제 - 캐시를 더 활용 해볼까요?
    @Cacheable(cacheNames = "product_list_cache")
    public List<ProductResponse> getProducts() {
        return productRepository.findAll().stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }


    // 상품 추가 비즈니스 로직 - 필수 과제 - 캐시를 더 활용 해볼까요?
    @CacheEvict(cacheNames = "product_list_cache", allEntries = true)
    public void createProduct(final CreateProductRequest request) {
        productRepository.save(Product.create(request.getName(), request.getSupplyPrice()));
    }
}
