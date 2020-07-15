package cn.nihility.jvm;

/**
 * MemoryStructureLearn
 *
 * @author dandelion
 * @date 2020-05-11 22:52
 */
public class MemoryStructureLearn {

    private static final int _1KB = 1024;
    private static final int _1MB = 1024 * 1024;

    public static void main(String[] args) throws InterruptedException {
        byte[] memory = new byte[_1MB];
        byte[] memory1 = new byte[2 * _1MB];
        byte[] memory3 = new byte[2 * _1MB];
        byte[] memory4 = new byte[_1MB];

    }
}
