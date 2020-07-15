package cn.nihility.web.servlet;

import cn.nihility.web.annotation.MyController;
import cn.nihility.web.annotation.MyParam;
import cn.nihility.web.annotation.MyRequestMapping;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * 仿制 spring mvc 请求的 DispatcherServlet
 *
 * @author dandelion
 * @date 2020-06-25 16:56
 */
public class MvcHttpServlet extends HttpServlet {

    /* 要扫描处理的 Controller 注解包 */
    private final static String scanPackage = "cn.nihility.web.spring.controller";

    /* 类名 : @Controller 注解类 */
    private static final Map<String, Class<?>> iocMap = new HashMap<>();
    /* url : 要处理的 @Mapping 方法 */
    private static final Map<String, Method> handlerMap = new HashMap<>();
    /* url : 反射调用的对象 */
    private final static Map<String, Object> controllerMap = new HashMap<>();


    @Override
    public void init() throws ServletException {
        // 1. 初始化扫描包中所有 @MyController 注解类，放到 ioc 容器
        try {
            initScanPackage(scanPackage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 2. 解析所有 @MyController 注解类的方法
        handlerMapping();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("========== MvcHttpServlet doPost");

        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        String finalUri = requestURI.replace(contextPath, "");
        System.out.println("requestURI : " + requestURI + " , contextPath : " + contextPath + " , finalUri : " + finalUri);

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=utf-8");

        if (handlerMap.containsKey(finalUri)) {
            Method method = handlerMap.get(finalUri);

            // 获取请求中的参数列表
            // http://localhost:8080/mvc-servlet/my/hei?user=tset&password=pass
            // -> user = tset : password = pass
            Map<String, String[]> parameterMap = request.getParameterMap();

            // 方法反射需要的参数
            Parameter[] parameters = method.getParameters();
            Class<?>[] parameterTypes = method.getParameterTypes();
            // 填充的方法参数列表
            Object[] params = new Object[parameters.length];

            for (int i = 0, ii = parameters.length; i < ii; i++) {
                String parameterTypeName = parameterTypes[i].getSimpleName();
                System.out.println("index : " + (i + 1) + " param type : " + parameterTypeName);

                if ("HttpServletRequest".equals(parameterTypeName)) {
                    params[i] = request;
                } else if ("HttpServletResponse".equals(parameterTypeName)) {
                    params[i] = response;
                } else {
                    // 其它类型，简单处理
                    if ("String".equals(parameterTypeName)) {
                        String paramName = parameters[i].getName();

                        if (parameters[i].isAnnotationPresent(MyParam.class)) {
                            paramName = parameters[i].getAnnotation(MyParam.class).value();
                        }

                        // 获取请求参数，是个数组类型，防止参数重名
                        String[] requestParams = parameterMap.get(paramName);
                        // 这里仅取第一个参数
                        if (requestParams != null) {
                            params[i] = requestParams[0];
                        } else {
                            String headerVal = request.getHeader(paramName);
                            if (headerVal != null) {
                                params[i] = headerVal;
                            } else {
                                params[i] = null;
                            }
                        }
                        System.out.println("param : " + paramName + " , value : " + params[i]);
                    }
                }
            }

            Object invokeInstance = controllerMap.get(finalUri);
            try {
                Object result = method.invoke(invokeInstance, params);
                if (result instanceof String) {

                } else {
                    System.out.println("uri " + finalUri + " , result type is not String, can not transfer to destination");
                    response.getWriter().write("result type is not String, can not transfer to destination");
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

        } else {
            response.getWriter().write("404 !! 服务器页面不存在 !!");
        }

    }

    private void handlerMapping() {
        if (!iocMap.isEmpty()) {
            iocMap.forEach((k, v) -> handlerControllerClazz(v));
        }
    }

    private void handlerControllerClazz(final Class<?> clazz) {
        System.out.println("====== handler clazz start : " + clazz.getName());
        String rootUri = "";
        if (clazz.isAnnotationPresent(MyRequestMapping.class)) {
            String path = clazz.getAnnotation(MyRequestMapping.class).value();
            System.out.println("=== root path : " + path);
            rootUri = path;
        }
        final String rootUriFinal = rootUri;

        /* 仅处理 public 方法 */
        Method[] methods = clazz.getMethods();
        Stream.of(methods).forEach(m -> handlerMethod(clazz, m, rootUriFinal));
        System.out.println("====== handler clazz end : " + clazz.getName());
    }

    private void handlerMethod(final Class<?> clazz, Method method, String rootUri) {
        System.out.println("--- handler method start : " + method.getName());
        if (method.isAnnotationPresent(MyRequestMapping.class)) {
            String path = method.getAnnotation(MyRequestMapping.class).value();
            String fullPath = rootUri + path;
            System.out.println("method request path : " + path + " , full path = " + fullPath);

            handlerMap.put(fullPath, method);

            try {
                controllerMap.put(fullPath, clazz.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        System.out.println("--- handler method end : " + method.getName());
    }


    private void initScanPackage(String scanPackage) throws IOException {
        if (null != scanPackage && !"".equals(scanPackage.trim())) {
            String location = "classpath*:" + scanPackage.replace(".", "/") + "/**/*.class";
            System.out.println("========== scan controller location - " + location);
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(MvcHttpServlet.class.getClassLoader());
            final CachingMetadataReaderFactory metadataReader = new CachingMetadataReaderFactory(resolver);
            Resource[] resources = resolver.getResources(location);

            if (null != resources) {
                Stream.of(resources).forEach(r -> {
                    try {
                        MetadataReader metadata = metadataReader.getMetadataReader(r);
                        String clazzName = metadata.getClassMetadata().getClassName();
                        if (metadata.getAnnotationMetadata().hasAnnotation(MyController.class.getName())) {
                            System.out.println("========== scan a class with MyController Annotation : " + clazzName);
                            Class<?> instance = loadClazzForName(clazzName);
                            if (null == instance) {
                                System.out.println("=== instance " + clazzName + " , error");
                            } else {
                                iocMap.put(clazzName, instance);
                            }
                        } else {
                            System.out.println("========== clazz " + clazzName + " is not annotation with MyController.");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

    private Class<?> loadClazzForName(String clazzName) {
        System.out.println("========== load clazz : " + clazzName);
        Class<?> clazz = null;
        try {
            clazz = Class.forName(clazzName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return clazz;
    }
}
