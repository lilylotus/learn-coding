package cn.nihility.unify.idempotent;

/**
 * 幂等性校验 token 的生命周期管理接口
 * 1. 缓存幂等性校验生成 token
 * 2. 删除请求后校验后的 token
 */
public interface IdempotentTokenSupervise {

    /**
     * 缓存 token
     * @param key 缓存 token 对应的 key
     * @param token 要缓存的 token
     * @return true -> 缓存成功
     */
    boolean cacheToken(String key, String token);

    /**
     * 删除幂等性缓存 key 对应的 token
     * @param key 要删除的 token 对应的 key 值
     * @return true -> 删除成功
     */
    boolean deleteToken(String key);

    /**
     * 校验幂等性 token key 是否存在
     * @param key 幂等性键值
     * @return true -> 键值存在
     */
    boolean exists(String key);

}
