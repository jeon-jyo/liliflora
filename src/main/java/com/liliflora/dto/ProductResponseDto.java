package com.liliflora.dto;

import com.liliflora.entity.Product;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class ProductResponseDto {

    @Getter
    @Setter
    @Builder
    public static class ProductDetailDto {

        private long productId;

        private String name;

        private int price;

        private int quantity;

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
