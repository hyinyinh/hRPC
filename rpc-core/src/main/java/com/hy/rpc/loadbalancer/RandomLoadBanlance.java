package com.hy.rpc.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;
import java.util.Random;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/5/25 18:27
 */
public class RandomLoadBanlance implements LoadBalance{
    @Override
    public Instance select(List<Instance> instanceList) {
        return instanceList.get(new Random().nextInt(instanceList.size()));
    }
}
