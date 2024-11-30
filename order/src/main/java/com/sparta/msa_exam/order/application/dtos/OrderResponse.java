package com.sparta.msa_exam.order.application.dtos;

import com.sparta.msa_exam.order.domain.Order;
import com.sparta.msa_exam.order.domain.OrderProduct;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class OrderResponse implements Serializable {
    private Long orderId;
    private String name;
    private List<Long> productIds;

    // Order -> OrderResponse 변경 Static 메서드
    public static OrderResponse of(final Order order) {
        return OrderResponse.builder()
                .orderId(order.getId())
                .name(order.getName())
                .productIds(order.getProductList().stream().map(OrderProduct::getProductId).collect(Collectors.toList()))
                .build();
    }

}
