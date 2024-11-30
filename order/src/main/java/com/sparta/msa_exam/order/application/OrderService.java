package com.sparta.msa_exam.order.application;

import com.sparta.msa_exam.order.application.dtos.AddProductToOrderRequest;
import com.sparta.msa_exam.order.application.dtos.CreateOrderRequest;
import com.sparta.msa_exam.order.application.dtos.OrderResponse;
import com.sparta.msa_exam.order.application.dtos.ProductData;
import com.sparta.msa_exam.order.domain.Order;
import com.sparta.msa_exam.order.domain.OrderProduct;
import com.sparta.msa_exam.order.domain.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;


@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;

    public OrderService(final OrderRepository orderRepository, final ProductService productService) {
        this.orderRepository = orderRepository;
        this.productService = productService;
    }

    @CircuitBreaker(name = "order-service", fallbackMethod = "fallbackMethod")
    public OrderResponse fallback() {
        productService.fallbackTest();
        return OrderResponse.of(Order.create("테스트"));
    }

    public OrderResponse fallbackMethod(Throwable t) {
        throw new RuntimeException("잠시 후에 주문 추가를 요청 해주세요.");
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
    public OrderResponse createOrder(final CreateOrderRequest request) {
        final Order order = Order.create(request.getName());
        List<ProductData> products = productService.getProductsByIds(request.getProductIds());
        products.forEach(product -> order.addProduct(OrderProduct.create(order, product.getId())));
        orderRepository.save(order);

        return OrderResponse.of(order);
    }


    // 주문 상품 추가 비즈니스 로직
    @Transactional
    public void addProductToOrder(final Long orderId, final AddProductToOrderRequest request) {
        final Order order = orderRepository.findById(orderId).orElseThrow();
        // 필수 과제 - 주문에 상품을 추가하는 API 만들 때 아래와 같이 구성해보세요.
        productService.getProducts().stream() // 상품 목록 조회
                .filter(item -> Objects.equals(item.getId(), request.getProductId())).findAny()  // 상품 목록 중에 request.getProductId() 와 Id 가 일치하는 상품이 있는지 검색
                .ifPresent(product -> order.addProduct(OrderProduct.create(order, product.getId()))); // 상품이 있다면 주문 Entity 의 addProduct 메서드로 상품을 추가함
    }
}
