package com.hy.rpc.netty;

import com.hy.rpc.RpcClient;
import com.hy.rpc.config.Config;
import com.hy.rpc.entity.HelloObject;
import com.hy.rpc.entity.HelloService;
import com.hy.rpc.entity.RpcRequest;
import com.hy.rpc.proxy.RpcClientProxy;
import com.hy.rpc.serializer.CommonSerializer;
import com.hy.rpc.serializer.KryoSerializer;

import java.util.Scanner;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/5/23 20:03
 */
public class NettyTestClient {
    public static void main(String[] args) {
        NettyClient client = new NettyClient();
        CommonSerializer serializer = Config.getSerializer();
        client.setSerializer(serializer);

        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);

        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        String res = helloService.hello(new HelloObject(12, "this is message"));
        System.out.println(res);
    }
}
