package com.sparta.msa_exam.order.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "`order`")
@AllArgsConstructor
@NoArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
@Getter
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST)
    private List<OrderProduct> productList = new ArrayList<>();

    public static Order create(final String name) {
        return Order.builder()
                .name(name)
                .productList(new ArrayList<>())
                .build();
    }

    // 주문에 상품 추가 메서드
    public void addProduct(OrderProduct product) {
        productList.add(product);
    }
}
