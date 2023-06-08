package rebue.wheel.vertx.config;

import lombok.Data;

@Data
public class ZkClientProperties {

    /**
     * 连接字符串
     */
    private String                connectString       = System.getProperty("curator-default-connect-string", "127.0.0.1:2181");
    /**
     * 会话超时毫秒数
     */
    private Integer               sessionTimeoutMs    = Integer.getInteger("curator-default-session-timeout", 60 * 1000);
    /**
     * 连接超时毫秒数
     */
    private Integer               connectionTimeoutMs = Integer.getInteger("curator-default-connection-timeout", 15 * 1000);
    /**
     * 重试策略
     */
    private RetryPolicyProperties retryPolicy         = new RetryPolicyProperties();

    /**
     * 重试策略
     * 计算公式: 当前sleep时间 = baseSleepTimeMs * Math.max(1, random.nextInt(1 << (retryCount + 1)))
     */
    @Data
    public static class RetryPolicyProperties {
        /**
         * 初始的sleep时间，用于计算之后的每次重试的sleep时间
         */
        private Integer baseSleepTimeMs = Integer.getInteger("curator-default-retry-policy-base-sleep-time-Ms", 5000);
        /**
         * 最大重试次数(最多只能设置到29)
         */
        private Integer maxRetries      = Integer.getInteger("curator-default-retry-policy-max-retries", 29);
        /**
         * 最大sleep时间，如果上述的当前sleep计算出来比这个大，那么sleep用这个时间(这里设为为1小时)
         */
        private Integer maxSleepMs      = Integer.getInteger("curator-default-retry-policy-max-sleep-ms", Integer.MAX_VALUE);
    }

}
