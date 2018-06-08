package com.servingcloud.invoice.transfer.canalclient;

import com.servingcloud.invoice.transfer.canalclient.properties.CanalServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 启动类
 * @author fqlee
 */
@SpringBootApplication
@EnableScheduling
public class CanalClientApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(CanalClientApplication.class, args);
		SpringContextUtil.setApplicationContext(context);
		Binder binder = Binder.get(context.getEnvironment());
		CanalServer canalServer = binder.bind("canal.server", Bindable.of(CanalServer.class)).get();
		CanalRunner canalRunner = new CanalRunner();
		canalRunner.setup(canalServer);
	}


}
