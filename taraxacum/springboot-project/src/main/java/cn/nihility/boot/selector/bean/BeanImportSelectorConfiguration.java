package cn.nihility.boot.selector.bean;

import cn.nihility.boot.selector.Cat;
import cn.nihility.boot.selector.Zoom;
import org.springframework.context.annotation.Bean;

/**
 * @author dandelion
 * @date 2020:06:27 17:19
 */
// @Configuration 不需要注解，ImportSelector 会自动加载
public class BeanImportSelectorConfiguration {

    /*
    * 这个是以 @Component 的形式加载的，方法内不会有代理拦截， zoom 的两个 cat 不一样
    * */

    @Bean
    public Cat cat() {
        return new Cat("ImportSelectorCat", 10);
    }

    @Bean
    public Zoom zoom() {
        Zoom zoom = new Zoom();
        zoom.setCat1(cat());
        zoom.setCat2(cat());
        return zoom;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
