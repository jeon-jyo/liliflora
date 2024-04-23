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
    public static class ProductDto {

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

        public static ProductDto fromEntity(Product entity) {
            return ProductDto.builder()
                    .productId(entity.getProductId())
                    .name(entity.getName())
                    .price(entity.getPrice())
                    .quantity(entity.getQuantity())
                    .category(entity.getCategory())
                    .description(entity.getDescription())
                    .build();
        }
    }

    @Builder
    public static class ProductSearch {
        private String keyword;
    }

}
