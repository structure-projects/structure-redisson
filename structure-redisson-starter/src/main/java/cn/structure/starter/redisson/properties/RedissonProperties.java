package cn.structure.starter.redisson.properties;

import cn.structure.starter.redisson.enumerate.LockModelEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.redisson.config.SslProvider;
import org.redisson.config.TransportMode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 *     Redisson配置
 * </p>
 * @author chuck
 * @since 2020-12-23
 * @version 1.0.1
 */
@Getter
@Setter
@ToString
@Configuration
@ConfigurationProperties(prefix = "structure.redisson")
public class RedissonProperties {
    /**
     * redisson的模式
     * <ul>
     *     <li>single 单机模式</li>
     *     <li>sentinel 哨兵模式</li>
     *     <li>cluster 集群模式</li>
     *     <li>replicated 云托管模式</li>
     *     <li>master-slave 主从模式</li>
     * </ul>
     */
    private String model;
    /*初始化 redisson 原有配置均有默认值不需要配置 */
    private Integer threads;
    private Integer nettyThreads;
    private String codec = "org.redisson.codec.JsonJacksonCodec";
    private TransportMode transportMode = TransportMode.NIO;
    private Integer database = 0;

    private Integer idleConnectionTimeout = 10000;
    private Integer pingTimeout = 1000;
    private Integer connectTimeout = 10000;
    private Integer timeout = 3000;
    private Integer retryAttempts = 3;
    private Integer retryInterval = 1500;
    private String password;
    private Integer subscriptionsPerConnection = 5;
    private String clientName;
    private Boolean sslEnableEndpointIdentification = true;
    private SslProvider sslProvider = SslProvider.JDK;
    private String sslTruststore;
    private String sslTruststorePassword;
    private String sslKeystore;
    private String sslKeystorePassword;
    private Integer pingConnectionInterval = 0;
    private Boolean keepAlive = false;
    private Boolean tcpNoDelay = false;

    private Boolean referenceEnabled = true;
    private Long lockWatchdogTimeout = 30000L;
    private Boolean keepPubSubOrder = true;
    private Boolean decodeInExecutor = false;
    private Boolean useScriptCache = false;
    private Integer minCleanUpDelay = 5;
    private Integer maxCleanUpDelay = 1800;

    /**
     * 单体模式的配置
     */
    @NestedConfigurationProperty
    private SingleServerProperties single;

    /**
     * 哨兵模式的配置
     */
    @NestedConfigurationProperty
    private SentinelProperties sentinel;

    /**
     * 集群模式的配置
     */
    @NestedConfigurationProperty
    private ClusterProperties cluster;

    /**
     * 云托管模式的配置
     */
    @NestedConfigurationProperty
    private ReplicatedProperties replicated;

    /**
     * 主从模式的配置
     */
    @NestedConfigurationProperty
    private MasterSlaveProperties masterSlave;

    /**
     * 锁的模式 如果不设置 单个key默认可重入锁 多个key默认联锁
     */
    private LockModelEnum lockModel;
    /**
     * 等待加锁超时时间 -1一直等待
     */
    private Long attemptTimeout = 10000L;
    /**
     *  数据缓存时间 默认30分钟
     */
    private Long dataValidTime = 1000 * 60 * 30L;

    @NestedConfigurationProperty
    private CacheProperties cache;

}
