package com.hy.rpc;

import com.hy.rpc.entity.RpcRequest;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/5/23 12:59
 */
public interface RpcServer {
    void start();
    <T> void publishService(Object service,String serviceName);
}
