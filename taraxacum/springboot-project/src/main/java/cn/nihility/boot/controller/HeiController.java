package cn.nihility.boot.controller;

import cn.nihility.boot.annotation.LoginToken;
import cn.nihility.boot.controller.ret.ResponseResultBody;
import cn.nihility.boot.controller.ret.ResultResponse;
import cn.nihility.boot.controller.vo.ResultVo;
import cn.nihility.boot.mybatis.dao.TestDao;
import cn.nihility.boot.util.ResultVoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.net.Inet4Address;
import java.net.UnknownHostException;
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
    private static final Map<String, Object> RESULT_DATA;

    private final TestDao testDao;

    static {
        RESULT_DATA = new HashMap<>();
        RESULT_DATA.put("status", 200);
        RESULT_DATA.put("message", "自定义 Hei 信息");
        RESULT_DATA.put("name", "anonymous");
        try {
            RESULT_DATA.put("remote", Inet4Address.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public HeiController(TestDao testDao) {
        this.testDao = testDao;
    }

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

    /* ========== 统一返回格式 ========== */

    @RequestMapping("/data")
    public Map<String, Object> data() {
        return RESULT_DATA;
    }

    @RequestMapping("/result")
    @ResponseBody
    public ResultResponse<Map<String, Object>> result() {
        return ResultResponse.success(RESULT_DATA);
    }

    @RequestMapping("/resultBody")
    @ResponseResultBody
    public Map<String, Object> resultBody() {
        return RESULT_DATA;
    }

    /* ========== mybatis ========== */

    @RequestMapping("/dao")
    public Map<String, Object> selector() {
        Map<String, Object> ret = new HashMap<>(4);
        ret.put("dto", testDao.selectAll());
        return ret;
    }
}
