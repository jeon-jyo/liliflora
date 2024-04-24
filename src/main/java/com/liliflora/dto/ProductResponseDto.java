package com.liliflora.dto;

import com.liliflora.entity.Product;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class ProductResponseDto {

    @Getter
    @Setter
    @Builder
    public static class ProductDetailDto {

        private long productId;

        @NotBlank(message = "이름은 필수 입력 값입니다.")
        private String name;

        @NotBlank(message = "가격은 필수 입력 값입니다.")
        private int price;

        @NotBlank(message = "재고는 필수 입력 값입니다.")
        private int quantity;

        @NotBlank(message = "카테고리는 필수 입력 값입니다.")
        private String category;

        private String description;

        public static ProductDetailDto fromEntity(Product product) {
            return ProductDetailDto.builder()
                    .productId(product.getProductId())
                    .name(product.getName())
                    .price(product.getPrice())
                    .quantity(product.getQuantity())
                    .category(product.getCategory())
                    .description(product.getDescription())
                    .build();
        }
    }

    @Builder
    public static class ProductSearch {
        private String keyword;
    }

}
