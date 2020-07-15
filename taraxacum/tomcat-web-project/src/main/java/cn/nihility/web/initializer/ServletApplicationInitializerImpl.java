package cn.nihility.web.initializer;

/**
 * ServletApplicationInitializerImpl
 *
 * @author dandelion
 * @date 2020-06-25 17:00
 */
public class ServletApplicationInitializerImpl implements ServletApplicationInitializer {
    @Override
    public void init(String info) {
        System.out.println("========== ServletApplicationInitializerImpl -> init info -- " + info);
    }
}
