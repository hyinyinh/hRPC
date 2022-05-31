package com.hy.rpc.registry;

import java.net.InetSocketAddress;

/**
 * @author hy
 * @version 1.0
 * @Desctiption 远程注册表
 * @date 2022/5/24 14:37
 */
public interface ServiceRegistry {
    //register 方法将服务的名称和地址注册进服务注册中心
    void register(String serviceName, InetSocketAddress inetSocketAddress);
    //lookupService 方法则是根据服务名称从注册中心获取到一个服务提供者的地址
    InetSocketAddress lookupService(String serviceName);
}
