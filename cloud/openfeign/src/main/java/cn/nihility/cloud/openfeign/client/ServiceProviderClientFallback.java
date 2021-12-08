package cn.nihility.cloud.openfeign.client;

import cn.nihility.common.pojo.UnifyResult;
import cn.nihility.common.util.UnifyResultUtil;
import org.springframework.stereotype.Component;

@Component
public class ServiceProviderClientFallback implements ServiceProviderClient {

    @Override
    public UnifyResult echo(String msg) {
        return UnifyResultUtil.success("Fallback [" + msg + "]");
    }

    @Override
    public UnifyResult randomTimeOut() {
        return UnifyResultUtil.success(-1);
    }

}
