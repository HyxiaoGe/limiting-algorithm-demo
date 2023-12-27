package com.hyxiao.algorithm.leakybucket;

import com.hyxiao.algorithm.slidingwindow.SlidingWindowRateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class LeakyBucketRateLimiterTest {

    public static final Logger logger = LoggerFactory.getLogger(LeakyBucketRateLimiterTest.class);

    public static void main(String[] args) {

        //  桶容量为100，漏水速率为10个请求
        LeakyBucketRateLimiter rateLimiter = new LeakyBucketRateLimiter(100, 10);

        //  使用线程池模拟高并发请求
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        //  记录成功通过的请求
        AtomicInteger successfulRequests = new AtomicInteger(0);

        //  记录被限流的请求
        AtomicInteger limitedRequests = new AtomicInteger(0);

        for (int i = 0; i < 100; i++) {
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
