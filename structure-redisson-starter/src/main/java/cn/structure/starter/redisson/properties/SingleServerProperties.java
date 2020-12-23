package cn.structure.starter.redisson.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>
 *     单节点配置属性
 * </p>
 * @author chuck
 * @since 2020-12-23
 * @version 1.0.1
 */
@Setter
@Getter
@ToString
public class SingleServerProperties {
    /**
     * 单节点redis的地址
     */
    private String address;
    /**
     * 订阅连接的最小空闲连接数
     */
    private Integer subscriptionConnectionMinimumIdleSize = 1;
    /**
     * 订阅连接池大小
     */
    private Integer subscriptionConnectionPoolSize = 50;
    /**
     * 最小空闲连接数
     */
    private Integer connectionMinimumIdleSize = 32;
    /**
     * 连接池大小
     */
    private Integer connectionPoolSize = 64;
    /**
     * DNS监控间隔，单位：毫秒
     */
    private Long dnsMonitoringInterval = 5000L;
}
