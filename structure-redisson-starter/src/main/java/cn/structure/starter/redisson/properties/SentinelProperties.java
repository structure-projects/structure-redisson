package cn.structure.starter.redisson.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *     哨兵模式
 * </p>
 * @author chuck
 * @since 2020-12-23
 * @version 1.0.1
 */
@Getter
@Setter
@ToString
public class SentinelProperties extends MultipleServerProperties {
    /**
     * 哨兵模式的地址
     */
    private List<String> sentinelAddresses = new ArrayList();
    /**
     * 哨兵模式的名字
     */
    private String masterName;
    /**
     * 节点变化扫描间隔时间
     */
    private int scanInterval = 1000;
}
