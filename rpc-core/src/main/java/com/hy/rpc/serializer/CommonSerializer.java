package com.hy.rpc.serializer;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/5/23 19:05
 */
public interface CommonSerializer {
    byte[] serialize(Object obj);
    Object deserialize(byte[] bytes,Class<?> clazz);
    int getCode();

    static CommonSerializer getByCode(int code){
        switch (code) {
            case 0:
                return new KryoSerializer();
            case 1:
                return new JsonSerializer();
            case 2:
                return new HessianSerializer();
            case 3:
                return new ProtobufSerializer();
            default:
                return null;
        }
    }
}
