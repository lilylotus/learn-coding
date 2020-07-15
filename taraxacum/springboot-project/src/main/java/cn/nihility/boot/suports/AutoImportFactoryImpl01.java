package cn.nihility.boot.suports;

/**
 * @author dandelion
 * @date 2020:06:26 22:23
 */
public class AutoImportFactoryImpl01 implements IAutoImportFactory {
    @Override
    public void init(String info) {
        System.out.println("AutoImportFactoryImpl01 -> init info [" + info + "]");
    }
}
