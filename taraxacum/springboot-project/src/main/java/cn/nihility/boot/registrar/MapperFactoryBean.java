package cn.nihility.boot.registrar;

import cn.nihility.boot.annotation.RegistrarParam;
import cn.nihility.boot.annotation.RegistrarSelect;
import cn.nihility.boot.registrar.dto.Duck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.stream.Stream;

/**
 * @author dandelion
 * @date 2020:06:27 18:30
 */
public class MapperFactoryBean implements FactoryBean<Object> {

    private final static Logger log = LoggerFactory.getLogger(MapperFactoryBean.class);
    private Class<?> mapperInstance;

    @Override
    public Object getObject() {
        log.info("MapperFactoryBean getObject - mapper [{}]", mapperInstance.getName());
        return Proxy.newProxyInstance(mapperInstance.getClassLoader(),
                new Class[] {mapperInstance}, ((proxy, method, args) -> {
                    if (method.isAnnotationPresent(RegistrarSelect.class)) {
                        String sql = method.getAnnotation(RegistrarSelect.class).value();
                        log.info("method [{}], sql [{}]", method.getName(), sql);

                        Parameter[] parameters = method.getParameters();
                        if (null != parameters && parameters.length > 0) {
                            Stream.of(parameters).forEach(p -> {
                                String paramName;
                                if (p.isAnnotationPresent(RegistrarParam.class)) {
                                    paramName = p.getAnnotation(RegistrarParam.class).value();
                                } else {
                                    paramName = p.getName();
                                }
                                log.info("method [{}], param [{}]", method.getName(), paramName);
                            });
                        }

                        if (null != args && args.length > 0) {
                            Integer id = (Integer) args[0];
                            log.info("arg [{}]", args[0]);
                            if (1 == id) {
                                return new Duck(1, "Registrar Duck Name", 5);
                            }
                        }
                    }

                    return null;
                }));
    }

    @Override
    public Class<?> getObjectType() {
        return mapperInstance;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public MapperFactoryBean(Class<?> mapperInstance) {
        this.mapperInstance = mapperInstance;
    }

    public MapperFactoryBean() {
    }

    public Class<?> getMapperInstance() {
        return mapperInstance;
    }

    public void setMapperInstance(Class<?> mapperInstance) {
        this.mapperInstance = mapperInstance;
    }
}
