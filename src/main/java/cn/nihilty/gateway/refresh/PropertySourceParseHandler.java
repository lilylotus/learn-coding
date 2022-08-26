package cn.nihilty.gateway.refresh;

import com.alibaba.cloud.nacos.utils.NacosConfigUtils;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.PropertySourcesPropertyResolver;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class PropertySourceParseHandler {

    /**
     * default extension.
     */
    private static final String DEFAULT_EXTENSION = "yml";

    private static List<PropertySourceLoader> propertySourceLoaders;

    private PropertySourceParseHandler() {
    }

    public static List<PropertySource<?>> parsePropertyResource(String configName, String configValue) throws IOException {

        if (!StringUtils.hasText(configValue) && !StringUtils.hasText(configName)) {
            return Collections.emptyList();
        }
        String extension = getFileExtension(configName);
        if (propertySourceLoaders == null) {
            propertySourceLoaders = SpringFactoriesLoader.loadFactories(PropertySourceLoader.class,
                    PropertySourceParseHandler.class.getClassLoader());
        }

        PropertySourceLoader sourceLoader = null;
        for (PropertySourceLoader propertySourceLoader : propertySourceLoaders) {
            if (!canLoadFileExtension(propertySourceLoader, extension)) {
                continue;
            }
            sourceLoader = propertySourceLoader;
        }

        if (null == sourceLoader) {
            throw new IOException("不支持的文件解析类型 [" + configName + "]");
        }

        return parsePropertySource(configName, configValue, sourceLoader);
    }

    private static List<PropertySource<?>> parsePropertySource(String configName, String configValue,
                                                               PropertySourceLoader propertySourceLoader) throws IOException {
        byte[] configValueBytes;
        if (propertySourceLoader instanceof PropertySourcesPropertyResolver) {
            // PropertiesPropertySourceLoader internal is to use the ISO_8859_1,
            // the Chinese will be garbled, needs to transform into unicode.
            configValueBytes = NacosConfigUtils.selectiveConvertUnicode(configValue).getBytes();
        } else {
            configValueBytes = configValue.getBytes(StandardCharsets.UTF_8);
        }
        ByteArrayResource resource = new ByteArrayResource(configValueBytes, configName);
        List<PropertySource<?>> propertySourceList = propertySourceLoader.load(configName, resource);
        if (CollectionUtils.isEmpty(propertySourceList)) {
            return Collections.emptyList();
        }
        return propertySourceList.stream().filter(Objects::nonNull)
                .map(propertySource -> {
                    if (propertySource instanceof EnumerablePropertySource) {
                        String[] propertyNames = ((EnumerablePropertySource) propertySource).getPropertyNames();
                        if (propertyNames.length > 0) {
                            Map<String, Object> map = new LinkedHashMap<>();
                            Arrays.stream(propertyNames).forEach(name -> map.put(name, propertySource.getProperty(name)));
                            return new OriginTrackedMapPropertySource(propertySource.getName(), map, true);
                        }
                    }
                    return propertySource;
                }).collect(Collectors.toList());
    }

    /**
     * @param name filename
     * @return file extension, default {@code DEFAULT_EXTENSION} if don't get
     */
    public static String getFileExtension(String name) {
        if (StringUtils.hasText(name)) {
            int idx = name.lastIndexOf(".");
            if (idx > 0 && idx < name.length() - 1) {
                return name.substring(idx + 1);
            }
        }
        return DEFAULT_EXTENSION;
    }

    private static boolean canLoadFileExtension(PropertySourceLoader loader, String name) {
        return Arrays.stream(loader.getFileExtensions())
                .anyMatch(fileExtension -> StringUtils.endsWithIgnoreCase(name, fileExtension));
    }

}
