package cn.structure.starter.redisson.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

/**
 * <p>
 *     主从模式
 * </p>
 * @author chuck
 * @since 2020-12-23
 * @version 1.0.1
 */
@Getter
@Setter
@ToString
public class MasterSlaveProperties extends MultipleServerProperties{
    /**
     * 从节点地址配置
     */
    private Set<String> slaveAddresses = new HashSet();
    /**
     * 主节点地址配置
     */
    private String masterAddress;
}
