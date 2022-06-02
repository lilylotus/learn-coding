package cn.nihility.rabbitmq.producer.controller;

import cn.nihility.rabbitmq.producer.direct.DirectConfiguration;
import cn.nihility.rabbitmq.producer.direct.DirectSendService;
import cn.nihility.rabbitmq.producer.fanout.FanoutSendService;
import cn.nihility.rabbitmq.producer.topic.TopicSendService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RabbitmqSendController {

    private DirectSendService directSendService;
    private FanoutSendService fanoutSendService;
    private TopicSendService topicSendService;

    public RabbitmqSendController(DirectSendService directSendService,
                                  FanoutSendService fanoutSendService,
                                  TopicSendService topicSendService) {
        this.directSendService = directSendService;
        this.fanoutSendService = fanoutSendService;
        this.topicSendService = topicSendService;
    }

    @RequestMapping("/direct/normal/correlation")
    public ResponseEntity<Object> sendNormalDirectQueueWithCorrelationData() {
        directSendService.sendNormalDirectQueueWithCorrelationData();
        return ResponseEntity.ok("sendNormalDirectQueueWithCorrelationData");
    }

    @RequestMapping("/direct/normal")
    public ResponseEntity<Object> sendNormalDirectQueue() {
        directSendService.sendNormalDirectQueue();
        return ResponseEntity.ok("sendNormalDirectQueue");
    }

    @RequestMapping("/direct/normal/json")
    public ResponseEntity<Object> sendNormalDirectQueueUseJsonTemplate() {
        directSendService.sendNormalDirectQueueUseJsonTemplate();
        return ResponseEntity.ok("sendNormalDirectQueueUseJsonTemplate");
    }

    @RequestMapping("/direct/normal/business")
    public ResponseEntity<Object> sendBusinessDirectQueue() {
        directSendService.sendBusinessDirectQueue();
        return ResponseEntity.ok("sendBusinessDirectQueue");
    }

    @RequestMapping("/direct/normal/lonely")
    public ResponseEntity<Object> sendLonelyExchangeWithQueue() {
        directSendService.sendLonelyExchangeWithQueue();
        return ResponseEntity.ok("sendLonelyExchangeWithQueue");
    }

    @RequestMapping("/fanout")
    public ResponseEntity<Object> fanoutSend() {
        fanoutSendService.fanoutSend();
        return ResponseEntity.ok("fanoutSend");
    }

    @RequestMapping("/topic/a")
    public ResponseEntity<Object> topicASend() {
        topicSendService.topicASend();
        return ResponseEntity.ok("topicASend");
    }

    @RequestMapping("/topic/b")
    public ResponseEntity<Object> topicBSend() {
        topicSendService.topicBSend();
        return ResponseEntity.ok("topicBSend");
    }

    /* ------ 业务数据不丢失 -------- */
    @RequestMapping("/direct/jdbc/ok")
    public ResponseEntity<Object> directJdbcOk() {
        directSendService.sendJdbcMessage(DirectConfiguration.JDBC_DIRECT_EXCHANGE,
            DirectConfiguration.JDBC_DIRECT_ROUTE_KEY);
        return ResponseEntity.ok("directJdbcOk");
    }

    @RequestMapping("/direct/jdbc/exchange")
    public ResponseEntity<Object> directJdbcExchange() {
        directSendService.sendJdbcMessage("WrongJdbcExchange",
            DirectConfiguration.JDBC_DIRECT_ROUTE_KEY);
        return ResponseEntity.ok("directJdbcExchange");
    }

    @RequestMapping("/direct/jdbc/route")
    public ResponseEntity<Object> directJdbcRoute() {
        directSendService.sendJdbcMessage(DirectConfiguration.JDBC_DIRECT_EXCHANGE,
            "WrongRouteKey");
        return ResponseEntity.ok("directJdbcRoute");
    }

}
