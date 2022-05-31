package com.hy.rpc.provider;

/**
 * @author hy
 * @version 1.0
 * @Desctiption 本地保存文件
 * @date 2022/5/23 10:36
 */

public interface ServiceProvider {
    <T> void addServiceProvider(T service,String serviceName);
    Object getServiceProvider(String serviceName);

}

