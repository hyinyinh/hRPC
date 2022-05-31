package com.hy.rpc.netty;

import ch.qos.logback.core.hook.ShutdownHook;
import com.hy.rpc.AbstractRpcServer;
import com.hy.rpc.RpcServer;
import com.hy.rpc.codec.CommonDecoder;
import com.hy.rpc.codec.CommonEncoder;
import com.hy.rpc.config.Config;
import com.hy.rpc.hook.ShutDownHook;
import com.hy.rpc.provider.ServiceProviderImpl;
import com.hy.rpc.registry.NacosServiceRegistry;
import com.hy.rpc.provider.ServiceProvider;
import com.hy.rpc.registry.ServiceRegistry;
import com.hy.rpc.serializer.CommonSerializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/5/23 13:27
 */
@Data
public class NettyServer extends AbstractRpcServer {
    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);
    private CommonSerializer serializer;

    public NettyServer(String host, int port, CommonSerializer serializer) {
        this.host = host;
        this.port = port;
        serviceRegistry = new NacosServiceRegistry(Config.getLoadBlance());
        serviceProvider = new ServiceProviderImpl();
        this.serializer = serializer;
        scanServices();
    }

    @Override
    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup,workGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.SO_BACKLOG,256)
                .option(ChannelOption.SO_KEEPALIVE,true)
                .childOption(ChannelOption.TCP_NODELAY,true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new IdleStateHandler(10,0, 0,TimeUnit.SECONDS))
                                .addLast(new CommonEncoder(Config.getSerializer()))
                                .addLast(new CommonDecoder())
                                .addLast(new NettyServerHandler());
                    }
                });
        ChannelFuture future = serverBootstrap.bind(host,port).sync();
        ShutDownHook.getShutDownHook().addClearAllHook();
        future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("启动服务器时有错误发生：",e);
        }finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
