package com.sparta.msa_exam.order.controller;

import com.sparta.msa_exam.order.application.OrderService;
import com.sparta.msa_exam.order.application.dtos.AddProductToOrderRequest;
import com.sparta.msa_exam.order.application.dtos.CreateOrderRequest;
import com.sparta.msa_exam.order.application.dtos.OrderResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    private final String serverPort;

    public OrderController(OrderService orderService, @Value("${server.port}") String serverPort) {
        this.orderService = orderService;
        this.serverPort = serverPort;
    }

    // 주문 단건 조회 API
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@RequestParam(value = "fallback", required = false) boolean fallback,
                                                  @PathVariable(name = "orderId") Long orderId) {

        OrderResponse response = fallback
                ? orderService.fallback()
                : orderService.getOrder(orderId);

        return createResponse(ResponseEntity.ok(response));
    }

    // 주문 추가 API
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return createResponse(ResponseEntity.ok(response));
    }

    // 주문에 상품을 추가하는 API
    @PutMapping("/{orderId}")
    public ResponseEntity<Boolean> addProductToOrder(@PathVariable(name = "orderId") Long orderId, @RequestBody AddProductToOrderRequest request) {
        orderService.addProductToOrder(orderId, request);
        return createResponse(ResponseEntity.ok(true));
    }

    // Response Header 에 `Server-Port` 룰 추가해주는 Generic 함수입니다.
    public <T> ResponseEntity<T> createResponse(ResponseEntity<T> response) {
        HttpHeaders headers = HttpHeaders.writableHttpHeaders(response.getHeaders()); // 인자로 받은 헤더의 정보를 수정할 수 있도록 불러옵니다.
        headers.add("Server-Port", serverPort); // Response Header 에 Server-Port 키값을 추가합니다.
        return new ResponseEntity<>(response.getBody(), headers, response.getStatusCode()); //인자로 받은 값에 수정한 헤더만 적용하여 응답합니다.
    }
}