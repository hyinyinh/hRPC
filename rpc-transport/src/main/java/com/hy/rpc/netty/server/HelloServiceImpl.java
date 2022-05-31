package com.hy.rpc.netty.server;

import com.hy.rpc.annotation.Service;
import com.hy.rpc.entity.HelloObject;
import com.hy.rpc.entity.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hy
 * @version 1.0
 * @Desctiption 对接口中的hello方法实现
 * @date 2022/5/22 16:39
 */
@Service
public class HelloServiceImpl implements HelloService {
    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);
    @Override
    public String hello(HelloObject object) {
        logger.info("接收到：{}",object.getMessage());
        return "这是调用的返回值，id="+object.getId();
    }
}
