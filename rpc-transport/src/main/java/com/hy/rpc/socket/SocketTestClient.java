package com.hy.rpc.socket;


import com.hy.rpc.config.Config;
import com.hy.rpc.entity.HelloObject;
import com.hy.rpc.entity.HelloService;
import com.hy.rpc.proxy.RpcClientProxy;
import com.hy.rpc.serializer.CommonSerializer;
import com.hy.rpc.socket.SocketClient;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/5/22 19:40
 */
public class SocketTestClient {
    public static void main(String[] args) {
        SocketClient socketClient = new SocketClient(Config.getLoadBlance());
        RpcClientProxy rpcClientProxy = new RpcClientProxy(socketClient);
        //动态代理，生成代理对象
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12,"this is a message");
        String res = helloService.hello(object);
        System.out.println(res);
    }
}
