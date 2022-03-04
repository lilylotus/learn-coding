# Diversity
各种各样、随性的编码

# Gradle `api` And `implementation`

[Removal of compile and runtime configurations](https://docs.gradle.org/current/userguide/upgrading_version_6.html#sec:configuration_removal)
[java_library_plugin (api/implementation)](https://docs.gradle.org/6.9.1/userguide/java_library_plugin.html#java_library_plugin)

Since its inception, Gradle provided the `compile` and `runtime` configurations to declare dependencies. These however did not support a fine grained scoping of dependencies. Hence, better replacements were introduced in Gradle 3.4:

- The `implementation` configuration should be used to declare dependencies which are *implementation details* of a library: they are not visible to consumers of the library during compilation time.
- The `api` configuration, available only if you apply the `java-library` plugin, should be used to declare dependencies which are part of the API of a library, that need to be exposed to consumers at compilation time.

In Gradle 7, both the `compile` and `runtime` configurations are removed. Therefore, you have to migrate to the `implementation` and `api` configurations above. If you are still using the `java` plugin for a Java library, you will need to apply the `java-library` plugin instead.

**注意：** `api` 需要插件 `java-library` 的支持

| Removed Configuration | New Configuration         |
| :-------------------- | :------------------------ |
| `compile`             | `api` or `implementation` |
| `runtime`             | `runtimeOnly`             |
| `testRuntime`         | `testRuntimeOnly`         |
| `testCompile`         | `testImplementation`      |
| `Runtime`             | `RuntimeOnly`             |
| `Compile`             | `Implementation`          |

*Table 4. Mapping between Java module directives and Gradle configurations to declare dependencies*

| Java Module Directive        | Gradle Configuration | Purpose                                 |
| :--------------------------- | :------------------- | :-------------------------------------- |
| `requires`                   | `implementation`     | Declaring implementation dependencies   |
| `requires transitive`        | `api`                | Declaring API dependencies              |
| `requires static`            | `compileOnly`        | Declaring compile only dependencies     |
| `requires static transitive` | `compileOnlyApi`     | Declaring compile only API dependencies |

# Spring Cloud 匹配的 Spring Boot 版本

| Spring Cloud Version  | Spring Boot Version        |
| :-------------------- | :------------------------ |
| Hoxton.SR12           | 2.3.12.RELEASE            |
| 2020.0.4-SNAPSHOT     | 2.4.10                    |

# Spring Cloud 重要版本修改

## Hystrix 从 Spring Cloud Netflix 移除
`@EnableCircuitBreaker` as of the 3.0.1 release. Hystrix has been removed from Spring Cloud Netflix。

取而代之的是 `Resilience4J Circuit Breakers`. 
[resilience4j-circuit-breakers](https://docs.spring.io/spring-cloud-circuitbreaker/docs/2.0.3-SNAPSHOT/reference/html/#configuring-resilience4j-circuit-breakers)

# Spring Boot v2.4.x 开始，配置文件处理方式改变

Spring Boot 2.4 版本开始对 `application.properties` 和 `application.yml` 文件的处理方式进行了彻底的修改。
更新的逻辑旨在简化和合理化加载外部配置的方式。还允提供新功能，例如 `spring.config.import` 支持。
更新后的设计有意限制某些属性组合。这意味着从 Spring Boot 2.3 或更早版本升级时，可能需要更改一些内容。

**注意：** 从 Spring Boot 2.4 版本开始，所有的 jar 包外部拓展配置文件都会覆盖内部的配置属性。

[Config File Processing (application properties and YAML files)](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.4-Release-Notes)
[Spring Boot Config Data Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-Config-Data-Migration-Guide)
[Application properties outside of jar should take precedence over profile-specific variants inside the jar](https://github.com/spring-projects/spring-boot/issues/3845)
[External Application Properties](https://docs.spring.io/spring-boot/docs/2.4.0-SNAPSHOT/reference/html/spring-boot-features.html#boot-features-external-config-files)
[External Application Properties (Spring Boot 2.4.x 以前版本)](https://docs.spring.io/spring-boot/docs/2.3.12.RELEASE/reference/html/spring-boot-features.html#boot-features-external-config-application-property-files)

*v2.4.x* 以前的配置文件解析类 `org.springframework.boot.context.config.ConfigFileApplicationListener`

*v2.4.x* 配置文件加载重构使用类 `org.springframework.boot.context.config.ConfigDataEnvironmentPostProcessor#postProcessEnvironment(ConfigurableEnvironment, ResourceLoader, java.util.Collection<java.lang.String>)` + `org.springframework.boot.context.config.ConfigDataEnvironment`

## 配置加载顺序

列表按优先顺序排列：（来自较低项的值覆盖较早的项），来自加载文件的文档作为 `PropertySource` 添加到 Spring `Environment`。

1. The classpath root
2. The classpath `/config` package
3. The current directory
4. The `/config` subdirectory in the current directory
5. Immediate child directories of the `/config` subdirectory

默认的配置文件检索路径顺序列表：
1. `optional:classpath:/`
2. `optional:classpath:/config/`
3. `optional:file:./`
4. `optional:file:./config/*/`
5. `optional:file:./config/`

可以自定以默认配置文件 (`application`) 的名称 `spring.config.name`
可以自定义配置文件的检索路径 `spring.config.location`（逗号 `,` 分隔的目录/文件路径），目录以 `/` 结尾。当指定多个位置时，后面的位置会覆盖前面位置的值。
自定义配置文件检索的额外路径，不会覆盖默认的检索路径 `spring.config.additional-location`，额外的检索路径会排在默认的路径后

```shell script
$ java -jar myproject.jar --spring.config.name=myproject

$ java -jar myproject.jar --spring.config.location=optional:classpath:/default.properties,optional:classpath:/override.properties
```
> `optional` 参数表明该路径是可选的，不用关心该路径是否真实存在
> 注意： `spring.config.name` 和 `spring.config.location` 属性很早就会被使用来决定那些配置文件需要被加载，所以必须被定义为环境属性
>（typically an OS environment variable, a system property, or a command-line argument）
>
> 注意：在加载配置文件时，`profile-specific` 定义的配置文件总会覆盖 `non-specific` 配置文件的属性。
> 如果多个 `profiles` 同时被指定 （`spring.profiles.active`)，那么采用 `last-wins` 的策略应用属性值。

## 使用传统的模式 (Legacy Mode)
若是不想升级到现在新的配置数据处理逻辑，那么就需要切换到传统的模式，`spring.config.use-legacy-processing` 属性设置 `true`。
添加 `application.yml` 或者 `application.properties` 中并置于 `jar` 包当中。 

## 简单场景
若是仅使用了 `application.yml` 或者 `application.properties` 配置，
没有复杂的 `spring.profiles<.*>` 属性和没使用 multi-document （多文本） YAML ，那么升级就能正常工作。

## 多文档 (multi-document) YAML 排序
当使用 multi-document YAML (YAML 文件中使用 `---` 分隔符)，请注意了，现在 `property sources` 的添加顺序就是文档的定义顺序。
在 Spring Boot 2.3 及以前版本，各个文档添加的顺序基于配置文件激活 (profile activation) 顺序。

## Profile 配置文件的使用
在 Spring Boot 2.3 及以前版本，jar 包外面的 `application.properties` 配置文件属性不会覆盖 jar 包内的 `application-<profile>.properties`。

以前的 `application.yaml` 配置：
```yaml
spring:
  profiles: "prod"
secret: "production-password"
```

迁移后改为：
```yaml
spring:
  config:
    activate:
      on-profile: "prod"
secret: "production-password"
```

## Profile 激活
`spring.profiles.active` 属性任然用来激活指定的 profiles。
示例：在命令行运行 `$ java -jar myapp.jar --spring.profiles.active=prod`

可以把该属性配置到 `application.yml` 或者 `application.properties` 当中，但是不能配置到 `profile-specific` 的配置文件中。
换句话说，不能再将其与具有 `spring.config.activate.on-profile` 属性的文档结合使用。

no longer combine it with a document that has a `spring.config.activate.on-profile` property.
Likewise, you can still use the `spring.profiles.include` property, but only in non `profile-specific` documents.

```yaml
# this document is valid
spring:
  profiles:
    active: "prod"

---

# this document is invalid
spring:
  config:
    activate:
      on-profile: "prod"
  profiles:
    include: "metrics"
```

> 注意： The reason we have introduced this restriction is so that `on-profile` conditions are only evaluated once

# Spring boot 参数校验

## `@Validated` 和 `@Valid` 区别

Spring Validation 验证框架对参数的验证机制提供了 `@Validated`（Spring's JSR-303规范，是标准 JSR-303 的一个变种），
javax 提供了`@Valid`（标准JSR-303规范），配合 BindingResult 可以直接提供参数验证结果。

在检验 Controller 的入参是否符合规范时，使用 `@Validated` 或者 `@Valid` 在基本验证功能上没有太多区别。

但是在分组、注解地方、嵌套验证等功能上两个有所不同：

1. 分组
    - `@Validated`：提供了一个分组功能，可以在入参验证时，根据不同的分组采用不同的验证机制。
    - `@Valid`：作为标准 JSR-303 规范，还没有吸收分组的功能。

2. 注解地方
    - `@Validated`：可以用在类型、方法和方法参数上。但是不能用在成员属性（字段）上
    - `@Valid`：可以用在方法、构造函数、方法参数和成员属性（字段）上

注意：两者是否能用于成员属性（字段）上直接影响能否提供嵌套验证的功能。
`@Validated` 不能用在成员属性（字段）上，但是 `@Valid` 能加在成员属性（字段）上，而且 `@Valid` 类注解上也说明了它支持嵌套验证功能

- `@Validated` 和 `@Valid` 加在方法参数前，都不会自动对参数进行嵌套验证
- `@Valid` 在方法参数时并不能够自动进行嵌套验证，而是用在需要嵌套验证类的相应字段上在配合方法参数上 `@Validated` 或 `@Valid` 来进行嵌套验证。

```java
public class ValidateEntity {

    @NotNull(message = "id不能为空")
    @Min(value = 1, message = "id必须为正整数")
    private Long id;

    @Valid // 嵌套验证必须用 `@Valid`
    @NotNull(message = "props不能为空")
    @Size(min = 1, message = "props至少要有一个自定义属性")
    private List<Prop> props;
}

// 在 Controller 接口中检验参数
public class ValidateController {
    /** 参数上用 `@Validated` 或 `@Valid` 进行参数校验，
    若参数内部属性字段需要嵌套检验，在该字段上添加 `@Valid` 注解  */
    public void validateGet(@Valid ValidateEntity entity) {}
}
```

总结一下 `@Validated` 和 `@Valid` 在嵌套验证功能上的区别：
- `@Validated`：用在方法入参上无法单独提供嵌套验证功能。不能用在成员属性（字段）上，也无法提示框架进行嵌套验证。能配合嵌套验证注解 `@Valid` 进行嵌套验证。
- `@Valid`：用在方法入参上无法单独提供嵌套验证功能。能够用在成员属性（字段）上，提示验证框架进行嵌套验证。能配合嵌套验证注解 `@Valid` 进行嵌套验证。
