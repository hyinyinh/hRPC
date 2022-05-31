package com.hy.rpc.config;

import com.hy.rpc.enumeration.SerializerCode;
import com.hy.rpc.loadbalancer.LoadBalance;
import com.hy.rpc.loadbalancer.RandomLoadBanlance;
import com.hy.rpc.loadbalancer.RoundRobinLoadBalance;
import com.hy.rpc.serializer.CommonSerializer;
import com.hy.rpc.serializer.ProtobufSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class Config {
    static Properties properties;
    static {
        try (InputStream in = Config.class.getResourceAsStream("/application.properties")) {
            properties = new Properties();
            properties.load(in);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
    public static CommonSerializer getSerializer() {
        String value = properties.getProperty("serializerCode");
        if(value == null) {
            return new ProtobufSerializer();
        } else {
            SerializerCode code = SerializerCode.valueOf(value);
            return CommonSerializer.getByCode(code.getCode());
        }
    }

    public static LoadBalance getLoadBlance() {
        String value = properties.getProperty("loadBlance");
        if(value == null) {
            return new RoundRobinLoadBalance();
        } else {
            return new RandomLoadBanlance();
        }
    }
}