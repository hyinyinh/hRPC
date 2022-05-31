package com.hy.rpc.socket;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.hy.rpc.RpcClient;
import com.hy.rpc.config.Config;
import com.hy.rpc.entity.RpcRequest;
import com.hy.rpc.loadbalancer.LoadBalance;
import com.hy.rpc.registry.NacosServiceRegistry;
import com.hy.rpc.registry.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;


/**
 * @author hy
 * @version 1.0
 * @Desctiption rpc客户端 发送rpcRequest
 * @date 2022/5/22 17:33
 */
public class SocketClient implements RpcClient{
    private static final Logger logger = LoggerFactory.getLogger(SocketClient.class);
    private final ServiceRegistry serviceRegistry;
    private final LoadBalance loadBalance;

    public SocketClient(LoadBalance loadBalance){
        if(loadBalance != null){
            this.loadBalance = loadBalance;
        }else {
            this.loadBalance = LoadBalance.getByCode(0);
        }
        this.serviceRegistry = new NacosServiceRegistry(Config.getLoadBlance());
    }


    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        InetSocketAddress inetSocketAddress = serviceRegistry.lookupService(rpcRequest.getInterfaceName());
        try (Socket socket = new Socket()) {
            socket.connect(inetSocketAddress);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream.writeObject(rpcRequest);
            objectOutputStream.flush();
            return objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            logger.error("调用时有错误发生：", e);
            return null;
        }
    }
}
