package cn.nihility.redis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.jedis.params.SetParams;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * RedisUtil
 * Redis 工具类
 * 基本的 keys, string, list, hash, set, sorted set 命令
 *
 * @author dandelion
 * @date 2020-03-28 22:58
 */
public class RedisUtil {

    private static final String REDIS_RESOURCE = "/redis/redis.properties";
    private static JedisPool jedisPool;

    static {
        Properties properties = new Properties();
        try {
            String redisConfigPath = RedisUtil.class.getResource(REDIS_RESOURCE).getFile();
            // System.out.println("redis config location : " + redisConfigPath);
            properties.load(new FileInputStream(redisConfigPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(50);
        jedisPool = new JedisPool(config, properties.getProperty("host"),
                Integer.parseInt(properties.getProperty("port")), Protocol.DEFAULT_TIMEOUT,
                properties.getProperty("auth"), 0);
    }

    public static void destroy() {
        if (null != jedisPool) {
            jedisPool.destroy();
        }
    }

    public static Jedis getConnection() {
        return jedisPool.getResource();
    }

    /* ============================== key command ============================== */

    /**
     * 判断 key 是否存在
     * @return true -> key 存在
     */
    public boolean keyExist(String key) {
        return basicOperation(key, Jedis::exists);
    }

    /**
     * 删除一个 key
     * @param key 要删除的 key 值
     * @return true -> 删除成功， false -> 其他情况
     */
    public Boolean keyDel(String key) {
        return basicOperation(key, Jedis::del) == 0L;
    }

    /**
     * 设置 key 的过期时间，(秒)
     * @param key key 值
     * @param seconds 多少秒后过期
     * @return true -> 设置成功
     */
    public Boolean keyExpire(String key, final int seconds) {
        return basicOperation(key, (j, v) -> j.expire(v, seconds)) == 1;
    }

    public Set<String> keyKeys(String keyPattern) {
        return basicOperation(keyPattern, Jedis::keys);
    }

    /**
     * 删除 key 的过期时间
     * @param key 设置的 key 值
     * @return true 配置成功
     */
    public Boolean keyPersist(String key) {
        return basicOperation(key, Jedis::persist) == 1;
    }

    /**
     * 设置 key 的过期时间，单位为毫秒
     * @param key 设置的 key 值
     * @param milliSeconds 过期事件
     * @return true 设置成功
     */
    public Boolean keyPExpire(String key, final long milliSeconds) {
        return basicOperation(key, (j, v) -> j.pexpire(v, milliSeconds)) == 1;
    }

    public long keyTTL(String key) {
        return basicOperation(key, Jedis::ttl);
    }

    public long keyPTTL(String key) {
        return basicOperation(key, Jedis::pttl);
    }

    /**
     * 随机获取一个 key 值
     * @return 随机 key 值，nil 数据库没有 key 值。
     */
    public String keyRandomKey() {
        return basicOperation(Jedis::randomKey);
    }

    public String keyRename(final String key, final String rename) {
        return basicOperation(key, (j, v) -> j.rename(v, rename));
    }

    public Long keyRenameNX(final String key, final String rename) {
        return basicOperation(key, (j, k) -> j.renamenx(key, rename));
    }

    public String keyType(String key) {
        return basicOperation(key, Jedis::type);
    }

    /* ============================== strings command ============================== */

    public long stringAppend(final String key, final String appendValue) {
        return basicOperation(key, (j, k) -> j.append(k, appendValue));
    }

    public long stringDecr(final String key) {
        return basicOperation(key, Jedis::decr);
    }

    public long stringDecrBy(final String key, final long decr) {
        return basicOperation(key, (j, k) -> j.decrBy(key, decr));
    }

    public String stringGet(final String key) {
        return basicOperation(key, Jedis::get);
    }

    public String stringGetRange(final String key, final int start, final int end) {
        return basicOperation(key, (j, k) -> j.getrange(k, start, end));
    }

    public String stringGetSet(final String key, final String value) {
        return basicOperation(key, (j, k) -> j.getSet(k, value));
    }

    public long stringIncr(final String key) {
        return basicOperation(key, Jedis::incr);
    }

    public long stringIncrBy(final String key, final int incr) {
        return basicOperation(key, (j, k) -> j.incrBy(k, incr));
    }

    public List<String> stringMget(final String ... keys) {
        return basicOperation(keys, Jedis::mget);
    }

    public String stringPSetEx(final String key, final long expire, final String value) {
        return basicOperation(key, (j, k) -> j.psetex(k, expire, value));
    }

    public String stringSet(final String key, final String value) {
        return basicOperation(key, (j, k) -> j.set(k, value));
    }

    public String stringSet(final String key, final String value, final SetParams setParams) {
        return basicOperation(key, (j, k) -> j.set(k, value, setParams));
    }

    public String stringSetEx(final String key, final String value, final int expireSeconds) {
        return basicOperation(key, (j, k) -> j.setex(key, expireSeconds, value));
    }

    public Boolean stringSetNX(final String key, final String value) {
        return basicOperation(key, (j, k) -> j.setnx(k, value)) == 1;
    }

    public long stringLen(final String key) {
        return basicOperation(key, Jedis::strlen);
    }

    public String stringSet(final String key, final String value, final int expireSeconds) {
        SetParams setParams = new SetParams();
        setParams.ex(expireSeconds);
        return basicOperation(key, (j, k) -> j.set(k, value, setParams));
    }

    /**
     * 返回的是 OK
     * @return OK 字符串
     */
    public String stringMset(final String ... keyValues) {
        return basicOperation(keyValues, Jedis::mset);
    }

    /* ============================== list command ============================== */

    public long listLPush(String key, final String ... value) {
        return basicOperation(key, (j, k) -> j.lpush(k, value));
    }

    public long listLPushX(String key, final String... values) {
        return basicOperation(key, (j, k) -> j.lpushx(k, values));
    }

    public long listRPush(String key, final String ... value) {
        return basicOperation(key, (j, k) -> j.rpush(k, value));
    }

    public long listRPushX(String key, final String... values) {
        return basicOperation(key, (j, k) -> j.rpushx(k, values));
    }

    public String listLPOP(String key) {
        return basicOperation(key, Jedis::lpop);
    }

    public String listRPOP(String key) {
        return basicOperation(key, Jedis::rpop);
    }

    public long listLen(String key) {
        return basicOperation(key, Jedis::llen);
    }

    public String listLIndex(String key, final long index) {
        return basicOperation(key, (j, k) -> j.lindex(k, index));
    }

    /**
     * count > 0 从 head 删除到 tail，count < 0 从 tail 删除到 head， count = 0 删除所有匹配元素
     * @return 删除的元素数量
     */
    public long listLRem(String key, final long count, final String delValue) {
        return basicOperation(key, (j, k) -> j.lrem(k, count, delValue));
    }

    public String listLSet(String key, final long index, final String value) {
        return basicOperation(key, (j, k) -> j.lset(k, index, value));
    }

    public String listLTrim(String key, final long start, final long end) {
        return basicOperation(key, (j, k) -> j.ltrim(k, start, end));
    }

    public List<String> listBLPop(final int timeout, final String... keys) {
        return basicOperation(keys, (j, k) -> j.blpop(timeout, k));
    }

    public List<String> listBRPop(final int timeout, final String ... key) {
        return basicOperation(key, (j, k) -> j.brpop(timeout, key));
    }

    /* ============================== hash command ============================== */

    public long hashHDel(String key, final String field) {
        return basicOperation(key, (j, k) -> j.hdel(k, field));
    }

    public Boolean hashFieldExists(String key, final String field) {
        return basicOperation(key, (j, k) -> j.hexists(k, field));
    }

    public String hashHGet(String key, final String field) {
        return basicOperation(key, (j, k) -> j.hget(k, field));
    }

    public Map<String, String> hashHGetAll(String key) {
        return basicOperation(key, Jedis::hgetAll);
    }

    public Set<String> hashHKeys(String key) {
        return basicOperation(key, Jedis::hkeys);
    }

    public long hashHLen(String key) {
        return basicOperation(key, Jedis::hlen);
    }

    public List<String> hashHMGet(String key, final String ... fields) {
        return basicOperation(key, (j, k) -> j.hmget(k, fields));
    }

    public String hashHMSet(String key, final Map<String, String> hash) {
        return basicOperation(key, (j, k) -> j.hmset(k, hash));
    }

    public long hashHSet(String key, final Map<String, String> hash) {
        return basicOperation(key, (j, k) -> j.hset(k, hash));
    }

    public Boolean hashHSetNX(String key, final String field, final String value) {
        return basicOperation(key, (j, k) -> j.hsetnx(k, field, value)) == 1;
    }

    public List<String> hashHVals(String key) {
        return basicOperation(key, Jedis::hvals);
    }

    /* ============================== set command ============================== */

    /**
     * Add one or more members to a set
     * @return 此次成功添加入 set 中的元素个数
     */
    public long setSAdd(String key, final String... members) {
        return basicOperation(key, (j, k) -> j.sadd(k, members));
    }

    /**
     * Get all the members in a set
     * @return 返回该 set 中的所有元素
     */
    public Set<String> setSmembers(String key) {
        return basicOperation(key, Jedis::smembers);
    }

    /**
     * Get the number of members in a set
     * @return 返回 set 中元素的数量
     */
    public long setSCard(String key) {
        return basicOperation(key, Jedis::scard);
    }

    public Set<String> setSDiff(String... keys) {
        return basicOperation(keys, Jedis::sdiff);
    }

    public long setSDiffStore(final String destination, String... keys) {
        return basicOperation(keys, (j, k) -> j.sdiffstore(destination, k));
    }

    public Set<String> setSInter(String... keys) {
        return basicOperation(keys, Jedis::sinter);
    }

    public long setSInterStore(final String destination, String... keys) {
        return basicOperation(keys, (j, k) -> j.sinterstore(destination, k));
    }

    public Set<String> setSUnion(String... keys) {
        return basicOperation(keys, Jedis::sunion);
    }

    public long setSUnionStore(final String destination, String... keys) {
        return basicOperation(keys, (j, k) -> j.sunionstore(destination, k));
    }

    /**
     * Determine if a given value is a member of a set
     * @return 1 the give member is in the set
     */
    public Boolean setSismember(String key, final String member) {
        return basicOperation(key, (j, k) -> j.sismember(k, member));
    }

    public Set<String> setSPop(String key, final long count) {
        return basicOperation(key, (j, k) -> j.spop(k, count));
    }

    public String setSPop(String key) {
        return basicOperation(key, Jedis::spop);
    }

    /**
     * 删除 set 中的一个元素
     * @return 1 删除成功
     */
    public long setSRem(String key, final String... members) {
        return basicOperation(key, (j, k) -> j.srem(k, members));
    }

    public Boolean setSMove(String source, final String destination, final String member) {
        return basicOperation(source, (j, k) -> j.smove(k, destination, member)) == 1;
    }

    /* ============================== basic command ============================== */

    /**
     * 获取指定范围内的元素， index 从 0 开始， 0 - 10 放回的是 11 个元素
     * @param key list 的 key 值
     * @param start 元素 index 开始
     * @param stop 结束的 index 位置
     * @return list 范围内元素值
     */
    public List<String> listLRange(String key, final long start, final long stop) {
        return basicOperation(key, (j, k) -> j.lrange(k, start, stop));
    }

    private <T, R> R basicOperation(T key, BiFunction<Jedis, T, R> operator) {
        try (Jedis jedis = jedisPool.getResource()) {
            return operator.apply(jedis, key);
        } catch (Exception ex) {
            throw new JedisException("Execute redis command error", ex);
        }
    }

    private <R> R basicOperation(Function<Jedis, R> operator) {
        try (Jedis jedis = jedisPool.getResource()) {
            return operator.apply(jedis);
        } catch (Exception ex) {
            throw new JedisException("Execute redis command error", ex);
        }
    }

}
