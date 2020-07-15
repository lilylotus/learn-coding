package cn.nihility.boot.util;

import cn.nihility.boot.suports.SpringFactoriesLoad;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author dandelion
 * @date 2020:06:27 16:49
 */
public class ResourceUtil {

    private final static Logger log = LoggerFactory.getLogger(ResourceUtil.class);
    private final static ClassLoader CLASS_LOADER = ResourceUtil.class.getClassLoader();

    public static List<String> scanPackageClassName(String basePackage) {
        final List<String> clazzNameList = new ArrayList<>();
        if (null != basePackage && !"".equals(basePackage.trim())) {
            String locationPattern = "classpath:" + basePackage.replace(".", "/") + "/*/*.class";
            log.info("location resolver pattern [{}]", locationPattern);
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(ResourceUtil.class.getClassLoader());
            try {
                Resource[] resources = resolver.getResources(locationPattern);
                final CachingMetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(ResourceUtil.class.getClassLoader());
                Stream.of(resources).forEach(r -> {
                    try {
                        MetadataReader metadata = readerFactory.getMetadataReader(r);
                        ClassMetadata classMetadata = metadata.getClassMetadata();
                        if (!classMetadata.isInterface() && !classMetadata.isAbstract()) {
                            clazzNameList.add(classMetadata.getClassName());
                        } else {
                            log.info("class [{}] exclusion",  classMetadata.getClassName());
                        }
                    } catch (IOException e) {
                        log.error("read [{}] error", r.getFilename(), e);
                    }
                });
            } catch (IOException e) {
                log.error("scan package [{}] error", basePackage, e);
            }
        }
        return clazzNameList;
    }

    public static List<Class<?>> scanPackageClass(String basePackage) {
        return scanPackageClass(basePackage, 0);
    }

    public static List<Class<?>> scanPackageClass(String basePackage, int depth) {
        final List<Class<?>> clazzList = new ArrayList<>();

        if (null != basePackage && !"".equals(basePackage.trim())) {
            String locationPattern;
            if (1 == depth) {
                locationPattern = "classpath:" + basePackage.replace(".", "/") + "/*.class";
            } else if (2 == depth) {
                locationPattern = "classpath:" + basePackage.replace(".", "/") + "/**/*.class";
            } else {
                locationPattern = "classpath:" + basePackage.replace(".", "/") + "/*/*.class";
            }

            log.info("location resolver pattern [{}]", locationPattern);
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(ResourceUtil.class.getClassLoader());
            try {
                Resource[] resources = resolver.getResources(locationPattern);
                final CachingMetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(ResourceUtil.class.getClassLoader());
                Stream.of(resources).forEach(r -> {
                    try {
                        clazzList.add(Class.forName(readerFactory.getMetadataReader(r).getClassMetadata().getClassName()));
                    } catch (IOException e) {
                        log.error("read [{}] error", r.getFilename(), e);
                    } catch (ClassNotFoundException e) {
                        log.error("read [{}] class for name error", r.getFilename(), e);
                    }
                });
            } catch (IOException e) {
                log.error("scan package [{}] error", basePackage, e);
            }
        }

        return clazzList;
    }


    public static <T> List<T> loadFactories(Class<T> clazz) {
        List<T> factories =
                SpringFactoriesLoader.loadFactories(clazz,
                        SpringFactoriesLoad.class.getClassLoader());

        factories.forEach(f -> System.out.println(f.getClass().getName()));

        return factories;
    }

    public static List<String> loadFactoriesClassName(Class<?> clazz) {
        final List<String> clazzNameList = new ArrayList<>();
        List<?> factories = SpringFactoriesLoader.loadFactories(clazz, CLASS_LOADER);
        factories.forEach(f -> clazzNameList.add(f.getClass().getName()));
        return clazzNameList;
    }

}
