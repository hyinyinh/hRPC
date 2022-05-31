package com.hy.rpc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author hy
 * @version 1.0
 * @Desctiption hello方法中需要传递的对象
 * @date 2022/5/22 16:36
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HelloObject implements Serializable {
    //它需要在调用过程中从客户端传递给服务端
    private Integer id;
    private String message;
}
