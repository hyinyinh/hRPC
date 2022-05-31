package com.hy.rpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/5/22 16:57
 */
@Getter
@AllArgsConstructor
public enum ResponseCode {
    SUCCESS(200,"方法调用成功"),
    FAIL(500,"调用方法失败"),
    METHOD_NOT_FOUND(500,"未找到指定方法"),
    CLASS_NOT_FOUND(500,"未找到指定类");

    private final int code;
    private final String message;

}
