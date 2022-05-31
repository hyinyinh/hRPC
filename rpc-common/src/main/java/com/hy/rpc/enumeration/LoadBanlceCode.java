package com.hy.rpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/5/25 18:32
 */
@AllArgsConstructor
@Getter
public enum LoadBanlceCode {
    Random(0),
    RoundLoad(1);

    private final int code;
}
