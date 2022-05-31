package com.hy.rpc.netty.server;

import com.hy.rpc.annotation.ServiceScan;
import com.hy.rpc.config.Config;
import com.hy.rpc.netty.NettyServer;
import com.hy.rpc.serializer.CommonSerializer;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/5/23 20:01
 */
@ServiceScan
public class NettyTestServer {
    public static void main(String[] args) {
        CommonSerializer serializer = Config.getSerializer();
        NettyServer nettyServer = new NettyServer("127.0.0.1",9999,serializer);
        nettyServer.start();
    }
}
