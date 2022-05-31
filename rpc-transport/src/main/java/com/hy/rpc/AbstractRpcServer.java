package com.hy.rpc;

import com.hy.rpc.annotation.Service;
import com.hy.rpc.annotation.ServiceScan;
import com.hy.rpc.enumeration.RpcError;
import com.hy.rpc.exception.RpcException;
import com.hy.rpc.provider.ServiceProvider;
import com.hy.rpc.registry.ServiceRegistry;
import com.hy.rpc.util.ReflectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Set;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/5/26 18:45
 */
public abstract class AbstractRpcServer implements RpcServer{
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    protected String host;
    protected int port;
    protected ServiceRegistry serviceRegistry;
    protected ServiceProvider serviceProvider;

    public void scanServices(){
        String mainClassName = ReflectUtil.getStackTrace();
        Class<?> startClass;
        try {
            startClass = Class.forName(mainClassName);
            if(!startClass.isAnnotationPresent(ServiceScan.class)){
                logger.error("启动类缺少@ServiceScan注解");
                throw new RpcException(RpcError.SERVICE_SCAN_PACKAGE_NOT_FOUND);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RpcException(RpcError.UNKNOWN_ERROR);
        }
        String basePackage = startClass.getAnnotation(ServiceScan.class).value();
        if(basePackage.equals("")){
            basePackage = mainClassName.substring(0,mainClassName.lastIndexOf("."));
        }
        Set<Class<?>> classSet = ReflectUtil.getClasses(basePackage);
        for (Class<?> clazz : classSet) {
            if(clazz.isAnnotationPresent(Service.class)){
                String serviceName = clazz.getAnnotation(Service.class).name();
                Object obj;
                try {
                    obj = clazz.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
                if("".equals(serviceName)){
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for (Class<?> anInterface : interfaces) {
                        publishService(obj,anInterface.getCanonicalName());
                    }
                }else {
                    publishService(obj,serviceName);
                }
            }
        }
    }


    @Override
    public <T> void publishService(Object service, String serviceName) {
        serviceProvider.addServiceProvider(service,serviceName);
        serviceRegistry.register(serviceName,new InetSocketAddress(host,port));
    }
}
