package com.sparta.msa_exam.order.application;

import com.sparta.msa_exam.order.application.dtos.AddProductToOrderRequest;
import com.sparta.msa_exam.order.application.dtos.CreateOrderRequest;
import com.sparta.msa_exam.order.application.dtos.OrderResponse;
import com.sparta.msa_exam.order.domain.Order;
import com.sparta.msa_exam.order.domain.OrderRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

import static com.sparta.msa_exam.order.domain.OrderProduct.create;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;

    public OrderService(final OrderRepository orderRepository, final ProductService productService) {
        this.orderRepository = orderRepository;
        this.productService = productService;
    }


    // 주문 조회 비즈니스 로직
    @Cacheable(cacheNames = "order_cache")
    public OrderResponse getOrder(final Long orderId) {
        return orderRepository.findById(orderId)
                .map(OrderResponse::of)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    // 주문 추가 비즈니스 로직
    @Transactional
    public void createOrder(final CreateOrderRequest request) {
        orderRepository.save(Order.create(request.getName()));
    }


    // 주문 상품 추가 비즈니스 로직
    @Transactional
    public void addProductToOrder(final Long orderId, final AddProductToOrderRequest request) {
        final Order order = orderRepository.findById(orderId).orElseThrow();
        // 필수 과제 - 주문에 상품을 추가하는 API 만들 때 아래와 같이 구성해보세요.
        productService.getProducts().stream() // 상품 목록 조회
                 .filter(item -> Objects.equals(item.getId(), request.getProductId())).findAny()  // 상품 목록 중에 request.getProductId() 와 Id 가 일치하는 상품이 있는지 검색
                .ifPresent(product -> order.addProduct(create(order, product.getId()))); // 상품이 있다면 주문 Entity 의 addProduct 메서드로 상품을 추가함
    }
}
