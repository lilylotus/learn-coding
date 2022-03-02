package cn.nihility.demo.post.config;

import org.springframework.context.annotation.Configuration;

/**
 * @author sakura
 * @date 2022-03-02 23:13
 */
@Configuration
@ProxyAutoScan(scanPackages = {"cn.nihility.demo.post.proxy"})
public class ProxyAutoScanConfiguration {

}
