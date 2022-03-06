package cn.nihility.cloud.controller;

import cn.nihility.common.pojo.UnifyResult;
import cn.nihility.common.util.UnifyResultUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author sakura
 * @date 2022-03-06 16:25
 */
@RestController
public class HeiController {

    private static final Logger logger = LoggerFactory.getLogger(HeiController.class);

    @GetMapping("/gateway/hei")
    public UnifyResult<String> hei() {
        logger.info("Cloud Gateway Hei.");
        return UnifyResultUtils.success("Cloud Gateway Hei");
    }

}
