package com.sparta.msa_exam.product.application.dtos;

import com.sparta.msa_exam.product.domain.Product;
import lombok.*;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class ProductResponse implements Serializable {
    private Long id;
    private String name;
    private Long price;

    public static ProductResponse of(final Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .build();
    }
}
