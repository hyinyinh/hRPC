package com.hy.rpc.hook;

import com.hy.rpc.constans.coderConstans;
import com.hy.rpc.util.NacosUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.hy.rpc.util.ThreadPoolFactory;

import java.util.concurrent.ExecutorService;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/5/25 16:58
 */
public class ShutDownHook {

    private static final Logger logger = LoggerFactory.getLogger(ShutDownHook.class);
    private final ExecutorService threadPool = ThreadPoolFactory.createDefaultThreadPool(coderConstans.SHUT_DOWN_HOOK);

    private static final ShutDownHook shutDownHook = new ShutDownHook();

    public static ShutDownHook getShutDownHook(){
        return shutDownHook;
    }

    public void addClearAllHook(){
        logger.info("关闭后将自动注销所有服务");
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            NacosUtil.clearRegistry();
            threadPool.shutdown();
        }));
    }

}
