package com.liliflora.service;

import com.liliflora.dto.ProductResponseDto;
import com.liliflora.entity.Product;
import com.liliflora.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

// @RequiredArgsConstructor - 롬복이 자동으로 생성자를 생성
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    // 상품 목록
    @Transactional
    public List<ProductResponseDto.ProductDetailDto> productList() {
        return productRepository.findAllByOrderByProductIdDesc().stream()
                .map(ProductResponseDto.ProductDetailDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 상품 상세
    @Transactional
    public ProductResponseDto.ProductDetailDto productDetail(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        return ProductResponseDto.ProductDetailDto.fromEntity(product);
    }
}
