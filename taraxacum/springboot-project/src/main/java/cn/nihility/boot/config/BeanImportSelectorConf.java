package cn.nihility.boot.config;

import cn.nihility.boot.annotation.EnableBeanImportSelector;
import cn.nihility.boot.annotation.ImportSelectorScan;

/**
 * @author dandelion
 * @date 2020:06:27 17:26
 */
// 这里采用 EnableAutoConfiguration
//@Configuration
@EnableBeanImportSelector
@ImportSelectorScan(basePackage = "cn.nihility.boot.selector")
public class BeanImportSelectorConf {
}
