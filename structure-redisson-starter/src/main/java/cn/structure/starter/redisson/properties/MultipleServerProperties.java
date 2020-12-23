package cn.structure.starter.redisson.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.redisson.config.ReadMode;
import org.redisson.config.SubscriptionMode;

/**
 * <p>
 *     多节点配置属性
 * </p>
 * @author chuck
 * @since 2020-12-23
 * @version 1.0.1
 */
@Getter
@Setter
@ToString
public class MultipleServerProperties {
    /**
     * <p>
     *     负载均衡算法类的选择
     * </p>
     * <pre>
     * 默认值： org.redisson.connection.balancer.RoundRobinLoadBalancer
     * 在使用多个Elasticache Redis服务节点的环境里，可以选用以下几种负载均衡方式选择一个节点：
     * org.redisson.connection.balancer.WeightedRoundRobinBalancer - 权重轮询调度算法
     * org.redisson.connection.balancer.RoundRobinLoadBalancer - 轮询调度算法
     * org.redisson.connection.balancer.RandomLoadBalancer - 随机调度算法
     * </pre>
     */
    private String loadBalancer = "org.redisson.connection.balancer.RoundRobinLoadBalancer";

    /**
     * <p>
     *     从节点最小空闲连接数
     * </p>
     * <pre>
     *     默认值：32
     *     多从节点的环境里，每个 从服务节点里用于普通操作（非 发布和订阅）的最小保持连接数（长连接）。
     *     长期保持一定数量的连接有利于提高瞬时读取反映速度。
     * </pre>
     */
    private Integer slaveConnectionMinimumIdleSize = 32;
    /**
     * <p>
     *     从节点连接池大小
     * </p>
     * <pre>
     *
     * </pre>
     */
    private Integer slaveConnectionPoolSize = 64;
    /**
     * 尝试每一个这样的超时连接到断开连接的Redis服务器
     */
    private Integer failedSlaveReconnectionInterval = 3000;
    private Integer failedSlaveCheckInterval = 180000;
    private Integer masterConnectionMinimumIdleSize = 32;
    private Integer masterConnectionPoolSize = 64;
    private ReadMode readMode = ReadMode.SLAVE;
    private SubscriptionMode subscriptionMode = SubscriptionMode.SLAVE;
    private Integer subscriptionConnectionMinimumIdleSize = 1;
    private Integer subscriptionConnectionPoolSize = 50;
    private Long dnsMonitoringInterval = 5000L;

}
