package com.sparta.msa_exam.product.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "product_name", unique = true)
    private String name;
    @Column(name = "supply_price")
    private Long price;

    public static Product create(String name, Long price) {
        return Product.builder()
                .name(name)
                .price(price)
                .build();
    }
}
