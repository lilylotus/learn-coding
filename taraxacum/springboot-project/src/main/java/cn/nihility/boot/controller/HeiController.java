package cn.nihility.boot.controller;

import cn.nihility.boot.annotation.LoginToken;
import cn.nihility.boot.controller.vo.ResultVo;
import cn.nihility.boot.util.ResultVoUtil;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dandelion
 * @date 2020:06:26 19:54
 */
@RestController
@RequestMapping("/hei")
public class HeiController {

    private final static Logger log = LoggerFactory.getLogger(HeiController.class);

    @RequestMapping("/hello")
    public ResultVo hello() {
        log.info("HeiController -> hello.");
        return ResultVoUtil.success(generateData());
    }

    @GetMapping(path = "/random")
    public ResultVo random() throws Exception {
        log.info("HeiController -> random exception.");
        randomException();
        return ResultVoUtil.success("随机异常", generateData());
    }

    @PostMapping(path = {"/login/{user}/{password}"})
    public ResultVo login(@PathVariable("user") String user, @PathVariable("password") String password) {
        log.info("HeiController login user [{}], password [{}]", user, password);
        if (null == user || "empty".equals(user)) {
            return ResultVoUtil.error("登录失败,用户不存在.");
        } else {
            if ("admin".equals(user) && "admin".equals(password)) {
                return ResultVoUtil.success(user + ":" + password + " - Login Success.");
            } else {
                return ResultVoUtil.error(user + ": Password Error");
            }
        }
    }

    @LoginToken
    @GetMapping(path = {"/token"})
    public ResultVo verifyToken() {
        log.info("HeiController -> verifyToken.");
        return ResultVoUtil.success("通过验证");
    }

    private Map<String, Object> generateData() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "request success");
        result.put("data", "Say Hello");
        return result;
    }

    /**
     * 随机抛出异常
     */
    private void randomException() throws Exception {
        String error = "随机异常";
        Exception[] exceptions = { //异常集合
                new NullPointerException(error),
                new ArrayIndexOutOfBoundsException(error),
                new NumberFormatException(error),
                new SQLException(error)};
        //发生概率
        double probability = 0.75;
        if (Math.random() < probability) {
            //情况1：要么抛出异常
            throw exceptions[(int) (Math.random() * exceptions.length)];
        } else {
            //情况2：要么继续运行
        }
    }

}
