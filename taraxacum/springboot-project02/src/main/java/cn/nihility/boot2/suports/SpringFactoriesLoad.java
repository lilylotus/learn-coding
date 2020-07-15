package cn.nihility.boot2.suports;

import org.springframework.core.io.support.SpringFactoriesLoader;

import java.util.List;

/**
 * @author dandelion
 * @date 2020:06:26 23:14
 */
public class SpringFactoriesLoad {

    public static void main(String[] args) {
        List<IAutoImportFactory> factories = loadFactories(IAutoImportFactory.class);
    }

    public static <T> List<T> loadFactories(Class<T> clazz) {
        List<T> factories =
                SpringFactoriesLoader.loadFactories(clazz,
                        SpringFactoriesLoad.class.getClassLoader());

        factories.forEach(f -> System.out.println(f.getClass()));

        return factories;
    }

}
