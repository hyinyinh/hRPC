package com.hy.rpc.netty;

import com.hy.rpc.RpcClient;
import com.hy.rpc.config.Config;
import com.hy.rpc.entity.RpcRequest;
import com.hy.rpc.entity.RpcResponse;
import com.hy.rpc.registry.NacosServiceRegistry;
import com.hy.rpc.registry.ServiceRegistry;
import com.hy.rpc.serializer.CommonSerializer;
import com.hy.rpc.util.SingletonFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/5/23 13:27
 */
public class NettyClient implements RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private static final Bootstrap bootstrap;
    private final ServiceRegistry serviceRegistry;
    private CommonSerializer serializer;
    private final UnprocessedRequests unprocessedRequests;
    private static final EventLoopGroup group;

    public NettyClient() {
        this.serviceRegistry = new NacosServiceRegistry(Config.getLoadBlance());
        unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }

    static {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class);
    }

    @Override
    public CompletableFuture<RpcResponse> sendRequest(RpcRequest rpcRequest) {
        CompletableFuture<RpcResponse> resultFuture = new CompletableFuture<RpcResponse>();
        try {
            InetSocketAddress inetSocketAddress = serviceRegistry.lookupService(rpcRequest.getInterfaceName());
            Channel channel = ChannelProvider.get(inetSocketAddress, serializer);
            if (!channel.isActive()) {
                group.shutdownGracefully();
                return null;
            }
            unprocessedRequests.put(rpcRequest.getRequestId(), resultFuture);
            channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    logger.info("客户端发送消息：{}", rpcRequest.toString());
                } else {
                    logger.info("发送消息时有错误发送：", future.cause());
                    future.channel().close();
                }
            });
        } catch (InterruptedException e) {
            unprocessedRequests.remove(rpcRequest.getRequestId());
            e.printStackTrace();
            //interrupt()这里作用是给受阻塞的当前线程发出一个中断信号，让当前线程退出阻塞状态，好继续执行然后结束
            Thread.currentThread().interrupt();
        }
        return resultFuture;
    }


}
