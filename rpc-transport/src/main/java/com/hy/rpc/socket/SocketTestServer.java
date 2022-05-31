package com.hy.rpc.socket;

import com.hy.rpc.config.Config;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/5/22 19:39
 */
public class SocketTestServer {
    public static void main(String[] args) {
        SocketServer socketServer = new SocketServer("127.0.0.1", 9998, Config.getSerializer());
        socketServer.start();
    }
}
