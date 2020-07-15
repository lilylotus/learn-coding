package cn.nihility.boot.aop;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * AOP Aspect Class
 * @author muscari
 * @date 2019-06-13 23:07
 */
@Component
public class Calculate {

    public int add(int itx, int ity) {
        return itx + ity;
    }

    public int multiple(int itx, int ity) {
        return itx * ity;
    }

    public int subtract(int itx, int ity) {
        return itx - ity;
    }

    public int division(int itx, int ity) {
        return itx / ity;
    }

}
