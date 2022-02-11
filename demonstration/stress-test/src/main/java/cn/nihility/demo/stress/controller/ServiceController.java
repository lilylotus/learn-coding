package cn.nihility.demo.stress.controller;

import cn.nihility.common.pojo.UnifyResult;
import cn.nihility.common.util.UnifyResultUtil;
import cn.nihility.common.util.UuidUtil;
import cn.nihility.demo.stress.service.JdbcService;
import cn.nihility.demo.stress.service.RedisService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.function.Predicate;

@RestController
public class ServiceController {

    private static final Logger logger = LoggerFactory.getLogger(ServiceController.class);

    private static final long SECKILL_GAP = 10000;
    private static final String SECKILL_LOCK_KEY = "goods:SECKILL_LOCK";

    private JdbcService jdbcService;
    private StringRedisTemplate stringRedisTemplate;
    private RedisService redisService;

    public ServiceController(JdbcService jdbcService, StringRedisTemplate stringRedisTemplate, RedisService redisService) {
        this.jdbcService = jdbcService;
        this.stringRedisTemplate = stringRedisTemplate;
        this.redisService = redisService;
    }

    @GetMapping("/redis/setnx")
    public UnifyResult<Boolean> redisSetNX() {
        logger.info("进入 redisSetNX");
        boolean result = redisService.setNX("nx", "nx-value", 60);
        logger.info("Set NX Result [{}]", result);
        return UnifyResultUtil.success(result);
    }

    @GetMapping("/redis/del/setnx")
    public UnifyResult<Boolean> redisDelSetNX() {
        logger.info("进入 redisDelSetNX");
        boolean result = redisService.delNX("nx", "nx-value");
        logger.info("DEL NX Result [{}]", result);
        return UnifyResultUtil.success(result);
    }

    @GetMapping("/redis/del2/setnx")
    public UnifyResult<Boolean> redisDel2SetNX() {
        logger.info("进入 redisDel2SetNX");
        boolean result = redisService.delNX("nx", "nx-error");
        logger.info("DEL NX redisDel2SetNX [{}]", result);
        return UnifyResultUtil.success(result);
    }

    @PostMapping("/shop/service")
    public UnifyResult<String> service() {
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

    @GetMapping("/buy/seckill")
    public ResponseEntity<UnifyResult<String>> redisSeckill() {

        long start = System.currentTimeMillis();
        String uuid = UuidUtil.jdkUUID(10);
        String val = uuid + "=" + start;
        boolean success = false;
        boolean loopFailure = true;
        String resultMsg = "购买失败";
        BoundValueOperations<String, String> ops = stringRedisTemplate.boundValueOps("goods:count");
        int goodsCount = stringToInt(ops.get());
        if (goodsCount < 1) {
            resultMsg = "预判当前商品已抢购完成，没有库存了，请下次再试";
            logger.error(resultMsg);
            jdbcService.recordServiceLog(resultMsg);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(UnifyResultUtil.success(resultMsg));
        }
        while ((System.currentTimeMillis() - start) < SECKILL_GAP) {
            if (redisService.setNX(SECKILL_LOCK_KEY, val, 5)) {
                loopFailure = false;
                goodsCount = stringToInt(ops.get());
                if (goodsCount < 1) {
                    resultMsg = "用户 [" + uuid + "]:[" + val + "] 获取到购买资格，但是商品已售完，无法购买";
                    logger.error("用户 [{}]:[{}] 获取到购买资格，但是商品已售完，无法购买", uuid, val);
                } else {
                    int remain = goodsCount - 1;
                    resultMsg = "用户 [" + uuid + "]:[" + val + "] 获取到购买资格，购买到一件商品，剩余库存 [" + remain + "]";
                    logger.error("用户 [{}]:[{}] 获取到购买资格，购买到一件商品，剩余库存 [{}]", uuid, val, remain);
                    ops.set(Integer.toString(remain));
                    success = true;
                }

                redisService.delNX(SECKILL_LOCK_KEY, val);
                break;
            }
            try {
                Thread.sleep(10L);
            } catch (InterruptedException e) {
            }
        }

        if (loopFailure) {
            logger.error("[{}]:[{}] 经过多次轮询抢购，还是未抢购到指定商品", uuid, val);
            resultMsg = "[" + uuid + "]:[" + val + "] 经过多次轮询抢购，还是未抢购到指定商品";
        }

        jdbcService.recordServiceLog(resultMsg);

        if (success) {
            return ResponseEntity.ok(UnifyResultUtil.success(resultMsg));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(UnifyResultUtil.success(resultMsg));
        }

    }

    /**
     * 并发完全错误
     */
    @PostMapping("/buy/service/redis")
    public ResponseEntity<UnifyResult<String>> buyServiceByRedis() {
        logger.info("进入 buyServiceByRedis");
        BoundValueOperations<String, String> ops = stringRedisTemplate.boundValueOps("buy:count");
        int remainCount = stringToInt(ops.get());

        if (remainCount < 1) {
            logger.warn("库存 [{}] 不足，无法购买", remainCount);
            jdbcService.recordServiceLog("库存 [" + remainCount + "] 不足，无法购买");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(UnifyResultUtil.success("库存不足，无法购买"));
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

    private ResponseEntity<UnifyResult<String>> buyLogic(Predicate<RedisConnection> func) {
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
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(UnifyResultUtil.success("库存不足，无法购买"));
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
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(UnifyResultUtil.success("没有抢购到购买资格，下次再试"));
                }
            }
        }
        jdbcService.recordServiceLog("系统异常");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(UnifyResultUtil.success("系统异常"));
    }

    @PostMapping("/buy/service/redisNX")
    public ResponseEntity<UnifyResult<String>> buyServiceByRedisNX() {
        logger.info("进入 buyServiceByRedisNX");
        return buyLogic(this::obtainBuyLock);
    }

    @PostMapping("/buy/service/redisNX2")
    public ResponseEntity<UnifyResult<String>> buyServiceByRedisNX2() {
        logger.info("进入 buyServiceByRedisNX2");
        return buyLogic(this::obtainBuyLockWhile);
    }

}
