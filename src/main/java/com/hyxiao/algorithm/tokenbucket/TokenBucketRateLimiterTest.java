package com.hyxiao.algorithm.tokenbucket;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class TokenBucketRateLimiterTest {

    public static final Logger logger = LoggerFactory.getLogger(TokenBucketRateLimiterTest.class);

    private void acquire() {
        //  每秒固定生成5个令牌
        RateLimiter rateLimiter = RateLimiter.create(1);
        for (int i = 0; i < 10; i++) {
            double time = rateLimiter.acquire();
            logger.info("等待时间：{}s", time);
        }
    }

    private void acquireSmoothly() {
        RateLimiter rateLimiter = RateLimiter.create(5, 3, TimeUnit.SECONDS);
        long startTimeStamp = System.currentTimeMillis();
        for (int i = 0; i < 15; i++) {
            double time = rateLimiter.acquire();
            logger.info("等待时间：{}s，总时间：{}ms", time, System.currentTimeMillis() - startTimeStamp);
        }
    }

    public static void main(String[] args) {

//        new TokenBucketRateLimiterTest().acquire();
        new TokenBucketRateLimiterTest().acquireSmoothly();

    }

}
