package com.liliflora.entity;

import com.liliflora.repository.ProductRepository;
import com.liliflora.service.OrderService;
import com.liliflora.service.ProductService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.acls.model.NotFoundException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

class ProductTest {
    private OrderService orderService;
    private ProductService productService;
    private ProductRepository productRepository;

    private final int threadCount = 100;
    private final long productId = 1L;
    private final int orderProductQuantity = 1;

    private ExecutorService executorService;
    private CountDownLatch countDownLatch;

    @BeforeEach
    public void beforeEach() {
        executorService = Executors.newFixedThreadPool(threadCount);
        countDownLatch = new CountDownLatch(threadCount);
    }

    @DisplayName("재고 감소 - 동시에 100개 요청")
    @Test
    void 재고감소_동시요청() throws InterruptedException {
        // Given

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("product not found"));

        // When
        IntStream.range(0, threadCount).forEach(e -> executorService.submit(() -> {
                    try {
//                        Product product = productRepository.findById(productId)
//                                .orElseThrow(() -> new NotFoundException("product not found"));

                        product.decreaseQuantity(orderProductQuantity);
                    } finally {
                        countDownLatch.countDown();
                    }
                }
        ));

        countDownLatch.await();

//        Product product2 = productRepository.findById(productId)
//                .orElseThrow(() -> new NotFoundException("product not found"));

        final int afterQuantity = product.getQuantity();
        System.out.println("afterQuantity: " + afterQuantity);
        Assertions.assertThat(afterQuantity).isZero();
    }
}