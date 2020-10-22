package cn.nihility.unify.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000
 * <p>
 * 1 位标识，由于 long 基本类型在 Java 中是带符号的，最高位是符号位，正数是 0，负数是 1，所以 id 一般是正数，最高位是 0
 * 41 位时间截(毫秒级)，注意，41 位时间截不是存储当前时间的时间截，而是存储时间截的差值（当前时间截 - 开始时间截)，
 * 开始时间截一般是我们的id生成器开始使用的时间，由我们程序来指定的。
 * 41 位的时间截，可以使用 69 年，年T = (1L << 41) / (1000L * 60 * 60 * 24 * 365) = 69
 * 10 位的数据机器位，可以部署在 1024 个节点，包括5位 dataCenterId 和 5 位 workerId
 * 12 位序列，毫秒内的计数，12 位的计数顺序号支持每个节点每毫秒(同一机器，同一时间截)产生 4096 个 ID 序号
 * <p>
 * 加起来刚好 64 位，为一个 Long 型。(转换成字符串后长度最多 19)
 * SnowFlake 的优点是，整体上按照时间自增排序，并且整个分布式系统内不会产生ID碰撞(由 dataCenter 数据中心 ID 和 workerId 机器 ID 作区分)，并且效率较高，
 * 经测试，SnowFlake 每秒能够产生 26 万 ID 左右。
 * <p>
 * 优点：
 * 1.毫秒数在高位，自增序列在低位，整个ID都是趋势递增的。
 * 2.不依赖数据库等第三方系统，以服务的方式部署，稳定性更高，生成ID的性能也是非常高的。
 * 3.可以根据自身业务特性分配bit位，非常灵活。
 * <p>
 * 缺点：
 * 强依赖机器时钟，如果机器上时钟回拨，会导致发号重复或者服务会处于不可用状态。
 */
public class SnowflakeIdWorker {

    private static final Logger log = LoggerFactory.getLogger(SnowflakeIdWorker.class);

    /**
     * 起始的时间戳 (2015-01-01)
     */
    private final static long START_TIME_STAMP = 1420041600000L;

    /*
     * 每一部分占用的位数， 时间戳占用 41 位， 12 + 10 (5 + 5) = 63 位
     */
    /** 序列号占用的位数 */
    private final static long SEQUENCE_BIT = 12;
    /** 机器标识占用的位数 10 = workerId + dataCenterId */
    /** 工作 ID 占用位数 */
    private final static long WORKER_ID_BIT = 5;
    /** 数据中心占用的位数 */
    private final static long DATA_CENTER_BIT = 5;

    /*
     * 每一部分的最大值
     * 计算原理：
     * 1 向左移动 12 位数(结果是 13 位)： 1 << 12 = 1000000000000
     * 只需要 13 位数 -1 即 12 位的最大值，这里使用它的反码（1000000000000的反码 == 0111111111111）
     * ~ 表示该数的反码
     */
    /** 最大的 dataCenter 数值，31 */
    private final static long MAX_DATA_CENTER_NUM = ~(-1L << DATA_CENTER_BIT);
    /** 最大的 worker 数值， 31   */
    private final static long MAX_WORKER_ID_NUM = ~(-1L << WORKER_ID_BIT);
    /** 最大的序列号值 4095 */
    private final static long MAX_SEQUENCE = ~(-1L << SEQUENCE_BIT);

    /*
     * 每一部分向左的位移
     */
    /** 机器 ID 向左移动 12 位 */
    private final static long WORKER_ID_LEFT = SEQUENCE_BIT;
    /** 数据中心 ID 向左移动 17 位， (12 + 5) */
    private final static long DATA_CENTER_LEFT = WORKER_ID_LEFT + WORKER_ID_BIT;
    /** 时间戳向左移动 22 位 (12 + 5 + 5) */
    private final static long TIMESTAMP_LEFT = DATA_CENTER_LEFT + DATA_CENTER_BIT;

    /** 数据中心 ID (0-31) */
    private volatile long dataCenterId;
    /** 工作机器 ID (0-31) */
    private volatile long workerId;
    /** 毫秒内序列 (0-4095) */
    private volatile long sequence = 0L;
    /** 上一次生成 ID 的时间戳 */
    private volatile long lastTimestamp = -1L;

    private static SnowflakeIdWorker snowFlakeId;

    //==============================Constructors=====================================
    /**
     * 实列构造函数
     *
     * @param dataCenterId 数据中心id(0-31)
     * @param workerId    工作机器标识id(0-31)
     */
    public SnowflakeIdWorker(long dataCenterId, long workerId) {
        if (dataCenterId > MAX_DATA_CENTER_NUM || dataCenterId < 0) {
            throw new IllegalArgumentException("dataCenterId can't be greater than MAX_DATA_CENTER_NUM or less than 0");
        }
        if (workerId > MAX_WORKER_ID_NUM || workerId < 0) {
            throw new IllegalArgumentException("workerId can't be greater than MAX_WORKER_ID_NUM or less than 0");
        }
        this.dataCenterId = dataCenterId;
        this.workerId = workerId;
    }

    // ==============================Methods==========================================
    /**
     * 产生下一个 Long ID
     */
    public synchronized long nextId() {
        long currTimeStamp = getNewStamp();

        // 如果当前时间小于上一次 ID 生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (currTimeStamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate id for " + (lastTimestamp - currTimeStamp)  + " milliseconds .");
        }

        if (currTimeStamp == lastTimestamp) {
            // 相同毫秒内，序列号自增
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // 同一毫秒的序列数已经达到最大，毫秒内序列溢出
            if (sequence == 0L) {
                // 阻塞到下一个毫秒,获得新的时间戳
                currTimeStamp = tilNextMillis(lastTimestamp);
            }
        } else {
            // 时间戳改变，毫秒内序列重置
            sequence = 0L;
        }

        // 上次生成ID的时间截
        lastTimestamp = currTimeStamp;

        // 移位并通过或运算拼到一起组成 64 位的 ID (时间戳部分 | 数据中心部分 | 机器标识部分 | 序列号部分)
        return ((currTimeStamp - START_TIME_STAMP) << TIMESTAMP_LEFT)
                | (dataCenterId << DATA_CENTER_LEFT)
                | (workerId << WORKER_ID_BIT)
                | sequence;
    }

    /**
     * 下一个 Binary String ID
     */
    public static String nextBinaryStringId() {
        return Long.toBinaryString(nextSnowId());
    }

    /**
     * 下一个 Long String ID
     */
    public static String nextLongStringId() {
        return Long.toString(nextSnowId());
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = getNewStamp();
        while (timestamp <= lastTimestamp) {
            timestamp = getNewStamp();
        }
        return timestamp;
    }

    /**
     * 获得当前时间戳
     */
    protected long getNewStamp() {
        return System.currentTimeMillis();
    }

    private static Long getWorkId() {
        String hostAddress = NetworkUtil.getFirstHostAddress();
        if (log.isDebugEnabled()) {
            log.debug("host address [{}]", hostAddress);
        }
        return intArrayToRandomInt(StringUtils.toCodePoints(hostAddress));
    }

    private static Long getDataCenterId() {
        final String hostName = NetworkUtil.getLocalHostName();
        if (log.isDebugEnabled()) {
            log.debug("host name [{}]", hostName);
        }
        return intArrayToRandomInt(StringUtils.toCodePoints(hostName));
    }

    private static long intArrayToRandomInt(int[] ints) {
        long sums = 0;
        for (int i : ints) {
            sums += i;
        }
        return (sums % 32);
    }

    /**
     * 获取雪花算法的下一个 ID
     */
    public static long nextSnowId() {
        if (snowFlakeId == null) {
            synchronized (SnowflakeIdWorker.class) {
                if (snowFlakeId == null) {
                    snowFlakeId = new SnowflakeIdWorker(getDataCenterId(), getWorkId());
                }
            }
        }
        return snowFlakeId.nextId();
    }


    /* ======================= test ========================= */

    public static void main(String[] args) {
        long startTime = System.nanoTime();
        for (int i = 0; i < 100; i++) {
            /*System.out.println(snowFlake.nextLongStringId());*/
            //System.out.println(nextBinaryStringId());
            System.out.println(nextLongStringId());
        }
        System.out.println((System.nanoTime() - startTime) / 1000000 + "ms");
    }

    public static void main3(String[] args) {
        SnowflakeIdWorker snowFlake = new SnowflakeIdWorker(20, 18);
        long startTime = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            /*System.out.println(snowFlake.nextLongStringId());*/
            snowFlake.nextId();
            System.out.println(SnowflakeIdWorker.nextSnowId());
        }
        System.out.println((System.nanoTime() - startTime) / 1000000 + "ms");
    }

    public static void main1(String[] args) {
         /*
        0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000
         */

        long start = System.currentTimeMillis();
        SnowflakeIdWorker snowFlake = new SnowflakeIdWorker(2, 3);
        Map<Long, Integer> longs = new HashMap<>();
        ExecutorService executorService = Executors.newFixedThreadPool(500);
        for (int i = 0; i < 1000000; i++) {
            executorService.execute(() -> {
                Long tmp = snowFlake.nextId();

                if (!longs.containsKey(tmp)) {
                    longs.put(tmp, 0);
                    //System.out.println("生成新的：" + tmp + "二进制：" + Long.toBinaryString(tmp));
                } else {
                    int count = longs.get(tmp) + 1;
                    longs.put(tmp, count);
                    System.out.println("重复：" + tmp + " 次数：" + longs.get(tmp));
                }
            });
        }
        System.out.println("用时毫秒" + (System.currentTimeMillis() - start));
        executorService.shutdown();
    }

}
