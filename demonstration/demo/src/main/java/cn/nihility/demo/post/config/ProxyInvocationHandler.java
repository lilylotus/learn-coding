package cn.nihility.demo.post.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author sakura
 * @date 2022-03-03 00:33
 */
public class ProxyInvocationHandler implements InvocationHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyInvocationHandler.class);

    private static final String HASH_CODE = "hashCode";
    private static final String EQUALS = "equals";
    private static final String TO_STRING = "toString";

    private Class<?> proxyClazz;

    public ProxyInvocationHandler(Class<?> proxyClazz) {
        this.proxyClazz = proxyClazz;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] argument) throws Throwable {

        if (method.getName().equals(HASH_CODE)) {
            return Objects.hash(proxy);
        } else if (method.getName().equals(EQUALS) && method.getParameterTypes().length == 1) {
            return Objects.equals(proxy, argument[0]);
        } else if (method.getName().equals(TO_STRING)) {
            return Objects.toString(proxyClazz);
        }

        LOGGER.info("Invoke [{}.{}] method", proxyClazz.getName(), method.getName());

        return null;
    }

}
