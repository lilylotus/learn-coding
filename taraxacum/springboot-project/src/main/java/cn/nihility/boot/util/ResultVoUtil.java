package cn.nihility.boot.util;

import cn.nihility.boot.controller.vo.ResultErrorInfo;
import cn.nihility.boot.controller.vo.ResultState;
import cn.nihility.boot.controller.vo.ResultVo;

/**
 * @author dandelion
 * @date 2020:06:27 09:57
 */
public class ResultVoUtil {

    public static ResultVo success(Object data) {
        return new ResultVo(ResultState.SUCCESS, data);
    }

    public static ResultVo success(String msg, Object data) {
        return new ResultVo(ResultState.SUCCESS.getCode(), msg, data);
    }

    public static ResultVo success() {
        return new ResultVo(ResultState.SUCCESS);
    }

    public static ResultVo error(ResultErrorInfo errorInfo) {
        return new ResultVo(ResultState.ERROR, errorInfo);
    }

    public static ResultVo error(String msg, ResultErrorInfo errorInfo) {
        return new ResultVo(ResultState.ERROR.getCode(), msg, errorInfo);
    }

    public static ResultVo error(String msg) {
        return new ResultVo(ResultState.ERROR.getCode(), msg);
    }

    public static ResultVo error() {
        return new ResultVo(ResultState.ERROR);
    }

}
