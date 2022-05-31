package com.hy.rpc.proxy;

import com.hy.rpc.entity.RpcRequest;
import com.hy.rpc.entity.RpcResponse;
import com.hy.rpc.handler.RequestHandler;
import com.hy.rpc.provider.ServiceProvider;
import com.hy.rpc.serializer.CommonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/5/23 12:39
 */
public class SocketRequestHanlderThread implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(SocketRequestHanlderThread.class);

    private Socket socket;
    private RequestHandler requestHandler;
    private CommonSerializer serializer;

    public SocketRequestHanlderThread(Socket socket,
                                      RequestHandler requestHandler,
                                      CommonSerializer serializer){
        this.socket = socket;
        this.requestHandler = requestHandler;
        this.serializer = serializer;
    }

    @Override
    public void run() {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
            String interfaceName = rpcRequest.getInterfaceName();
            Object result = requestHandler.handler(rpcRequest);
            objectOutputStream.writeObject(RpcResponse.success(result, rpcRequest.getRequestId()));
            objectOutputStream.flush();
        } catch (IOException | ClassNotFoundException e) {
            logger.error("调用时有错误发生{}",e);
        }
    }
}
