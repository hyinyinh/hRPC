package com.hy.rpc.util;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.hy.rpc.constans.coderConstans;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/5/25 16:21
 */
public class NacosUtil {
    private static final Logger logger = LoggerFactory.getLogger(NacosUtil.class);

    private static final NamingService namingService;
    private static final Set<String> serviceNames = new HashSet<>();
    private static InetSocketAddress address;

    static {
        namingService = getNacosNamingService();
    }

    public static NamingService getNacosNamingService(){
        try{
            return NamingFactory.createNamingService(coderConstans.SERVER_ADDR);
        }catch (NacosException e){
            logger.error("连接Nacos时有错误发生");
            return null;
        }
    }

    public static void registerService(String serviceName,InetSocketAddress address) throws NacosException {
        namingService.registerInstance(serviceName,address.getHostName(),address.getPort());
        NacosUtil.address = address;
        serviceNames.add(serviceName);
    }

    public static List<Instance> getAllInstance(String serviceName) throws NacosException {
        return namingService.getAllInstances(serviceName);
    }

    public static void clearRegistry(){
        //deregisterInstance 进行服务注销
        if(!serviceNames.isEmpty() && address != null){
            String host = address.getHostName();
            int port = address.getPort();

            Iterator<String> iterator = serviceNames.iterator();
            while(iterator.hasNext()){
                String serviceName = iterator.next();
                try {
                    namingService.deregisterInstance(serviceName,host,port);
                } catch (NacosException e) {
                    logger.error("注销服务失败");
                }
            }
        }
    }
}
