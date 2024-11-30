package com.sparta.msa_exam.order.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_product")
@AllArgsConstructor
@NoArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
@Getter
public class OrderProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "product_id")
    private Long productId;

    public static OrderProduct create(final Order order, final Long productId) {
        return OrderProduct.builder()
                .order(order)
                .productId(productId)
                .build();
    }
}
