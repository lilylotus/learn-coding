package cn.nihility.demo.proxy;

import cn.nihility.demo.post.config.ProxyAutoScanConfiguration;
import cn.nihility.demo.post.proxy.InvokeProxyService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author sakura
 * @date 2022-03-03 00:51
 */
class ProxyAutoScanTest {

    @Test
    void test() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ProxyAutoScanConfiguration.class);
        ctx.refresh();

        InvokeProxyService service = ctx.getBean("invokeProxyService", InvokeProxyService.class);
        Assertions.assertNotNull(service);
        service.say("proxy service invoke");

        ctx.registerShutdownHook();
    }

}
