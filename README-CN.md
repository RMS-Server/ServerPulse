# ServerPulse

[English](README.md) | **简体中文**

一个轻量级的 Fabric 服务端模组，通过 HTTP API 实时暴露服务器性能指标。

## 支持版本

| Minecraft | Java |
|-----------|------|
| 1.17.1    | 16   |
| 1.21.4    | 21   |

## 功能特性

- 内置 HTTP 服务器随 Minecraft 服务器启动（默认端口 `25580`）
- 零外部依赖 — 使用 JDK 内置的 HTTP 服务器
- 使用 100 tick 环形缓冲区追踪 tick 耗时，提供准确的滚动平均值

## API 端点

### `GET /tps`

返回当前服务器性能指标。

**响应：**

```json
{
  "tps": 20.00,
  "mspt": 12.34,
  "mspt_min": 8.12,
  "mspt_max": 22.34
}
```

| 字段       | 说明                                     |
|------------|------------------------------------------|
| `tps`      | 每秒 tick 数（上限 20.0）                  |
| `mspt`     | 平均每 tick 毫秒数（最近 100 tick）         |
| `mspt_min` | 最小 tick 耗时（最近 100 tick）             |
| `mspt_max` | 最大 tick 耗时（最近 100 tick）             |

## 配置

首次启动时会在 `config/serverpulse.properties` 生成配置文件：

```properties
# ServerPulse configuration
port=25580
```

## 构建

```bash
./gradlew 1.17.1:build    # 构建 Minecraft 1.17.1 版本
./gradlew 1.21.4:build    # 构建 Minecraft 1.21.4 版本
```

输出 JAR 位于 `versions/<version>/build/libs/`。

## 许可证

本项目基于 GPL-3.0 许可证发布。
