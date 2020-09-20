package cn.nihility.boot.redis;

public interface CommonCacheService {

	/**
	 * 删除
	 */
	void del(String... keys);

	/**
	 * 为key设置超时时间
	 */
	Boolean expire(String key, long seconds);

	/**
	 * 根据key获取过期时间
	 *
	 * @param key 键不能为 null
	 * @return 时间(秒) 返回 0 代表为永久有效
	 */
	Long getExpire(String key);

	/**
	 * key 值是否存在
	 * @param key 查询的 key
	 * @return true 存在
	 */
	Boolean hasKey(String key);
}
