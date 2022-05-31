package com.hy.rpc.netty;

import com.hy.rpc.entity.RpcRequest;
import com.hy.rpc.entity.RpcResponse;
import com.hy.rpc.handler.RequestHandler;
import com.hy.rpc.util.ThreadPoolFactory;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;


/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/5/23 19:40
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

    private static RequestHandler requestHandler;
    private static final String THREAD_NAME_PREFIX = "netty-server-handler";
    private static final ExecutorService threadPool;

    static{
        requestHandler = new RequestHandler();
        threadPool = ThreadPoolFactory.createDefaultThreadPool(THREAD_NAME_PREFIX);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            IdleState state = ((IdleStateEvent) evt).state();
            if(state == IdleState.READER_IDLE){
                logger.info("已经5s没有收到数据了 服务器将断开连接");
                //ctx.close();
            }
        }else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("处理过程调用时有错误发生:{}",cause);
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest rpcRequest) throws Exception {
        threadPool.execute(()->{
            try{
                if(rpcRequest.getHeartBeat()){
                    logger.info("接收到客户端心跳包...");
                    return;
                }
                logger.info("服务器收到请求：{}",rpcRequest);
                Object result = requestHandler.handler(rpcRequest);
                if(ctx.channel().isActive() && ctx.channel().isWritable()){
                    ctx.writeAndFlush(RpcResponse.success(result,rpcRequest.getRequestId()));
                }else {
                    logger.info("通道不可写");
                }
            }finally {
                ReferenceCountUtil.release(rpcRequest);
            }
        });
    }
}
