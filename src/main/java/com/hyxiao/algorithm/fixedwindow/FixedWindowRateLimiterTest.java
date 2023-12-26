package com.hyxiao.algorithm.fixedwindow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class FixedWindowRateLimiterTest {

    public static final Logger logger = LoggerFactory.getLogger(FixedWindowRateLimiterTest.class);

    public static void main(String[] args) {

        //  设置时间窗口大小为3秒，最大请求数为150
        FixedWindowRateLimiter rateLimiter = new FixedWindowRateLimiter(3000, 150);

        //  使用一个线程池来模拟高并发请求
        ExecutorService executorService = Executors.newFixedThreadPool(20);

        //  记录成功通过的请求
        AtomicInteger successfulRequests = new AtomicInteger(0);

        //  记录被限流的请求
        AtomicInteger limitedRequests = new AtomicInteger(0);

        //  模拟1000个请求
        for (int i = 0; i < 1000; i++) {
            executorService.execute(() -> {
                if (rateLimiter.tryAcquire()) {
                    successfulRequests.incrementAndGet();
                } else {
                    limitedRequests.incrementAndGet();
                }
            });
        }

        //  关闭线程池
        executorService.shutdown();
        try {
            // 等待所有任务完成
            executorService.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 输出测试结果
        logger.info("Successful requests: " + successfulRequests.get());
        logger.info("Limited requests: " + limitedRequests.get());

    }

}
