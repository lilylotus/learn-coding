package cn.nihility.profile;

import cn.nihility.profile.bean.PropertyBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProfileController {

    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    private ApplicationContext applicationContext;

    public ProfileController(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @GetMapping("/env/{key}")
    public ResponseEntity<Object> parseProperty(@PathVariable String key) {
        String val = applicationContext.getEnvironment().getProperty(key);
        logger.info("Environment Property [{}]:[{}]", key, val);
        return ResponseEntity.ok(val);
    }

    @GetMapping("/property")
    public ResponseEntity<Object> propertyBean() {
        PropertyBean bean = applicationContext.getBean("property-cn.nihility.profile.bean.PropertyBean", PropertyBean.class);
        logger.info("PropertyBean [{}]", bean);
        return ResponseEntity.ok(bean);
    }

    @GetMapping("/property/another")
    public ResponseEntity<Object> anotherPropertyBean() {
        PropertyBean bean = applicationContext.getBean("anotherPropertyBean", PropertyBean.class);
        logger.info("AnotherPropertyBean [{}]", bean);
        return ResponseEntity.ok(bean);
    }

}
