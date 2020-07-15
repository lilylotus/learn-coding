package cn.nihility.app.job;

/**
 * DoMyJobImpl
 *
 * @author dandelion
 * @date 2020-03-24 22:27
 */
public class DoMyJobImpl implements IDoMyJob {
    @Override
    public void doJob(String command) {
        System.out.println("DoMyJobImpl -> command [" + command + "]");
    }
}
