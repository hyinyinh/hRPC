package com.hy.rpc.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author hy
 * @version 1.0
 * @Desctiption 确定一个方法：接口名称、方法名、方法参数、方法参数类型
 *              通过这几个参数找到方法并调用
 * @date 2022/5/22 16:47
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcRequest implements Serializable {
    /**
     * 请求号
     */
    private String requestId;
    /**
     * 待调用接口名称
     */
    private String interfaceName;
    /**
     * 待调用方法名称
     */
    private String methodName;
    /**
     * 调用方法的参数
     */
    private Object[] parameters;
    /**
     * 调用方法的参数类型
     */
    private Class<?>[] paramTypes;
    /*
    * 是否是心跳包
    * */
    private Boolean heartBeat;
}
