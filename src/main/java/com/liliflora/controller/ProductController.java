package com.liliflora.controller;

import com.liliflora.dto.ProductResponseDto;
import com.liliflora.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/list")
    public List<ProductResponseDto.ProductDto> productList(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("ProductController.productList()");

        return productService.productList();
    }
}
