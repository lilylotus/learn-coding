#### 统一归纳的项目
- spring-mvc
- practice
- gradle-springboot
- springboot-mvc-20
- springboot-learn-mvn
- springboot-gradle
- springboot-mvn
- tomcat-servlet3-learn
- springboot-base

#### 跳过项目
- spring-learn-mvn
- springmvc
- spring-learn-mvn
- spring-gradle
- spring-web-mvn
- spring-web-gradle
- [springcloud-base]

#### 可以删除的项目
- spring-cloud-base02

## 笔记
#### @Component 和 @Configuration 的区别
```
Configuration 时在 driver 和 spring 容器之中的是同一个对象，而使用 Component 时是不同的对象。
原因 ConfigurationClassPostProcessor 类之中，通过调用 enhanceConfigurationClasses 方法
注解 @Configuration 的类进行 CGLIB 代理

@Component 的时候，不同的 CarBean
CarBean{id=100, name='Car Name'} : cn.nihility.bean.CarBean@6f9ad11c
CarBean{id=100, name='Car Name'} : cn.nihility.bean.CarBean@4b2d44bc

@Configuration 的时候，相同的 CarBean

@Bean
public DriverBean driverBean() {
    DriverBean driver = new DriverBean();
    driver.setCar(carBean()); // 方法注入
    return driver;
}
@Bean
public CarBean carBean() {
    return new CarBean("Car Name", 100);
}

```