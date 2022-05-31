package com.hy.rpc.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.hy.rpc.enumeration.RpcError;
import com.hy.rpc.exception.RpcException;
import com.hy.rpc.loadbalancer.LoadBalance;
import com.hy.rpc.loadbalancer.RandomLoadBanlance;
import com.hy.rpc.util.NacosUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/5/24 14:50
 */
public class NacosServiceRegistry implements ServiceRegistry{
    private static final Logger logger = LoggerFactory.getLogger(NacosServiceRegistry.class);

    private final LoadBalance loadBalance;

    public NacosServiceRegistry(){
        this.loadBalance = new RandomLoadBanlance();
    }

    public NacosServiceRegistry(LoadBalance loadBalance){
        this.loadBalance = loadBalance;
    }

    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            NacosUtil.registerService(serviceName,inetSocketAddress);
        } catch (NacosException e) {
            logger.error("注册服务时有错误发生:", e);
            e.printStackTrace();
        }
    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try {
            List<Instance> instances = NacosUtil.getAllInstance(serviceName);
            if(instances.size() == 0){
                logger.error("找不到对应的服务："+serviceName);
                throw new RpcException(RpcError.SERVICE_NOT_FOUND);
            }
            Instance instance = loadBalance.select(instances);
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e) {
            e.printStackTrace();
            return null;
        }
    }
}
