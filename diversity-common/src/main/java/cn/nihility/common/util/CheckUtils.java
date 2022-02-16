package cn.nihility.common.util;

import cn.nihility.common.exception.CheckInvalidException;
import org.apache.commons.lang3.StringUtils;

/**
 * @author nihility
 * @date 2022/02/16 13:37
 */
public final class CheckUtils {

    private CheckUtils() {
    }

    public static void stringNotBlank(String content, String tip) {
        if (StringUtils.isBlank(content)) {
            throw new CheckInvalidException(tip);
        }
    }

}
