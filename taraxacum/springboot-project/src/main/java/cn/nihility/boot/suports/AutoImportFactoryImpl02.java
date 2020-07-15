package cn.nihility.boot.suports;

/**
 * @author dandelion
 * @date 2020:06:26 22:24
 */
public class AutoImportFactoryImpl02 implements IAutoImportFactory {
    @Override
    public void init(String info) {
        System.out.println("AutoImportFactoryImpl02 -> init [" + info + "]");
    }
}
