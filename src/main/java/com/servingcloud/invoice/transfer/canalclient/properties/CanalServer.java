package com.servingcloud.invoice.transfer.canalclient.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

/**
 * @author fqlee
 * @since 2018/6/6
 */
@Data
@ConfigurationProperties(prefix="canal.server")
public class CanalServer {
    /**
     *
     */
    private String host;
    /**
     *
     */
    private String port;
    /**
     *
     */
    private String instance;
    /**
     *
     */
    private String batchSize;
    /**
     *
     */
    private String sleep;
}
