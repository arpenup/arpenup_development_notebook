package rate_limit;


import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * 固定窗口限流器
 */
@Slf4j
public class FixedWindowRateLimiter {

    private final long windowSizeInMilliseconds; // 滑动窗口大小，单位：毫秒

    private final int maxRequests; // 允许通过的最大请求数

    private long lastRequestTimestamp; // 上次请求的时间戳

    private int counter; // 当前请求数

    /**
     * 构造函数
     * @param windowSizeInMilliseconds 滑动窗口大小，单位：毫秒
     * @param maxRequests 允许通过的最大请求数
     * @param lastRequestTimestamp 上次请求的时间戳
     * @param counter 当前请求数
     */
    public FixedWindowRateLimiter(long windowSizeInMilliseconds, int maxRequests, long lastRequestTimestamp, int counter) {
        this.windowSizeInMilliseconds = windowSizeInMilliseconds;
        this.maxRequests = maxRequests;
        this.lastRequestTimestamp = lastRequestTimestamp;
        this.counter = counter;
    }

    /**
     * 尝试获取许可
     * @return true 允许通过，false 被限流
     */
    public boolean tryAcquire() {
        long now = System.currentTimeMillis(); // 当前时间

        // 若请求超出当前时间窗口
        if (now - lastRequestTimestamp > windowSizeInMilliseconds) {
            counter = 0; // 重置计数器
            lastRequestTimestamp = now;
        }

        if (counter < maxRequests) {
            counter++; // 增加计数器
            return true; // 允许通过
        } else {
            return false; // 被限流
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("========== 固定窗口限流 ==========");

        long windowSizeInMilliSeconds = 1000; // 窗口大小，单位：毫秒
        int maxRequests = 3; // 窗口允许的最大请求数

        FixedWindowRateLimiter rateLimiter = new FixedWindowRateLimiter(windowSizeInMilliSeconds, maxRequests, System.currentTimeMillis(), 0);

        for (int i = 0; i < 10; i++) {
            if (rateLimiter.tryAcquire()) {
                log.info("\033[34m{}\033[0m\t: Request {}\u001B[32m allowed\033[0m", LocalDateTime.now(), i + 1);
            } else {
                log.info("\033[34m{}\033[0m\t: Request {}\033[31m denied\033[0m", LocalDateTime.now(), i + 1);
            }

            int sleepTimeInMills = (int)(Math.random()*300) + 100; // 随机间隔 [100, 400）毫秒
            Thread.sleep(sleepTimeInMills); // 模拟请求间隔
        }
    }

}
