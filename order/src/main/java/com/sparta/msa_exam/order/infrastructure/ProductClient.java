package com.sparta.msa_exam.order.infrastructure;

import com.sparta.msa_exam.order.application.ProductService;
import com.sparta.msa_exam.order.application.dtos.ProductData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

// 성퓸 서비스 호출 클라이언트가 응용 계층의 인터페이스인 ProductService 를 상속받아 DIP 를 적용합니다.
@FeignClient(name = "product-service")
public interface ProductClient extends ProductService {
    @GetMapping("/products")
        // 상품 목록 조회 API
    List<ProductData> getProducts();

    @GetMapping("/products")
    List<ProductData> getProductsByIds(@RequestParam(value = "ids") List<Long> productIds);

    @GetMapping("/products/error-path")
        // 존재하지 않는 URL 호출
    Boolean fallbackTest();
}
