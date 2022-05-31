package com.hy.rpc.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/5/25 18:28
 */
public class RoundRobinLoadBalance implements LoadBalance{
    private int index;
    @Override
    public Instance select(List<Instance> instanceList) {
        if(index >= instanceList.size()){
            index %= instanceList.size();
        }
        return instanceList.get(index);
    }
}
