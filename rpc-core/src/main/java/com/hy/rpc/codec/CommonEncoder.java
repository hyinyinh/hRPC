package com.hy.rpc.codec;

import com.hy.rpc.entity.RpcRequest;
import com.hy.rpc.enumeration.PackageType;
import com.hy.rpc.serializer.CommonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.hy.rpc.constans.coderConstans.MAGIC_NUMBER;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/5/23 17:01
 */
public class CommonEncoder extends MessageToByteEncoder {
    private static final Logger logger = LoggerFactory.getLogger(CommonDecoder.class);
    private final CommonSerializer serializer;
    public CommonEncoder(CommonSerializer commonSerializer){
        this.serializer = commonSerializer;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        logger.info("正在编码。。。。");
        out.writeInt(MAGIC_NUMBER);
        if(msg instanceof RpcRequest) {
            out.writeInt(PackageType.REQUEST_PACK.getCode());
        }else{
            out.writeInt(PackageType.RESPONSE_PACK.getCode());
        }
        out.writeInt(serializer.getCode());
        byte[] bytes = serializer.serialize(msg);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }
}
