package com.liliflora.service;

import com.liliflora.dto.ProductResponseDto;
import com.liliflora.entity.Product;
import com.liliflora.entity.Stock;
import com.liliflora.repository.ProductRepository;
import com.liliflora.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// @RequiredArgsConstructor - 롬복이 자동으로 생성자를 생성
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final StockRepository stockRepository;

    // 상품 목록
    @Transactional
    public List<ProductResponseDto.ProductDetailDto> productList() {
        return productRepository.findAllByOrderByProductIdDesc().stream()
                .map(ProductResponseDto.ProductDetailDto::fromEntity)
                .toList();
    }

    // 상품 상세
    @Transactional
    public ProductResponseDto.ProductDetailDto productDetail(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        Stock stock = stockRepository.findById(productId)
                .orElseGet(() -> {
                    // 없으면 새로운 Stock 객체 생성
                    Stock newStock = new Stock(product.getProductId(), product.getQuantity());
                    return stockRepository.save(newStock); // 새로운 재고 redis 저장하고 반환
                });

        return ProductResponseDto.ProductDetailDto.fromEntityAndQuantity(product, stock.getQuantity());
    }
}
