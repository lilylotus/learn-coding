package cn.nihility.redis;

import cn.nihility.util.AlgorithmUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.params.SetParams;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * RedisUtilTest
 *
 * @author dandelion
 * @date 2020-03-28 23:06
 */
public class RedisUtilTest {

    private static RedisUtil redisUtil;

    @BeforeAll
    public static void init() {
        redisUtil = new RedisUtil();
    }

    @AfterAll
    public static void destroy() {
        RedisUtil.destroy();
    }

    @Test
    public void testExist() {
        Assertions.assertTrue(redisUtil.keyExist("redis"));
    }

    @Test
    public void testNotExistKey() {
        Assertions.assertFalse(redisUtil.keyExist("not:exist"));
    }

    @Test
    public void testDelKey() {
        assertTrue(redisUtil.keyDel("a:v1"));
    }

    @Test
    public void testExpireKey() {
        assertTrue(redisUtil.keyExpire("redis", 600));
    }

    @Test
    public void testKeys() {
        assertEquals(2, redisUtil.keyKeys("redis*").size());
    }

    @Test
    public void testKeyPExpire() {
        assertTrue(redisUtil.keyPExpire("redis", 1000 * 600));
    }

    @Test
    public void testKeyPersist() {
        assertTrue(redisUtil.keyPersist("redis"));
    }

    @Test
    public void testKeyTTL() {
        assertEquals(-1, redisUtil.keyTTL("redis"));
        assertEquals(-1, redisUtil.keyPTTL("redis"));
    }

    @Test
    public void testKeyRandomKey() {
        System.out.println(redisUtil.keyRandomKey());
    }

    @Test
    public void testKeyRename() {
        System.out.println(redisUtil.keyRename("redis", "redis:rename"));
    }

    @Test
    public void testKeyType() {
        assertEquals("string", redisUtil.keyType("pexpire"));
    }

    @Test
    public void testIncr() {
        assertEquals(1002, redisUtil.stringIncr("string:incr"));
        assertEquals(1012, redisUtil.stringIncrBy("string:incr", 10));
    }

    @Test
    public void testStringDec() {
        assertEquals(1000, redisUtil.stringDecr("string:incr"));
        assertEquals(990, redisUtil.stringDecrBy("string:incr", 10));
    }

    @Test
    public void testStringGetSet() {
        assertEquals("new redis values", redisUtil.stringGetSet("redis", "new redis values new"));
    }

    @Test
    public void testStringMget() {
        System.out.println(redisUtil.stringMget("redis", "string:int", "string:incr", "none"));
    }

    @Test
    public void testStringMset() {
        assertEquals("OK", redisUtil.stringMset("string:mset1", "mset1 value", "string:mset2", "mset2 value"));
    }

    @Test
    public void testStringPSetEx() {
        assertEquals("OK", redisUtil.stringPSetEx("string:psetex02", 1000 * 120, "psetex value"));
    }

    @Test
    public void testStringSet() {
        assertEquals("OK", redisUtil.stringSet("string:jedis:set", "string jedis set value"));
        SetParams setParams = new SetParams();
        setParams.ex(600);
        assertEquals("OK", redisUtil.stringSet("string:jedis:setparams", "string jedis set params value", setParams));
    }

    @Test
    public void testSetNx() {
//        assertFalse(redisUtil.stringSetNX("redis", "string redis value"));
        assertTrue(redisUtil.stringSetNX("string:jedis:setnx", "string redis value"));
    }

    @Test
    public void testStrLen() {
        assertEquals(18, redisUtil.stringLen("string:jedis:setnx"));
    }

    /* ============================= list test ============================== */
    @Test
    public void testListLpush() {
        assertEquals(5, redisUtil.listLPush("list:list01", "value01", "value02"));
        assertEquals(7, redisUtil.listLPushX("list:list01", "value03", "value04"));
    }

    @Test
    public void testListLRange() {
        System.out.println(redisUtil.listLRange("list:list01", 0, -1));
    }

    @Test
    public void testListRpush() {
        assertEquals(2, redisUtil.listRPush("list:list02", "value05", "value06"));
//        assertEquals(2, redisUtil.listRPushX("list:list02", "value07", "value08"));
    }

    @Test
    public void testListPop() {
        assertEquals("value08", redisUtil.listLPOP("list:list01"));
        assertEquals("value06", redisUtil.listRPOP("list:list01"));
    }

    @Test
    public void testListIndex() {
        assertEquals("value07", redisUtil.listLIndex("list:list01", 0));
    }

    @Test
    public void testBLpop() {
        System.out.println(redisUtil.listBLPop(0, "list:list02"));
    }

    /* ================================ hash test ===================================== */
    private String hashKey = "hash:jedis";

    @Test
    public void testHashSet() {
        Map<String, String> hash = new HashMap<>();
        hash.put("k1", "value 01");
        hash.put("k4", "value 04");
        hash.put("k3", "value 03");
        hash.put("k2", "value 02");
        long len = redisUtil.hashHSet("hash:jedis", hash);
        assertEquals(4, len);
    }

    @Test
    public void testHashVals() {
        System.out.println(redisUtil.hashHVals(hashKey));
    }

    @Test
    public void testHashKeys() {
        System.out.println(redisUtil.hashHKeys(hashKey));
    }

    @Test
    public void testHashDel() {
        assertEquals(1, redisUtil.hashHDel(hashKey, "k2"));
    }

    @Test
    public void testHashGet() {
        assertEquals("value 03", redisUtil.hashHGet(hashKey, "k3"));
    }

    /* =================================== set test ================================= */
    private String setKey = "set:jedis";
    @Test
    public void testSetAdd() {
//        assertEquals(4, redisUtil.setSAdd(setKey, "m1", "m2", "m3", "m4"));
        assertEquals(3, redisUtil.setSAdd(setKey, "m1", "m2", "m3", "m4"));
    }

    @Test
    public void testSetGet() {
        System.out.println(redisUtil.setSmembers(setKey));
    }

    @Test
    public void testSetRem() {
        assertEquals(2, redisUtil.setSRem(setKey, "m1", "m3"));
    }

    @Test
    public void testSetPop() {
        System.out.println(redisUtil.setSPop(setKey));
    }

    @Test
    public void testSetExist() {
        assertTrue(redisUtil.setSismember(setKey, "m4"));
    }

    @Test
    public void testSetLen() {
        assertEquals(1, redisUtil.setSCard(setKey));
    }

    @Test
    public void testSetMove() {
        assertTrue(redisUtil.setSMove(setKey + "01", setKey, "m9"));
    }

    @Test
    public void testSetDiff() {
        // jedis -> m1 m2 m3 m4 m9
        // jedis01 -> m1 m4 m6 m7 m8
        // [m9, m2, m3]
        System.out.println(redisUtil.setSDiff(setKey, setKey + "01"));
    }

    @Test
    public void testSetInter() {
        System.out.println(redisUtil.setSInter(setKey, setKey + "01"));
    }

    @Test
    public void testSetUnion() {
        System.out.println(redisUtil.setSUnion(setKey, setKey + "01"));
    }

    /* ------- pipeline ------------------ */
    @Test
    public void testPipelined() {
        try (Jedis jedis = RedisUtil.getConnection()) {
            long begin = System.currentTimeMillis();
            int count = 100, loop = 100;
            Pipeline pipelined = jedis.pipelined();
            char prefix = AlgorithmUtil.randomAlphabet();
            System.out.println("Prefix " + prefix);

            int index = 0;
            for (int j = 0; j < loop; j++) {
                pipelined.clear();
                for (int i = 0; i < count; i++, index++) {
                    pipelined.set("pipelined:key_index_" + prefix + index, "pipelined index value " + prefix + index);
                }
                List<Object> objects = pipelined.syncAndReturnAll();
                objects.forEach(o -> System.out.print(o + "  "));
                System.out.println("============================");
            }

            long end = System.currentTimeMillis();
            System.out.println("duration " + (end - begin));
        }
    }

    @Test
    public void testNoPipelined() {
        try (Jedis jedis = RedisUtil.getConnection()) {
            long begin = System.currentTimeMillis();
            int count = 100, loop = 100;
            char prefix = AlgorithmUtil.randomAlphabet();
            System.out.println("Prefix " + prefix);

            int index = 0;
            for (int j = 0; j < loop; j++) {
                for (int i = 0; i < count; i++, index++) {
                    jedis.set("no_pipelined:key_index_" + prefix + index, "no_pipelined index value " + prefix + index);
                }
                System.out.println("============================");
            }

            long end = System.currentTimeMillis();
            System.out.println("duration " + (end - begin));
        }
    }

}
