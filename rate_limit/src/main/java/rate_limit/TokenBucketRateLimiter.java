package rate_limit;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 令牌桶限流器
 */
@Slf4j
public class TokenBucketRateLimiter {

    private final int bucketCapacity; // 令牌桶的容量

    private final int refillRate; // 令牌生成速率（个令牌生成的时间间隔，毫秒）

    private AtomicInteger tokens; // 当前令牌数

    /**
     * 构造函数
     * @param bucketCapacity 令牌桶的容量
     * @param refillRate 令牌生成速率（每个令牌生成的时间间隔，毫秒）
     * @param threadPoolSize 请求队列的线程池大小
     */
    public TokenBucketRateLimiter(int bucketCapacity, int refillRate, int... threadPoolSize) {
        this.bucketCapacity = bucketCapacity;
        this.refillRate = refillRate;
        this.tokens = new AtomicInteger(0);

        int poolSize =1; // 默认线程池大小为1
        if (threadPoolSize.length > 0) {
            poolSize = threadPoolSize[0];
        }

        startRefilling(poolSize); // 启动定期生成令牌的线程
    }

    /**
     * 定期为令牌桶添加令牌
     * @param threadPoolSize 线程池大小
     */
    private void startRefilling(int threadPoolSize) {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(threadPoolSize);

        // 添加 JVM 关闭钩子，确保线程池在程序终止时关闭
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting down ScheduledExecutorService...");
            executor.shutdown();
            try {
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
        }));

        executor.scheduleAtFixedRate(() -> {
            int currentTokens = tokens.get();
            if (currentTokens < bucketCapacity) {
                int newTokens = Math.min(bucketCapacity, currentTokens + 1);
                tokens.set(newTokens);
                System.out.println(LocalDateTime.now() + "\t\t新增令牌，当前令牌数：" + newTokens + "\tat " + System.currentTimeMillis());
            }
        }, 0, refillRate, TimeUnit.MILLISECONDS); // 每秒填充令牌
    }

    /**
     * 尝试获取令牌
     * @return 是否成功获取令牌
     */
    public boolean tryAcquire() {
        int currentTokens = tokens.get();
        if (currentTokens > 0) {
            if (tokens.compareAndSet(currentTokens, currentTokens - 1)) {
                System.out.println(LocalDateTime.now() + "\t\t消耗令牌，剩余令牌数：" + (currentTokens - 1) + "\tat " + System.currentTimeMillis());
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) throws InterruptedException {
        TokenBucketRateLimiter rateLimiter = new TokenBucketRateLimiter(10, 500, 2); // 容量为10，每500毫秒生成1个令牌

        // 模拟10个请求
        for (int i = 0; i < 10; i++) {
            boolean allowed = rateLimiter.tryAcquire();
            if (allowed) {
                log.info("\033[34m{}\033[0m\t: Request {}\u001B[32m allowed\u001B[0m at {}", LocalDateTime.now(), i + 1, System.currentTimeMillis());
            } else {
                log.info("{\033[34m{}\033[0m\t: Request {}\u001B[31m denied\u001B[0m at {}", LocalDateTime.now(), i + 1, System.currentTimeMillis());
            }

            int sleepTimeInMills = (int)(Math.random()*300) + 100; // 随机间隔 [100, 400）毫秒
            Thread.sleep(sleepTimeInMills); // 模拟请求间隔
        }
    }

}
