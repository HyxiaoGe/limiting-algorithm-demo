package com.hyxiao.algorithm.slidingwindow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SlidingWindowRateLimiterTest {

    public static final Logger logger = LoggerFactory.getLogger(SlidingWindowRateLimiterTest.class);


    public static void main(String[] args) {

        //  创建一个3秒钟的滑动窗口，分成3个小窗口，总请求数不超过150个
        SlidingWindowRateLimiter rateLimiter = new SlidingWindowRateLimiter(3000, 3, 150);

        //  使用线程池模拟高并发请求
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        //  记录成功通过的请求
        AtomicInteger successfulRequests = new AtomicInteger(0);

        //  记录被限流的请求
        AtomicInteger limitedRequests = new AtomicInteger(0);

        for (int i = 0; i < 1000; i++) {
            executorService.execute(() -> {
                if (rateLimiter.tryAcquire()) {
                    successfulRequests.incrementAndGet();
                } else {
                    limitedRequests.incrementAndGet();
                }
            });
            //  每发起一个请求休眠10毫秒，模拟请求间隔
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        //  关闭线程池
        executorService.shutdown();
        try {
            //  等待所有任务完成
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
        // 输出测试结果
        logger.info("Successful requests: {}", successfulRequests.get());
        logger.info("Limited requests: {}", limitedRequests.get());
    }

}
