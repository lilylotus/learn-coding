package cn.nihility.demo.stress.controller;

import cn.nihility.common.pojo.UnifyResult;
import cn.nihility.common.util.UnifyResultUtil;
import cn.nihility.demo.stress.service.JdbcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.function.Predicate;

@RestController
public class ServiceController {

    private static final Logger logger = LoggerFactory.getLogger(ServiceController.class);

    private JdbcService jdbcService;
    private StringRedisTemplate stringRedisTemplate;

    public ServiceController(JdbcService jdbcService, StringRedisTemplate stringRedisTemplate) {
        this.jdbcService = jdbcService;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @PostMapping("/shop/service")
    public UnifyResult service() {
        logger.info("进入 Shopping");
        jdbcService.addServiceLog();
        return UnifyResultUtil.success("ok");
    }

    private int stringToInt(String num) {
        if (null == num) {
            return 0;
        }
        try {
            return Integer.parseInt(num);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    /**
     * 并发完全错误
     */
    @PostMapping("/buy/service/redis")
    public ResponseEntity<UnifyResult> buyServiceByRedis() {
        logger.info("进入 buyServiceByRedis");
        BoundValueOperations<String, String> ops = stringRedisTemplate.boundValueOps("buy:count");
        int remainCount = stringToInt(ops.get());

        if (remainCount < 1) {
            logger.warn("库存 [{}] 不足，无法购买", remainCount);
            jdbcService.recordServiceLog("库存 [" + remainCount + "] 不足，无法购买");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(UnifyResultUtil.failure("库存不足，无法购买"));
        } else {
            int remain = stringToInt(ops.get()) - 1;
            ops.set(Integer.toString(remain));
            jdbcService.recordServiceLog("购买 1 件，库存 [" + remainCount + "] 剩余 [" + remain + "]");
            logger.info("购买 1 件，库存 [{}] 剩余 [{}]", remainCount, remain);
            return ResponseEntity.ok(UnifyResultUtil.success("购买成功"));
        }
    }

    private Boolean obtainBuyLock(final RedisConnection conn) {
        Boolean result = Boolean.FALSE;
        for (int i = 0; i < 10; i++) {
            result = conn.set("buy:nx_lock_key".getBytes(StandardCharsets.UTF_8),
                "nx_lock_value".getBytes(StandardCharsets.UTF_8),
                Expiration.seconds(10L), RedisStringCommands.SetOption.SET_IF_ABSENT);
            if (Boolean.TRUE.equals(result)) {
                break;
            } else {
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                }
            }
        }
        return result;
    }

    private Boolean obtainBuyLockWhile(final RedisConnection conn) {
        Boolean result;
        while (true) {
            result = conn.set("buy:nx_lock_key".getBytes(StandardCharsets.UTF_8),
                "nx_lock_value".getBytes(StandardCharsets.UTF_8),
                Expiration.seconds(10L), RedisStringCommands.SetOption.SET_IF_ABSENT);
            if (Boolean.TRUE.equals(result)) {
                break;
            } else {
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                }
            }
        }
        return result;
    }

    private ResponseEntity<UnifyResult> buyLogic(Predicate<RedisConnection> func) {
        BoundValueOperations<String, String> ops = stringRedisTemplate.boundValueOps("buy:count");
        RedisConnectionFactory factory = stringRedisTemplate.getConnectionFactory();
        if (null != factory) {
            try (RedisConnection conn = factory.getConnection()) {
                Boolean ok = func.test(conn);
                if (Boolean.TRUE.equals(ok)) {
                    logger.info("获取到购买资格，执行购买逻辑。");
                    int remainCount = stringToInt(ops.get());

                    if (remainCount < 1) {
                        logger.warn("库存 [{}] 不足，无法购买", remainCount);
                        jdbcService.recordServiceLog("库存 [" + remainCount + "] 不足，无法购买");
                        conn.del("buy:nx_lock_key".getBytes(StandardCharsets.UTF_8));
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(UnifyResultUtil.failure("库存不足，无法购买"));
                    } else {
                        int remain = stringToInt(ops.get()) - 1;
                        ops.set(Integer.toString(remain));
                        jdbcService.recordServiceLog("购买 1 件，库存 [" + remainCount + "] 剩余 [" + remain + "]");
                        conn.del("buy:nx_lock_key".getBytes(StandardCharsets.UTF_8));
                        return ResponseEntity.ok(UnifyResultUtil.success("购买成功"));
                    }
                } else {
                    jdbcService.recordServiceLog("没有抢购到购买资格，无法购买。");
                    logger.warn("没有抢购到购买资格，下次再试。");
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(UnifyResultUtil.failure("没有抢购到购买资格，下次再试"));
                }
            }
        }
        jdbcService.recordServiceLog("系统异常");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(UnifyResultUtil.failure("系统异常"));
    }

    @PostMapping("/buy/service/redisNX")
    public ResponseEntity<UnifyResult> buyServiceByRedisNX() {
        logger.info("进入 buyServiceByRedisNX");
        return buyLogic(this::obtainBuyLock);
    }

    @PostMapping("/buy/service/redisNX2")
    public ResponseEntity<UnifyResult> buyServiceByRedisNX2() {
        logger.info("进入 buyServiceByRedisNX2");
        return buyLogic(this::obtainBuyLockWhile);
    }

}
