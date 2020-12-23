package cn.structure.starter.redisson.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 *     缓存配置
 * </p>
 * @author chuck
 * @since 2020-12-23
 * @version 1.0.1
 */
@Getter
@Setter
@ToString
@Configuration
@ConfigurationProperties("structure.redisson.cache")
public class CacheProperties {

    /**
     * 缓存key的分组名 例如: GROUP_KYE:BUSINESS_KEY
     */
    private String keyGroupName;
}
