package cn.nihility.resume;


import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 实现要求：
 * 1、根据以下代码片段，参考log4j/slf4j等公共日志库编写一个自定义的简易日志类；
 * 2、至少需要支持文件输出、控制台输出二种日志输出方式，并支持同时输出到文件和控制台；
 * 3、支持DEBUG/INFO/WARN/ERROR四种日志级别；
 * 4、请合理进行设计模式，进行接口类、抽象类等设计，至少包括FileLogger、ConsoleLogger二个子实现类；
 * 5、注意代码注释书写。
 */
public class KLLogger {

    public static void main(String[] args) {
        System.out.println(DebugLevel.ERROR.name() + DebugLevel.ERROR.ordinal());

        Logger logger1 = ConsoleLogger.getLogger(KLLogger.class);
//        logger1.setLogLevel(DebugLevel.INFO);

/*        logger1.debug("debug 1...");
        logger1.info("info 1...");
        logger1.warn("warn 1...");
        logger1.error("error 1...");*/

        final Logger logger = FileLogger.getLogger(KLLogger.class, "fileLogger.log");
        logger.setLogLevel(DebugLevel.INFO);
        logger.wrapperLogger((AbstractLogger) logger1);

        logger.debug("debug 1...");
        logger.info("info 1...");
        logger.warn("warn 1...");
        logger.error("error 1...");
    }

}

/* 文件日志记录 */
class FileLogger extends AbstractLogger {

    private final DateTimeFormatter dateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private String fileName = "default.log";

    private FileLogger(Class<?> clazz) {
        super(clazz);
    }

    private FileLogger(Class<?> clazz, String fileName) {
        super(clazz);
        this.fileName = fileName;
    }

    public static Logger getLogger(Class<?> clazz) {
        return new FileLogger(clazz);
    }

    public static Logger getLogger(Class<?> clazz, String fileName) {
        return new FileLogger(clazz, fileName);
    }

    @Override
    protected void writeLogger(String level, String logger) {
        String preTime = dateTimeFormatter.format(LocalDateTime.now());
        try (BufferedWriter bw =
                     new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName, true), StandardCharsets.UTF_8))) {
            bw.write(preTime + " [" + level + "] " + getLoggerClassName() + " - " + logger);
            bw.newLine();
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

/* 控制台记录日志 */
class ConsoleLogger extends AbstractLogger {

    private final DateTimeFormatter dateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private ConsoleLogger(Class<?> clazz) {
        super(clazz);
    }

    public static Logger getLogger(Class<?> clazz) {
        return new ConsoleLogger(clazz);
    }

    @Override
    protected void writeLogger(String level, String logger) {
        String preTime = dateTimeFormatter.format(LocalDateTime.now());
        System.out.println(preTime + " [" + level + "] " + getLoggerClassName() + " - " + logger);
    }
}

/* 日志的抽象，writeLogger 交给具体的 Logger 实现 */
abstract class AbstractLogger implements Logger {

    private DebugLevel level = DebugLevel.DEBUG;
    private Class<?> loggerClass;
    /* 类似装饰模式，并行处理 */
    private AbstractLogger wrapper;

    protected abstract void writeLogger(String level, String logger);

    AbstractLogger(Class<?> clazz) {
        this.loggerClass = clazz;
    }

    @Override
    public void setLogLevel(DebugLevel level) {
        this.level = level;
    }

    @Override
    public void debug(String logger) {
        switchLogger(DebugLevel.DEBUG, logger);
    }

    @Override
    public void info(String logger) {
        switchLogger(DebugLevel.INFO, logger);
    }

    @Override
    public void warn(String logger) {
        switchLogger(DebugLevel.WARN, logger);
    }

    @Override
    public void error(String logger) {
        switchLogger(DebugLevel.ERROR, logger);
    }

    @Override
    public void wrapperLogger(AbstractLogger append) {
        this.wrapper = append;
    }

    protected void switchLogger(DebugLevel writeLevel, String logger) {
        if (writeLevel.ordinal() >= level.ordinal()) {
            writeLogger(writeLevel.name(), logger);
            if (null != wrapper) {
                wrapper.writeLogger(writeLevel.name(), logger);
            }
        }
    }

    protected String getLoggerClassName() {
        return (loggerClass == null ? "" : loggerClass.getName());
    }

    public Class<?> getLoggerClass() {
        return loggerClass;
    }

    public void setLoggerClass(Class<?> loggerClass) {
        this.loggerClass = loggerClass;
    }

    public DebugLevel getLevel() {
        return level;
    }

    public void setLevel(DebugLevel level) {
        this.level = level;
    }
}

interface Logger {

    /* 设置日志记录级别 */
    void setLogLevel(DebugLevel level);

    /* 记录日志 */
    void debug(String logger);

    void info(String logger);

    void warn(String logger);

    void error(String logger);

    /* 并行的 logger 处理器 */
    void wrapperLogger(AbstractLogger append);
}


enum DebugLevel {
    DEBUG, INFO, WARN, ERROR
}