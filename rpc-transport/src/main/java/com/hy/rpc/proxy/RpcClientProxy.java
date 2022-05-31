package com.hy.rpc.proxy;

import com.hy.rpc.RpcClient;
import com.hy.rpc.netty.NettyClient;
import com.hy.rpc.socket.SocketClient;
import com.hy.rpc.entity.RpcRequest;
import com.hy.rpc.entity.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author hy
 * @version 1.0
 * @Desctiption JDK动态代理 通过host port指明服务器位置 通过getProxy方法生成代理对象
 * @date 2022/5/22 17:14
 */
public class RpcClientProxy implements InvocationHandler {
    //InvocationHandler位于JDK反射包下，其作用是在实现JDK动态代理的时候提供了动态执行增强逻辑的方法
   private static final Logger logger = LoggerFactory.getLogger(RpcClientProxy.class);

    private RpcClient rpcClient;

    public RpcClientProxy(RpcClient client){
        this.rpcClient = client;
    }

    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),
                                            new Class<?>[]{clazz},
                                        this);
    }

    /**
     * 重写invoke方法，实现代理类在被客户端调用时候的动作
     *      客户端通过代理将RPCRequest对象发送给服务端
     *      服务端返回数据给代理，客户端在代理处获得数据
     *
     *      客户端只需要调用代理来实现与服务端的传输
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        RpcRequest rpcRequest = new RpcRequest(UUID.randomUUID().toString(), method.getDeclaringClass().getName(),
                method.getName(), args, method.getParameterTypes(), false);

        Object result = null;
        if(rpcClient instanceof NettyClient){
            try {
                //异步获取调用结果
                CompletableFuture<RpcResponse> completableFuture = (CompletableFuture<RpcResponse>) rpcClient.sendRequest(rpcRequest);
                result = completableFuture.get().getData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;

    }
}
