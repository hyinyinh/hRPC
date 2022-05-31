package com.hy.rpc.entity;

import com.hy.rpc.enumeration.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author hy
 * @version 1.0
 * @Desctiption  服务器调用完方法，需要返回给客户端的对象
 * @date 2022/5/22 16:54
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpcResponse<T> implements Serializable {
    /**
     * 响应对应的请求号
     */
    private String requestId;
    /*
    * 响应状态码
    * */
    private Integer statusCode;

    /*
    * 响应状态补充信息
    * */
    private String message;

    /*
    * 响应数据
    * */
    private T data;

    //成功返回响应数据
    public static <T> RpcResponse<T> success(T data,String requestId){
        RpcResponse<T> response = new RpcResponse<>();
        response.setRequestId(requestId);
        response.setStatusCode(ResponseCode.SUCCESS.getCode());
        response.setData(data);
        return response;
    }

    //失败返回状态码以及错误信息
    public static <T> RpcResponse<T> fail(ResponseCode code,String requestId){
        RpcResponse<T> response = new RpcResponse<>();
        response.setRequestId(requestId);
        response.setStatusCode(code.getCode());
        response.setMessage(code.getMessage());
        return response;
    }
}
