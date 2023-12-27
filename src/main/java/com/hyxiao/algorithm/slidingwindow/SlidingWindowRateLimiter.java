package com.hyxiao.algorithm.slidingwindow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlidingWindowRateLimiter {

    public static final Logger logger = LoggerFactory.getLogger(SlidingWindowRateLimiter.class);

    //  总时间滑动窗口大小，单位毫秒
    long windowSize;

    //  分片窗口的数量，即滑动窗口分成的小窗口数量
    int shardNum;

    //  在总时间滑动窗口内允许通过的最大请求数
    int maxRequestCount;

    //  数组，存储每个小窗口内的请求数
    int[] shardRequestCount;

    //  请求总数，是当前所有小窗口请求数的总和
    int totalCount;

    //  当前的小窗口索引下标
    int shardId;

    //  每个小窗口的大小，单位毫秒
    long tinyWindowSize;

    //  当前窗口的右边界时间戳
    long windowBorder;

    public SlidingWindowRateLimiter(long windowSize, int shardNum, int maxRequestCount) {
        this.windowSize = windowSize;
        this.shardNum = shardNum;
        this.maxRequestCount = maxRequestCount;
        //  用于存储每个小窗口的请求计数，数组的长度就是小窗口的数量
        this.shardRequestCount = new int[shardNum];
        //  整个窗口大小除以小窗口数量，代表每个小窗口代表的时间维度
        this.tinyWindowSize = windowSize / shardNum;
        //  窗口的起始时间
        this.windowBorder = System.currentTimeMillis();
    }

    /**
     * shardId = (++shardId) % shardNum; 通过递增 shardId 并对 shardNum 取模来更新当前的小窗口索引
     * totalCount -= shardRequestCount[shardId];   每次窗口滑动时，都会减去最旧的小窗口（即将被滑出的小窗口）中的计数
     * shardRequestCount[shardId] = 0; 重置小窗口的计数器
     * windowBorder += tinyWindowSize; 将窗口右边界向前移动，跳过已经过期的小窗口
     *
     */
    public synchronized boolean tryAcquire() {
        long currentTime = System.currentTimeMillis();
        if (windowBorder < currentTime) {
            logger.info("window reset");
            do {
                logger.info("window sliding");
                shardId = (++shardId) % shardNum;
                totalCount -= shardRequestCount[shardId];
                shardRequestCount[shardId] = 0;
                windowBorder += tinyWindowSize;
            } while (windowBorder < currentTime);
        }

        if (totalCount < maxRequestCount) {
            shardRequestCount[shardId]++;
            totalCount++;
            logger.info("tryAcquire success: shardId:{}, shardRequestCount:{}, totalCount:{}", shardId, shardRequestCount[shardId], totalCount);
            return true;
        } else {
            logger.info("tryAcquire fail");
            return false;
        }

    }

}
