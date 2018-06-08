package com.servingcloud.invoice.transfer.canalclient;

import com.alibaba.fastjson.JSON;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.servingcloud.invoice.transfer.canalclient.properties.CanalServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.concurrent.Executors.newFixedThreadPool;

/**
 * @author fqlee
 * @since 2018/6/6
 */
@Slf4j
@Component
public class CanalRunner {

    private static final Pattern LINE_PATTERN = Pattern.compile("_(\\w)");

    private static AmqpTemplate rabbitTemplate = (AmqpTemplate) SpringContextUtil.getBean("rabbitTemplate");
    /**
     * canal配置
     */
    private static CanalServer canalServer;
    /**
     * 启动次数
     */
    private static int runners = 0;

    public static int getRunners() {
        return runners;
    }

    /**
     * 设置启动信息
     * @param canalServer canal配置
     */
    protected void setup(CanalServer canalServer) {
        CanalRunner.canalServer = canalServer;
        starter();
    }

    /**
     * 重新启动线程
     */
    public static void restart(){
        CanalRunner canalRunner = new CanalRunner();
        canalRunner.starter();
    }

    private void starter(){
        ExecutorService executorService = newFixedThreadPool(10);
        executorService.execute(()-> run(canalServer));
    }

    /**
     * 启动canal调用
     * @param canalServer canal配置
     */
    private void run(CanalServer canalServer){
        CanalConnector connector = CanalConnectors.newSingleConnector(
                new InetSocketAddress(
                        canalServer.getHost(),
                        Integer.parseInt(canalServer.getPort())),
                canalServer.getInstance(),
                "",
                ""
        );
        try {
            connector.connect();
            connector.rollback();
            Message message;
            ++runners;
            while (true) {
                // 获取指定数量的数据
                message = connector.getWithoutAck(Integer.parseInt(canalServer.getBatchSize()));
                if (message.getId() == -1 || message.getEntries().size() == 0) {
                    try {
                        // 等待时间
                        Thread.sleep(Integer.parseInt(canalServer.getSleep()));
                    } catch (InterruptedException e) {
                        log.error("线程睡眠异常{}", e);
                    }
                } else {
                    parseEntryList(message.getEntries());
                }
                // 提交确认
                connector.ack(message.getId());
            }
        } finally {
            --runners;
            log.error("connect error!");
            connector.disconnect();
        }
    }

    /**
     * 批量解析事件
     * @param list 条目集合
     */
    private void parseEntryList(List<CanalEntry.Entry> list) {
        list.forEach(this::parseEntry);
    }

    /**
     * 解析单个事件
     * @param entry 条目
     */
    private void parseEntry(CanalEntry.Entry entry) {
        if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN || entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND) {
            return;
        }

        CanalEntry.RowChange rowChange;
        try {
            rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
        } catch (Exception e) {
            throw new RuntimeException("ERROR ## parser of eromanga-event has an error , data:" + entry.toString(), e);
        }

        //单条 binlog sql
        CanalEntry.EventType eventType = rowChange.getEventType();
        log.info("*****************开始解析->binlog[{}:{}],name[{},{}],eventType:{}*****************",
                entry.getHeader().getLogfileName(),
                entry.getHeader().getLogfileOffset(),
                entry.getHeader().getSchemaName(),
                entry.getHeader().getTableName(),
                eventType
        );
        rowChange.getRowDatasList().forEach(rowData -> parseRowData(entry.getHeader(),eventType, rowData));
        log.info("*****************解析结束***************************************************************************************",
                entry.getHeader().getLogfileName(),
                entry.getHeader().getLogfileOffset(),
                entry.getHeader().getSchemaName(),
                entry.getHeader().getTableName(),
                eventType
        );
    }

    /**
     * 解析单行SQL数据
     * @param header 头
     * @param eventType 事件类型
     * @param rowData 行数据
     */
    private void parseRowData(CanalEntry.Header header, CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        String tableName = header.getSchemaName() + "." + header.getTableName();
        if (eventType == CanalEntry.EventType.DELETE) {
            saveRowData(tableName + ".DELETE",rowData.getBeforeColumnsList());
        } else if (eventType == CanalEntry.EventType.INSERT) {
            saveRowData(tableName + ".INSERT",rowData.getAfterColumnsList());
        } else if (eventType == CanalEntry.EventType.UPDATE){
            saveRowData(tableName + ".UPDATE",rowData.getAfterColumnsList());
        }
    }

    /**
     * 保存行数据
     * @param tableEventType 表数据事件类型
     * @param columns 表字段数据
     */
    private void saveRowData(String tableEventType,List<CanalEntry.Column> columns) {
        Map map = new HashMap<>(15);
        columns.forEach(column -> map.put(lineToHump(column.getName()),column.getValue()));
        log.info("json解析:{}",JSON.toJSON(map));
        rabbitTemplate.convertAndSend(tableEventType.toUpperCase(),JSON.toJSONString(map));
    }

    /**
     * 下划线转驼峰
     * @return string
     */
    private static String lineToHump(String str){
        str = str.toLowerCase();
        Matcher matcher = LINE_PATTERN.matcher(str);
        StringBuffer sb = new StringBuffer();
        while(matcher.find()){
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
