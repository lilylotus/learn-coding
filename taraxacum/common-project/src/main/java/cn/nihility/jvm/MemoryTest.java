package cn.nihility.jvm;

/**
 * MemoryTest
 *
 * @author dandelion
 * @date 2020-03-30 16:07
 */
public class MemoryTest {

    private static final int _1M = 1024 * 1024;
    private static final int _1KB = 1024;

    public static void main(String[] args) throws InterruptedException {


        for (;;) {

            byte[] tmp = new byte[_1M];

            Thread.sleep(1000L);
        }

    }
}
