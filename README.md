# 统一规范项目

## 包含功能

* 统一返回数据结构、状态码、错误码
* 统一异常处理
* 采用 `log4j2` 日志记录，添加每次请求的追踪 ID 支持
* 用户登录采用 [JWT (auth0)] 认证，采用注解指定该 uri 是否需要认证
* 记录每次请求的详细信息切面

## 参考

* [Redisson 配置](https://github.com/redisson/redisson/wiki/2.-%E9%85%8D%E7%BD%AE%E6%96%B9%E6%B3%95)
* [Redisson 配置英文版](https://github.com/redisson/redisson/wiki/2.-Configuration)