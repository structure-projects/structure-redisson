package cn.structure.starter.redisson.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *     集群配置
 * </p>
 */
@Getter
@Setter
@ToString
public class ClusterProperties extends MultipleServerProperties {
    /**
     * nat的映射配置
     */
    private Map<String, String> natMap = Collections.emptyMap();
    /**
     * 集群redis节点地址配置
     */
    private List<String> nodeAddresses = new ArrayList();
}
