package com.liliflora.service;

import com.liliflora.entity.Product;
import com.liliflora.entity.Stock;
import com.liliflora.repository.ProductRepository;
import com.liliflora.repository.StockRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockService {

    private final ProductRepository productRepository;
    private final StockRepository stockRepository;

    @Scheduled(fixedRate = 60 * 1000)
    @Transactional
    public void syncProductQuantity() {
        Iterable<Stock> stocks = stockRepository.findAll();
        for (Stock stock : stocks) {
            if (stock != null) {
                long productId = stock.getStockId();
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new NotFoundException("Product not found " + productId));

                product.syncQuantity(stock.getQuantity());
            }
        }
    }
}
