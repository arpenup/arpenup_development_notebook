package rate_limit;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.concurrent.*;

/**
 * 漏桶算法限流器
 */
@Slf4j
public class LeakyBucketRateLimiter {

    private final int leakRate; // 请求处理速率（每个请求处理的时间间隔，毫秒）

    private final BlockingQueue<Runnable> bucket; // 请求队列

    /**
     * 构造函数
     * @param bucketCapacity 漏桶容量
     * @param leakRate 请求处理速率（每个请求处理的时间间隔，毫秒）
     * @param threadPoolSize 请求队列的线程池大小
     */
    public LeakyBucketRateLimiter(int bucketCapacity, int leakRate, int... threadPoolSize) {
        this.leakRate = leakRate;
        this.bucket = new LinkedBlockingQueue<>(bucketCapacity);

        int poolSize =1; // 默认线程池大小为1
        if (threadPoolSize.length > 0) {
            poolSize = threadPoolSize[0];
        }

        startConsuming(poolSize); // 启动定期消费请求的线程
    }

    /**
     * 启动请求的定期消费任务
     * @param threadPoolSize 线程池大小
     */
    private void startConsuming(int threadPoolSize) {
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
            Runnable request = bucket.poll();
            if (request != null) {
                request.run();
            }
        }, 0, leakRate, TimeUnit.MILLISECONDS); // 消费间隔

    }

    /**
     * 尝试获取许可
     * @return true 允许通过，false 被限流
     */
    public boolean tryAcquire(Runnable request) {
        return bucket.offer(request);
    }

    public static void main(String[] args) throws InterruptedException {
        LeakyBucketRateLimiter rateLimiter = new LeakyBucketRateLimiter(5, 500, 2);

        // 模拟10个请求
        for (int i = 0; i < 10; i++) {
            int finalI = i + 1;
            long startTimeMillis = System.currentTimeMillis();
            boolean tried = rateLimiter.tryAcquire(() -> {
                int sleepTimeInMills = (int)(Math.random()*1400) + 100; // 随机间隔 [100, 1500）毫秒
                try {
                    Thread.sleep(sleepTimeInMills);
                } catch (InterruptedException e) {
                    System.out.println("处理请求：[" + finalI + "] 失败  add at " + startTimeMillis + "\tstart at " + System.currentTimeMillis() + "\tsleep " + sleepTimeInMills + "ms");
                    throw new RuntimeException(e);
                }
                System.out.println("处理请求：[" + finalI + "] 成功  add at " + startTimeMillis + "\tstart at " + System.currentTimeMillis() + "\tsleep " + sleepTimeInMills + "ms");
            });
            if (tried) {
                log.info("\033[34m{}\033[0m\t\t: Request {}\u001B[32m allowed\u001B[0m at {}", LocalDateTime.now(), i + 1, startTimeMillis);
            } else {
                log.info("\033[34m{}\033[0m\t\t: Request {}\u001B[31m denied\u001B[0m at {}", LocalDateTime.now(), i + 1, startTimeMillis);
            }

            int sleepTimeInMills = (int)(Math.random()*300) + 100; // 随机间隔 [100, 400）毫秒
            Thread.sleep(sleepTimeInMills); // 模拟请求间隔
        }
    }

}
