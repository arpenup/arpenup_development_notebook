package rate_limit;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

import java.time.LocalDateTime;

/**
 * 滑动窗口限流器
 */
@Slf4j
public class SlidingWindowRateLimiter {

    private final Jedis jedis; // Redis 客户端

    private final String key; // 限流数据的 Redis键名

    private final long windowSizeInMilliseconds; // 滑动窗口大小，单位：毫秒

    private final int maxRequests; // 允许通过的最大请求数

    /**
     * 构造函数
     * @param jedis Redis 客户端实例
     * @param key Redis 键名，用于存储限流数据
     * @param windowSizeInMilliseconds 滑动窗口大小，单位：秒
     * @param maxRequests 允许通过的最大请求数
     */
    public SlidingWindowRateLimiter(Jedis jedis, String key, long windowSizeInMilliseconds, int maxRequests) {
        this.jedis = jedis;
        this.key = key;
        this.windowSizeInMilliseconds = windowSizeInMilliseconds;
        this.maxRequests = maxRequests;
    }

    /**
     * 尝试获取许可
     * @return true 允许通过，false 被限流
     */
    public boolean tryAcquire() {
        long now = System.currentTimeMillis(); // 当前时间
        long before = now - windowSizeInMilliseconds; // 窗口开始时间

        // 移除窗口之前的记录
        jedis.zremrangeByScore(key, 0, before);

        // 查询当前窗口内的请求数量
        long currentRequests = jedis.zcard(key);

        if (currentRequests < maxRequests) {
            // 添加当前请求的时间戳
            jedis.zadd(key, now, String.valueOf(now));
            return true;
        }

        return false;
    }

    /**
     * 尝试获取许可
     * 使用LUA脚本，解决高并发下的原子性问题
     * @return true 允许通过，false 被限流
     */
    public boolean tryAcquire4Concurrency() {
        long now = System.currentTimeMillis(); // 当前时间

        String luaScript = "local window_start_time = ARGV[1] -ARGV[3] " +
                " redis.call('ZREMRANGEBYSCORE',KEYS[1],'-inf',window_start_time) " +
                " local now_request = redis.call('ZCARD',KEYS[1]) " +
                " if now_request < tonumber(ARGV[2]) then " +
                " redis.call('ZADD',KEYS[1],ARGV[1],ARGV[1]) " +
                "     return 1 " +
                "else " +
                "      return 0 " +
                " end ";
        Object result = jedis.eval(luaScript, 1, key, String.valueOf(now), String.valueOf(maxRequests), String.valueOf(windowSizeInMilliseconds));
        return (long) result == 1;
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("========== 滑动窗口限流 ==========");

        try (Jedis jedis = new Jedis("172.23.84.30", 6379)) {

            jedis.auth("arpen@2025"); // 设置密码
            jedis.select(0); // 选择数据库

            String rateLimiterKey = "my_api:rate_limiter"; // 主键名

            long windowSizeInMilliSeconds = 1000; // 窗口大小，单位：毫秒
            int maxRequests = 3; // 窗口允许的最大请求数

            SlidingWindowRateLimiter limiter = new SlidingWindowRateLimiter(jedis, rateLimiterKey, windowSizeInMilliSeconds, maxRequests);

            for (int i = 0; i < 10; i++) {
                if (limiter.tryAcquire4Concurrency()) {
                    log.info("\033[34m{}\033[0m\t: Request {}\u001B[32m allowed\u001B[0m at {}", LocalDateTime.now(), i + 1, System.currentTimeMillis());
                } else {
                    log.info("{\033[34m{}\033[0m\t: Request {}\u001B[31m denied\u001B[0m at {}", LocalDateTime.now(), i + 1, System.currentTimeMillis());
                }

                int sleepTimeInMills = (int)(Math.random()*300) + 100; // 随机间隔 [100, 400）毫秒
                Thread.sleep(sleepTimeInMills); // 模拟请求间隔
            }
        }

    }

}
