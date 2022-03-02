package cn.nihility.demo.post.proxy;

import cn.nihility.demo.post.config.ProxyService;

/**
 * @author sakura
 * @date 2022-03-03 00:50
 */
@ProxyService
public interface InvokeProxyService {

    void say(String msg);

}
