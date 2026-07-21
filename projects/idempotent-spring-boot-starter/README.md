# Idempotent Spring Boot Starter

一个以“亲手实现核心逻辑”为目标的 Java 基础组件练习项目。工程骨架、Spring Boot 接线和示例应用已经准备好，幂等算法的关键路径由你完成。

## 你最终会得到什么

业务项目只需引入 Starter，并在方法上增加注解：

```java
@Idempotent(key = "#requestId", namespace = "create-order", ttlSeconds = 600)
public String createOrder(String requestId) {
    return "created:" + requestId;
}
```

同一个幂等键在有效期内只允许一个调用执行，其余调用得到明确的重复执行异常。

## 模块

```text
idempotent-core                 纯 Java 核心契约和内存仓储
idempotent-spring-boot-starter Spring 注解、AOP 和自动配置
idempotent-example              可启动的演示应用
```

## 环境

- JDK 21
- Maven 3.6.3+

本机可以这样选择 JDK 21：

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
mvn test
```

## 你的三个核心练习

按顺序完成，每完成一个就删除对应测试上的 `@Disabled`：

1. `InMemoryIdempotentRepository`：使用线程安全结构实现原子抢占、成功标记和释放。
2. `IdempotentExecutor`：完成“抢占 → 执行业务 → 成功/失败收尾”的状态流转。
3. `SpelIdempotentKeyResolver`：从方法参数中解析 SpEL 表达式并生成稳定 Key。

搜索所有练习位置：

```bash
rg "TODO learner" .
```

## 验证节奏

```bash
# 骨架应当始终可编译
mvn test

# 完成核心执行器后，仅运行对应测试
mvn -pl idempotent-core -Dtest=IdempotentExecutorTest test

# 安装本项目的三个本地模块
mvn install

# 启动示例
cd idempotent-example
mvn spring-boot:run
```

示例请求：

```bash
curl "http://localhost:8080/orders/demo-001"
```

第一次调用核心 TODO 尚未完成时返回错误是预期行为。实现三个练习后，同一个 `demo-001` 的第二次调用应被识别为重复执行。

## 当前范围

第一阶段只实现内存版，重点学清楚契约、状态和并发。Redis、结果复用、续租、监控指标都留到后续迭代，不提前堆进骨架。
