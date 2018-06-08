package com.servingcloud.invoice.transfer.canalclient.task;

import com.servingcloud.invoice.transfer.canalclient.CanalRunner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author fqlee
 * @since 2018/6/7
 */
@Component
@Slf4j
public class CanalTask {

    private final int times = 1;

    /**
     * 健康检查
     */
    @Scheduled(cron = "0/100 * * * * ? ")
    public void healthCheck(){
        log.info("**********************************定时任务启动**********************************");
        int runners = CanalRunner.getRunners();
        log.info("**********************************CannalRunner启动实例数:{}**********************",runners);
        if (runners < times){
            CanalRunner.restart();
        }
        log.info("**********************************定时任务结束**********************************");
    }

}
