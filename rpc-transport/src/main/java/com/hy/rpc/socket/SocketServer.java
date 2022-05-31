package com.hy.rpc.socket;

import com.hy.rpc.AbstractRpcServer;
import com.hy.rpc.RpcServer;
import com.hy.rpc.handler.RequestHandler;
import com.hy.rpc.provider.ServiceProviderImpl;
import com.hy.rpc.proxy.SocketRequestHanlderThread;
import com.hy.rpc.provider.ServiceProvider;
import com.hy.rpc.registry.NacosServiceRegistry;
import com.hy.rpc.registry.ServiceRegistry;
import com.hy.rpc.serializer.CommonSerializer;
import com.hy.rpc.serializer.KryoSerializer;
import com.hy.rpc.util.ThreadPoolFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * @author hy
 * @version 1.0
 * @Desctiption rpc服务端 使用ServerSocket监听某个端口，利用线程池循环接收连接请求
 * @date 2022/5/22 19:22
 */
public class SocketServer extends AbstractRpcServer {

    private final ExecutorService threadPool;
    private RequestHandler requestHandler = new RequestHandler();
    private final CommonSerializer serializer;

    private static final Logger logger = LoggerFactory.getLogger(SocketServer.class);

    public SocketServer(String host, int port) {
        this(host, port, new KryoSerializer());
    }
    public SocketServer(String host, int port, CommonSerializer serializer) {
        this.host = host;
        this.port = port;
        threadPool = ThreadPoolFactory.createDefaultThreadPool("socket-rpc-server");
        serviceRegistry = new NacosServiceRegistry();
        serviceProvider = new ServiceProviderImpl();
        this.serializer = serializer;
        scanServices();
    }


    @Override
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket()) {
            logger.info("服务器正在启动...");
            Socket socket;
            while((socket = serverSocket.accept()) != null) {
                logger.info("客户端连接！Ip为：" + socket.getInetAddress());
                //向工作线程WorkerThread传入了socket和用于服务端实例service。
                //通过线程中的run方法生成RpcResponse对象
                threadPool.execute(new SocketRequestHanlderThread(socket, requestHandler,serializer));
            }
            threadPool.shutdown();
        } catch (IOException e) {
            logger.error("连接时有错误发生：", e);
        }
    }

}
