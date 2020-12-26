package cn.structure.starter.redisson.configuration;

import cn.structure.starter.redisson.aop.LockAop;
import cn.structure.starter.redisson.aop.RedisCacheAop;
import cn.structure.starter.redisson.properties.*;
import cn.structure.starter.redisson.utils.DistributedLockerImpl;
import cn.structure.starter.redisson.utils.IDistributedLocker;
import cn.structure.starter.redisson.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.api.RedissonRxClient;
import org.redisson.client.codec.Codec;
import org.redisson.config.*;
import org.redisson.connection.balancer.LoadBalancer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.net.URI;
import java.net.URISyntaxException;

import static cn.structure.starter.redisson.utils.StringUtil.prefixAddress;


/**
 * <p>
 *     redisson配置
 * </p>
 * @author chuck
 * @since 2020-12-23
 * @version 1.0.1
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(value = RedissonProperties.class)
@ConditionalOnClass(RedissonProperties.class)
public class RedissonConfiguration {

    @Resource
    private RedissonProperties redissonProperties;

    @Bean
    @ConditionalOnMissingBean(LockAop.class)
    public LockAop lockAop() {
        return new LockAop();
    }

    @Bean
    @ConditionalOnMissingBean(RedisCacheAop.class)
    public RedisCacheAop redisCacheAop(){
        return new RedisCacheAop();
    }

    @Bean
    @ConditionalOnClass(RedissonClient.class)
    public IDistributedLocker distributedLocker(RedissonClient redissonClient){
        return new DistributedLockerImpl(redissonClient);
    }

    /**
     * 注入config
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(Config.class)
    public Config config(){
        Config config = new Config();
        try {
            config.setCodec((Codec) Class.forName(redissonProperties.getCodec()).newInstance());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        config.setReferenceEnabled(redissonProperties.getReferenceEnabled());
        config.setLockWatchdogTimeout(redissonProperties.getLockWatchdogTimeout());
        config.setKeepPubSubOrder(redissonProperties.getKeepPubSubOrder());
        config.setDecodeInExecutor(redissonProperties.getDecodeInExecutor());
        config.setUseScriptCache(redissonProperties.getUseScriptCache());
        config.setMinCleanUpDelay(redissonProperties.getMinCleanUpDelay());
        config.setMaxCleanUpDelay(redissonProperties.getMaxCleanUpDelay());
        config.setTransportMode((redissonProperties.getTransportMode() == null)? TransportMode.NIO : redissonProperties.getTransportMode());
        return config;
    }

    /**
     * 注入 RedissonReactiveClient 单体模式
     * @param config
     * @return
     */
    @Bean
    @ConditionalOnClass(Config.class)
    @ConditionalOnProperty(value = "structure.redisson.model", havingValue = "single")
    @ConditionalOnMissingBean(RedissonReactiveClient.class)
    public RedissonReactiveClient redissonReactiveClientBySingle(Config config){
        redissonSingleConfig(config);
        return Redisson.createReactive(config);
    }

    /**
     * 注入 RedissonClient 单体模式
     * @param config
     * @return
     */
    @Bean
    @ConditionalOnClass(Config.class)
    @ConditionalOnProperty(value = "structure.redisson.model", havingValue = "single")
    @ConditionalOnMissingBean(RedissonClient.class)
    public RedissonClient redissonClientBySingle(Config config) {
        redissonSingleConfig(config);
        return Redisson.create(config);
    }

    /**
     * 注入 RedissonRxClient 单体模式
     * @param config
     * @return
     */
    @Bean
    @ConditionalOnClass(Config.class)
    @ConditionalOnProperty(value = "structure.redisson.model", havingValue = "single")
    @ConditionalOnMissingBean(RedissonRxClient.class)
    public RedissonRxClient redissonRxClientBySingle(Config config){
        redissonSingleConfig(config);
        return Redisson.createRx(config);
    }

    /**
     * 注入 RedissonReactiveClient 哨兵模式
     * @param config
     * @return
     */
    @Bean
    @ConditionalOnClass(Config.class)
    @ConditionalOnProperty(value = "structure.redisson.model",havingValue = "sentinel")
    @ConditionalOnMissingBean(RedissonReactiveClient.class)
    public RedissonReactiveClient redissonReactiveClientBySentinel(Config config){
        redissonSentinelConfig(config);
        return Redisson.createReactive(config);
    }

    /**
     * 注入 RedissonClient 哨兵模式
     * @param config
     * @return
     */
    @Bean
    @ConditionalOnClass(Config.class)
    @ConditionalOnProperty(value = "structure.redisson.model",havingValue = "sentinel")
    @ConditionalOnMissingBean(RedissonClient.class)
    public RedissonClient redissonClientBySentinel(Config config){
        redissonSentinelConfig(config);
        return Redisson.create(config);
    }

    /**
     * 注入 RedissonRxClient 哨兵模式
     * @param config
     * @return
     */
    @Bean
    @ConditionalOnClass(Config.class)
    @ConditionalOnProperty(value = "structure.redisson.model", havingValue = "sentinel")
    @ConditionalOnMissingBean(RedissonRxClient.class)
    public RedissonRxClient redissonRxClientBySentinel(Config config){
        redissonSentinelConfig(config);
        return Redisson.createRx(config);
    }



    /**
     * 注入 RedissonReactiveClient 集群模式
     * @param config
     * @return
     */
    @Bean
    @ConditionalOnClass(Config.class)
    @ConditionalOnProperty(value = "structure.redisson.model",havingValue = "cluster")
    @ConditionalOnMissingBean(RedissonReactiveClient.class)
    public RedissonReactiveClient redissonReactiveClientByCluster(Config config){
        redissonClusterConfig(config);
        return Redisson.createReactive(config);
    }

    /**
     * 注入 RedissonClient 集群模式
     * @param config
     * @return
     */
    @Bean
    @ConditionalOnClass(Config.class)
    @ConditionalOnProperty(value = "structure.redisson.model",havingValue = "cluster")
    @ConditionalOnMissingBean(RedissonClient.class)
    public RedissonClient redissonClientByCluster(Config config){
        redissonClusterConfig(config);
        return Redisson.create(config);
    }

    /**
     * 注入 RedissonRxClient 集群模式
     * @param config
     * @return
     */
    @Bean
    @ConditionalOnClass(Config.class)
    @ConditionalOnProperty(value = "structure.redisson.model", havingValue = "cluster")
    @ConditionalOnMissingBean(RedissonRxClient.class)
    public RedissonRxClient redissonRxClientByCluster(Config config){
        redissonClusterConfig(config);
        return Redisson.createRx(config);
    }


    /**
     * 注入 RedissonReactiveClient 托管模式
     * @param config
     * @return
     */
    @Bean
    @ConditionalOnClass(Config.class)
    @ConditionalOnProperty(value = "structure.redisson.model",havingValue = "replicated")
    @ConditionalOnMissingBean(RedissonReactiveClient.class)
    public RedissonReactiveClient redissonReactiveClientByReplicated(Config config){
        redissonReplicatedConfig(config);
        return Redisson.createReactive(config);
    }

    /**
     * 注入 RedissonClient 托管模式
     * @param config
     * @return
     */
    @Bean
    @ConditionalOnClass(Config.class)
    @ConditionalOnProperty(value = "structure.redisson.model",havingValue = "replicated")
    @ConditionalOnMissingBean(RedissonClient.class)
    public RedissonClient redissonClientByReplicated(Config config){
        redissonReplicatedConfig(config);
        return Redisson.create(config);
    }

    /**
     * 注入 RedissonRxClient 云托管模式
     * @param config
     * @return
     */
    @Bean
    @ConditionalOnClass(Config.class)
    @ConditionalOnProperty(value = "structure.redisson.model", havingValue = "replicated")
    @ConditionalOnMissingBean(RedissonRxClient.class)
    public RedissonRxClient redissonRxClientByReplicated(Config config){
        redissonReplicatedConfig(config);
        return Redisson.createRx(config);
    }

    /**
     * 注入 RedissonRxClient 主从模式
     * @param config
     * @return
     */
    @Bean
    @ConditionalOnClass(Config.class)
    @ConditionalOnProperty(value = "structure.redisson.model",havingValue = "master-slave")
    @ConditionalOnMissingBean(RedissonReactiveClient.class)
    public RedissonReactiveClient redissonReactiveClientByMasterSlave(Config config){
        redissonMasterSlaveConfig(config);
        return Redisson.createReactive(config);
    }

    @Bean
    @ConditionalOnClass(Config.class)
    @ConditionalOnProperty(value = "structure.redisson.model",havingValue = "master-slave")
    @ConditionalOnMissingBean(RedissonClient.class)
    public RedissonClient redissonClientByMasterSlave(Config config){
        redissonMasterSlaveConfig(config);
        return Redisson.create(config);
    }

    @Bean
    @ConditionalOnClass(Config.class)
    @ConditionalOnProperty(value = "structure.redisson.model", havingValue = "master-slave")
    @ConditionalOnMissingBean(RedissonRxClient.class)
    public RedissonRxClient redissonRxClientByMasterSlave(Config config){
        redissonMasterSlaveConfig(config);
        return Redisson.createRx(config);
    }


    /**
     * 加载redisson单体配置
     * @param config
     */
    private void redissonSingleConfig(Config config) {
        SingleServerConfig singleServerConfig = config.useSingleServer();
        SingleServerProperties param = redissonProperties.getSingle();
        singleServerConfig.setAddress(prefixAddress(param.getAddress()));
        singleServerConfig.setConnectionMinimumIdleSize(param.getConnectionMinimumIdleSize());
        singleServerConfig.setConnectionPoolSize(param.getConnectionPoolSize());
        singleServerConfig.setDatabase(redissonProperties.getDatabase());
        singleServerConfig.setDnsMonitoringInterval(param.getDnsMonitoringInterval());
        singleServerConfig.setSubscriptionConnectionMinimumIdleSize(param.getSubscriptionConnectionMinimumIdleSize());
        singleServerConfig.setSubscriptionConnectionPoolSize(param.getSubscriptionConnectionPoolSize());
        singleServerConfig.setPingTimeout(redissonProperties.getPingTimeout());
        singleServerConfig.setClientName(redissonProperties.getClientName());
        singleServerConfig.setConnectTimeout(redissonProperties.getConnectTimeout());
        singleServerConfig.setIdleConnectionTimeout(redissonProperties.getIdleConnectionTimeout());
        singleServerConfig.setKeepAlive(redissonProperties.getKeepAlive());
        singleServerConfig.setPassword(redissonProperties.getPassword());
        singleServerConfig.setPingConnectionInterval(redissonProperties.getPingConnectionInterval());
        singleServerConfig.setRetryAttempts(redissonProperties.getRetryAttempts());
        singleServerConfig.setRetryInterval(redissonProperties.getRetryInterval());
        singleServerConfig.setSslEnableEndpointIdentification(redissonProperties.getSslEnableEndpointIdentification());
        try {
            if (!StringUtil.isBlank(redissonProperties.getSslKeystore())) {
                singleServerConfig.setSslKeystore(new URI(redissonProperties.getSslKeystore()));
            }
            if (!StringUtil.isBlank(redissonProperties.getSslTruststore())) {
                singleServerConfig.setSslTruststore(new URI(redissonProperties.getSslTruststore()));
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        singleServerConfig.setSslKeystorePassword(redissonProperties.getSslKeystorePassword());
        singleServerConfig.setSslProvider(redissonProperties.getSslProvider());
        singleServerConfig.setSslTruststorePassword(redissonProperties.getSslTruststorePassword());
        singleServerConfig.setSubscriptionsPerConnection(redissonProperties.getSubscriptionsPerConnection());
        singleServerConfig.setTcpNoDelay(redissonProperties.getTcpNoDelay());
        singleServerConfig.setTimeout(redissonProperties.getTimeout());
    }


    /**
     * 加载redisson集群配置
     * @param config
     */
    private void redissonClusterConfig(Config config){
        ClusterProperties cluster = redissonProperties.getCluster();
        ClusterServersConfig clusterServersConfig = config.useClusterServers();
        clusterServersConfig.setNatMap(cluster.getNatMap());
        for (String nodeAddress : cluster.getNodeAddresses()) {
            clusterServersConfig.addNodeAddress(prefixAddress(nodeAddress));
        }
        initBaseConfig(clusterServersConfig,cluster);
    }

    /**
     * 加载 redisson 托管配置
     * @param config
     */
    private void redissonReplicatedConfig(Config config) {
        ReplicatedProperties replicated = redissonProperties.getReplicated();
        ReplicatedServersConfig replicatedServersConfig = config.useReplicatedServers();
        replicatedServersConfig.setDatabase(redissonProperties.getDatabase());
        replicatedServersConfig.setScanInterval(replicated.getScanInterval());
        for (String nodeAddress : replicated.getNodeAddresses()) {
            replicatedServersConfig.addNodeAddress(prefixAddress(nodeAddress));
        }
        initBaseConfig(replicatedServersConfig,replicated);
    }

    /**
     * 加载 redisson 哨兵配置
     * @param config
     */
    private void redissonSentinelConfig (Config config){
        SentinelProperties sentinel = redissonProperties.getSentinel();
        SentinelServersConfig sentinelServersConfig = config.useSentinelServers();
        sentinelServersConfig.setDatabase(redissonProperties.getDatabase());
        sentinelServersConfig.setMasterName(sentinel.getMasterName());
        sentinelServersConfig.setScanInterval(sentinel.getScanInterval());
        for (String nodeAddress : sentinel.getSentinelAddresses()) {
            sentinelServersConfig.addSentinelAddress(prefixAddress(nodeAddress));
        }
        initBaseConfig(sentinelServersConfig,sentinel);
    }

    /**
     * 加载主从模式配置
     * @param config
     */
    private void redissonMasterSlaveConfig(Config config) {
        MasterSlaveProperties masterSlave = redissonProperties.getMasterSlave();
        MasterSlaveServersConfig masterSlaveServersConfig = config.useMasterSlaveServers();
        masterSlaveServersConfig.setDatabase(redissonProperties.getDatabase());
        masterSlaveServersConfig.setMasterAddress(prefixAddress(masterSlave.getMasterAddress()));
        for (String nodeAddress : masterSlave.getSlaveAddresses()) {
            masterSlaveServersConfig.addSlaveAddress(prefixAddress(nodeAddress));
        }
        initBaseConfig(masterSlaveServersConfig,masterSlave);
    }

    /**
     * 初始化多节点公共配置
     * @param baseMasterSlaveServersConfig
     * @param multipleServerProperties
     */
    private void initBaseConfig(BaseMasterSlaveServersConfig baseMasterSlaveServersConfig, MultipleServerProperties multipleServerProperties) {
        baseMasterSlaveServersConfig.setSlaveConnectionMinimumIdleSize(multipleServerProperties.getSlaveConnectionMinimumIdleSize());
        baseMasterSlaveServersConfig.setSlaveConnectionPoolSize(multipleServerProperties.getSlaveConnectionPoolSize());
        baseMasterSlaveServersConfig.setFailedSlaveReconnectionInterval(multipleServerProperties.getFailedSlaveReconnectionInterval());
        baseMasterSlaveServersConfig.setFailedSlaveCheckInterval(multipleServerProperties.getFailedSlaveCheckInterval());
        baseMasterSlaveServersConfig.setMasterConnectionMinimumIdleSize(multipleServerProperties.getMasterConnectionMinimumIdleSize());
        baseMasterSlaveServersConfig.setMasterConnectionPoolSize(multipleServerProperties.getMasterConnectionPoolSize());
        baseMasterSlaveServersConfig.setReadMode(multipleServerProperties.getReadMode());
        baseMasterSlaveServersConfig.setSubscriptionMode(multipleServerProperties.getSubscriptionMode());
        baseMasterSlaveServersConfig.setSubscriptionConnectionMinimumIdleSize(multipleServerProperties.getSubscriptionConnectionMinimumIdleSize());
        baseMasterSlaveServersConfig.setSubscriptionConnectionPoolSize(multipleServerProperties.getSubscriptionConnectionPoolSize());
        baseMasterSlaveServersConfig.setDnsMonitoringInterval(multipleServerProperties.getDnsMonitoringInterval());
        try {
            baseMasterSlaveServersConfig.setLoadBalancer((LoadBalancer) Class.forName(multipleServerProperties.getLoadBalancer()).newInstance());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        baseMasterSlaveServersConfig.setPingTimeout(redissonProperties.getPingTimeout());
        baseMasterSlaveServersConfig.setClientName(redissonProperties.getClientName());
        baseMasterSlaveServersConfig.setConnectTimeout(redissonProperties.getConnectTimeout());
        baseMasterSlaveServersConfig.setIdleConnectionTimeout(redissonProperties.getIdleConnectionTimeout());
        if (null != redissonProperties.getKeepAlive()) {
            baseMasterSlaveServersConfig.setKeepAlive(redissonProperties.getKeepAlive());
        }
        baseMasterSlaveServersConfig.setPassword(redissonProperties.getPassword());
        baseMasterSlaveServersConfig.setPingConnectionInterval(redissonProperties.getPingConnectionInterval());
        baseMasterSlaveServersConfig.setRetryAttempts(redissonProperties.getRetryAttempts());
        baseMasterSlaveServersConfig.setRetryInterval(redissonProperties.getRetryInterval());
        baseMasterSlaveServersConfig.setSslEnableEndpointIdentification(redissonProperties.getSslEnableEndpointIdentification());
        try {
            if (!StringUtil.isBlank(redissonProperties.getSslKeystore())) {
                baseMasterSlaveServersConfig.setSslKeystore(new URI(redissonProperties.getSslKeystore()));
            }
            if (!StringUtil.isBlank(redissonProperties.getSslTruststore())) {
                baseMasterSlaveServersConfig.setSslTruststore(new URI(redissonProperties.getSslTruststore()));
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        baseMasterSlaveServersConfig.setSslKeystorePassword(redissonProperties.getSslKeystorePassword());
        baseMasterSlaveServersConfig.setSslProvider(redissonProperties.getSslProvider());
        baseMasterSlaveServersConfig.setSslTruststorePassword(redissonProperties.getSslTruststorePassword());
        baseMasterSlaveServersConfig.setSubscriptionsPerConnection(redissonProperties.getSubscriptionsPerConnection());
        baseMasterSlaveServersConfig.setTcpNoDelay(redissonProperties.getTcpNoDelay());
        baseMasterSlaveServersConfig.setTimeout(redissonProperties.getTimeout());
    }


}
