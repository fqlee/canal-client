# canal-client

#### 项目介绍
canal客户端、解析binlog日志将数据从mysql过渡到rabbitmq和mongodb中

#### 软件架构
软件架构说明


#### 安装教程

1. 下载最新版本的canal服务端，[下载地址](https://github.com/alibaba/canal/releases)
![![输入图片说明](https://gitee.com/uploads/images/2018/0608/190733_120ed703_1697823.png "DJT1038ULOX7C]SHGY7D1UU.png")](https://gitee.com/uploads/images/2018/0608/190711_dcd1c334_1697823.png "屏幕截图.png")
2. 解压缩
mkdir /tmp/canal
tar zxvf canal.deployer-$version.tar.gz  -C /tmp/canal

解压完成后，进入/tmp/canal目录，可以看到如下结构：

drwxr-xr-x 2 jianghang jianghang  136 2013-02-05 21:51 bin
drwxr-xr-x 4 jianghang jianghang  160 2013-02-05 21:51 conf
drwxr-xr-x 2 jianghang jianghang 1.3K 2013-02-05 21:51 lib
drwxr-xr-x 2 jianghang jianghang   48 2013-02-05 21:29 logs

3. 配置修改

应用参数：

`vi conf/example/instance.properties`


```
#################################################
#### mysql serverId
canal.instance.mysql.slaveId = 1234

#################################################
#### mysql serverId
canal.instance.mysql.slaveId = 1234

 **position info，需要改成自己的数据库信息** 
```


```
canal.instance.master.address = 127.0.0.1:3306
canal.instance.master.journal.name =
canal.instance.master.position =
canal.instance.master.timestamp =
```

```
#canal.instance.standby.address =
#canal.instance.standby.journal.name =
#canal.instance.standby.position =
#canal.instance.standby.timestamp =
```

 **username/password，需要改成自己的数据库信息** 


```
canal.instance.dbUsername = canal

canal.instance.dbPassword = canal
canal.instance.defaultDatabaseName =
canal.instance.connectionCharset = UTF-8
```

 **table regex** 

```
canal.instance.filter.regex = .\..
```

#################################################

说明：

- 这里是列表文本这里是列表文本canal.instance.connectionCharset 代表数据库的编码方式对应到java中的编码类型，比如UTF-8，GBK , ISO-8859-1

4. 准备启动

`sh bin/startup.sh`

5. 查看日志

`vi logs/canal/canal.log`


```
2013-02-05 22:45:27.967 [main] INFO  com.alibaba.otter.canal.deployer.CanalLauncher - ## start the canal server.
2013-02-05 22:45:28.113 [main] INFO  com.alibaba.otter.canal.deployer.CanalController - ## start the canal server[10.1.29.120:11111]
2013-02-05 22:45:28.210 [main] INFO  com.alibaba.otter.canal.deployer.CanalLauncher - ## the canal server is running now ......
```

具体instance的日志：

`vi logs/example/example.log`


```
2013-02-05 22:50:45.636 [main] INFO  c.a.o.c.i.spring.support.PropertyPlaceholderConfigurer - Loading properties file from class path resource [canal.properties]
2013-02-05 22:50:45.641 [main] INFO  c.a.o.c.i.spring.support.PropertyPlaceholderConfigurer - Loading properties file from class path resource [example/instance.properties]
2013-02-05 22:50:45.803 [main] INFO  c.a.otter.canal.instance.spring.CanalInstanceWithSpring - start CannalInstance for 1-example 
2013-02-05 22:50:45.810 [main] INFO  c.a.otter.canal.instance.spring.CanalInstanceWithSpring - start successful....
```



#### 使用说明

1. git clone https://github.com/fqlee/canal-client.git
2. 更改配置文件application.properties，修改canal服务端的连接信息和rabbitmq连接信息
3. 启动CanalClientApplication
4. 编辑用户表user数据，触发binlog
5. 查看rabbitmq中是否有对应的队列新增，注意队列名命名为${database}.{table}.{eventType}，例如测试的是test库的user表
  
```
    插入user数据则队列为 TEST.USER.INSERT
    修改user数据则队列为 TEST.USER.UPDATE
    删除user数据则队列为 TEST.USER.DELETE
```

6. 查看mongodb是否有对应的数据

#### 参与贡献

1. Fork 本项目
2. 新建 Feat_xxx 分支
3. 提交代码
4. 新建 Pull Request
