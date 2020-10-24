package cn.nihility.unify.controller;

import cn.nihility.unify.cache.CacheServiceImpl;
import cn.nihility.unify.exception.UnifyException;
import cn.nihility.unify.pojo.UnifyResult;
import cn.nihility.unify.pojo.UnifyResultCode;
import cn.nihility.unify.pojo.UnifyResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/cache")
public class CacheController {

    private static final Logger log = LoggerFactory.getLogger(CacheController.class);

    private final CacheServiceImpl cacheService;

    public CacheController(CacheServiceImpl cacheService) {
        this.cacheService = cacheService;
    }

    @RequestMapping("/data/{id}")
    public UnifyResult queryDataById(@PathVariable Long id) {
        if (log.isDebugEnabled()) {
            log.debug("request param id [{}]", id);
        }
        if (null == id) {
            throw new UnifyException("查询 ID 不可为空", UnifyResultCode.PARAM_IS_INVALID);
        }
        return UnifyResultUtil.success(cacheService.queryDataById(id));
    }

    @RequestMapping("/data2/{id}")
    public UnifyResult queryDataByIdOptimize(@PathVariable Long id) {
        if (log.isDebugEnabled()) {
            log.debug("request param id [{}]", id);
        }
        if (null == id) {
            throw new UnifyException("查询 ID 不可为空", UnifyResultCode.PARAM_IS_INVALID);
        }
        return UnifyResultUtil.success(cacheService.queryDataByIdOptimized(id));
    }

}
