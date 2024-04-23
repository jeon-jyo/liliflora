package com.liliflora.service;

import com.liliflora.dto.ProductResponseDto;
import com.liliflora.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<ProductResponseDto.ProductDto> productList() {
        return productRepository.findAllByOrderByProductIdDesc().stream()
                .map(ProductResponseDto.ProductDto::fromEntity)
                .collect(Collectors.toList());
    }

}
