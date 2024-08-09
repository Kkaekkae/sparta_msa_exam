package com.sparta.msa_exam.order.application.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AddProductToOrderRequest {
    private Long productId;
}
