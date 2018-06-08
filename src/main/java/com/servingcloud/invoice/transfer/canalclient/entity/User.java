package com.servingcloud.invoice.transfer.canalclient.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * 用户数据
 * @author fqlee
 * @since 2018/6/8
 */
@Data
public class User {

    @Id
    private String uid;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
}
