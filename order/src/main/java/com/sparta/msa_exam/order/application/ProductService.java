package com.sparta.msa_exam.order.application;

import com.sparta.msa_exam.order.application.dtos.ProductData;

import java.util.List;
// 응용 계층 DIP 적용을 위한 상품 서비스 인터페이스
public interface ProductService {
    List<ProductData> getProducts();
}
