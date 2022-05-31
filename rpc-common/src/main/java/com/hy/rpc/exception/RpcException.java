package com.hy.rpc.exception;

import com.hy.rpc.enumeration.RpcError;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/5/23 12:28
 */
public class RpcException extends RuntimeException {

    public RpcException(RpcError error, String detail) {
        super(error.getMessage() + ": " + detail);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(RpcError error) {
        super(error.getMessage());
    }

}