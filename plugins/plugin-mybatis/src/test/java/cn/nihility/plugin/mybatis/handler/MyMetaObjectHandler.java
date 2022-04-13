package cn.nihility.plugin.mybatis.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * https://baomidou.com/pages/4c6bcf/
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    private static final Logger logger = LoggerFactory.getLogger(MyMetaObjectHandler.class);

    @Override
    public void insertFill(MetaObject metaObject) {
        logger.info("start  insert fill ...");
        // this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now()); // 起始版本 3.3.0(推荐使用)
        // 或者 起始版本 3.3.3(推荐)
        this.strictInsertFill(metaObject, "addTime", LocalDateTime::now, LocalDateTime.class);
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime::now, LocalDateTime.class); // 起始版本 3.3.3(推荐)
        // 或者 也可以使用(3.3.0 该方法有bug)
        // this.fillStrategy(metaObject, "createTime", LocalDateTime.now()); //

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        logger.info("start update fill ....");
        // 起始版本 3.3.0(推荐)
        // this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        // 或者 起始版本 3.3.3(推荐)
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime::now, LocalDateTime.class);
        // 或者 也可以使用(3.3.0 该方法有bug)
        // this.fillStrategy(metaObject, "updateTime", LocalDateTime.now());
    }

}
