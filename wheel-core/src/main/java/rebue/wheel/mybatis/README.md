# mybatis自动处理枚举类型的转换

## 启用方法

在spring boot启用

1. 依赖mybatis-spring-boot-starter
2. Spring Boot的配置文件配置如下： 

```yaml
mybatis:
  configuration:
    default-enum-type-handler: rebue.wheel.mybatis.AutoEnumTypeHandler
```