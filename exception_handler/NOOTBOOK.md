# 全局异常统一处理

## 1. 为什么需要全局异常处理
  - 统一异常响应格式，提升接口一致性
  - 集中日志记录，便于排查问题
  - 避免异常信息泄露，增强安全性
  - 简化 Controller 代码

## 2. 实现方式

  - 使用 Spring Boot 提供的 `@ControllerAdvice` 注解
  - 使用 `@ExceptionHandler` 注解处理特定异常
  - 使用 `@ResponseStatus` 注解设置响应状态码
  - 使用 `@ResponseBody` 注解返回 JSON 格式响应

### 2.0 结构目录

```plaintext
exception_handler/src/main/java
├── exception_handler/                 # 模块主包，存放全局异常处理相关代码
│   ├── controller/                    # 控制器包，放置业务 Controller 类
│   ├── entity/                        # 实体包，定义统一响应体等数据结构；并存放了错误信息的枚举类（可创建单独的 emum 包）
│   ├── exception/                     # 异常包，存放自定义异常类以及基类
│   ├── handler/                       # 异常处理包，存放全局异常处理器类
│   └── ExHandlerApplication.java      # Spring Boot 启动类
```


### 2.1 关键注解说明

- `@RestControllerAdvice`：全局异常处理组件，作用于所有 @RestController。
- `@ExceptionHandler(Exception.class)`：指定处理的异常类型

### 2.2 示例代码

```java
// exception_handler/handler/GlobalExceptionHandler.java
package exception_handler;

import exception_handler.entity.WebResp;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public WebResp<?> handleException(Exception e, HttpServletRequest request) {
        // 记录日志
        e.printStackTrace();
        return WebResp.failure("INTERNAL_SERVER_ERROR", null);
    }
}
```

### 2.3 响应体结构

使用统一的请求响应结构

```json
{
  "code": "INTERNAL_SERVER_ERROR",
  "msg": "服务器内部异常",
  "data": null
}
```

## 3. 常见问题排查

- 全局异常处理类需在 Spring Boot 扫描路径下
- 方法参数类型应为 Exception 和 HttpServletRequest
- 避免异常被局部 try-catch 捕获
- 检查是否有其他异常处理器优先级更高

## 4. 测试建议

可通过集成测试（@SpringBootTest + MockMvc）验证异常处理效果，确保所有异常均被统一拦截并返回规范响应。

---
## 结论：
通过全局异常处理机制，可以大幅提升 Spring Boot 项目的健壮性和可维护性。
