package com.sparta.msa_exam.order.domain;

import jakarta.persistence.*;
import lombok.*;

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
    @Column(name = "supply_price")
    private List<OrderProduct> productList;

    public static Order create(final String name) {
        return Order.builder().name(name).build();
    }

    // 주문에 상품 추가 메서드
    public void addProduct(OrderProduct product){
        productList.add(product);
    }
}
