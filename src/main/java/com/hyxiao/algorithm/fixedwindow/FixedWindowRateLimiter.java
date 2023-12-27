package com.hyxiao.algorithm.fixedwindow;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class FixedWindowRateLimiter {

    public static final Logger logger = LoggerFactory.getLogger(FixedWindowRateLimiter.class);

    //  时间窗口大小，单位毫秒
    long windowSize;

    //  在一个时间窗口内允许通过的最大请求数
    int maxRequestCount;

    //  当前窗口通过的请求数
    AtomicInteger counter = new AtomicInteger(0);

    //  窗口右边界，即当前窗口的结束时间
    long windowBorder;

    public FixedWindowRateLimiter(long windowSize, int maxRequestCount) {
        this.windowSize = windowSize;
        this.maxRequestCount = maxRequestCount;
        //  windowBorder 设置为当前时间加上窗口大小，这标志着第一个时间窗口的结束时间
        this.windowBorder = System.currentTimeMillis() + windowSize;
    }

    public synchronized boolean tryAcquire() {
        long currentTime = System.currentTimeMillis();
        //  检查当前时间是否超过了时间窗口的边界
        if (windowBorder < currentTime) {
            logger.info("window reset");
            //  do-while 循环更新窗口结界，直到它超出当前时间。
            //  这确保了，即使因为某些原因（如服务停止），错过了一个或多个窗口，窗口边界也会被正确设置。
            do {
                windowBorder += windowSize;
            } while (windowBorder < currentTime);
            counter = new AtomicInteger(0);
        }

        //  检查当前窗口内的请求数是否小于允许的最大请求数
        if (counter.intValue() < maxRequestCount) {
            counter.incrementAndGet();
            logger.info("tryAcquire success, No.{}", counter.intValue());
            return true;
        } else {
            logger.info("tryAcquire fail");
            return false;
        }

    }

}
