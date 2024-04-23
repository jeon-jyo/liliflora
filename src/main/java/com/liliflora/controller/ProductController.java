package com.liliflora.controller;

import com.liliflora.dto.ProductResponseDto;
import com.liliflora.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 상품 목록
    @GetMapping("/list")
    public List<ProductResponseDto.ProductDto> productList() {
        log.info("ProductController.productList()");

        return productService.productList();
    }

    // 상품 상세
    @GetMapping("/{productId}")
    public ProductResponseDto.ProductDto productDetail(@PathVariable Long productId) {
        log.info("ProductController.productDetail()");

        return productService.productDetail(productId);
    }
}
