package com.hy.rpc.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.hy.rpc.serializer.CommonSerializer;
import com.hy.rpc.serializer.JsonSerializer;
import com.hy.rpc.serializer.KryoSerializer;

import java.util.List;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/5/25 18:26
 */
public interface LoadBalance {
    Instance select(List<Instance> instanceList);

    static LoadBalance getByCode(int code){
        switch (code) {
            case 0:
                return new RandomLoadBanlance();
            case 1:
                return new RoundRobinLoadBalance();
            default:
                return null;
        }
    }
}
