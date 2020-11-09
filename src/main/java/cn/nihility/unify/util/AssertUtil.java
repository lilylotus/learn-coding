package cn.nihility.unify.util;

import cn.nihility.unify.exception.UnifyException;
import org.apache.commons.lang3.StringUtils;

/**
 * 常用断言校验工具类
 */
public class AssertUtil {

    /**
     * 字符串不能为空断言
     * @param str 断言的文本
     */
    public static void assertStringNotNull(String str) throws UnifyException {
        if (null == str) {
            throwUnifyInnerErrorException("String cannot be null");
        }
    }

    /**
     * 对象不能为空断言
     * @param str 断言的对象
     */
    public static void assertObjectNotNull(Object str) throws UnifyException {
        if (null == str) {
            throwUnifyInnerErrorException("Object cannot be null");
        }
    }

    /**
     * 断言字符串不可为 null/空白
     * @param str 断言字符串
     */
    public static void assertStringNotBlank(String str) throws UnifyException {
        if (StringUtils.isBlank(str)) {
            throwUnifyInnerErrorException("String cannot be blank");
        }
    }

    /**
     * 统一抛出异常信息
     * @param msg 异常提示信息
     * @throws UnifyException 自定义统一异常
     */
    public static void throwUnifyInnerErrorException(String msg) throws UnifyException {
        throw new UnifyException(msg);
    }

}
