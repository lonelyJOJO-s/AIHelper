# Go Delay Queue

一个用 Go 手搓的本地延迟任务队列练习项目。第一版先用内存存储和标准库 HTTP，重点练清楚任务状态、调度循环、重试和取消。

## 模块

```text
cmd/server          HTTP 服务入口
internal/api        REST API
internal/job        任务领域模型
internal/store      内存仓储
internal/scheduler  调度器外壳和核心练习点
docs                设计和练习记录
```

## 运行

```bash
go test ./...
go run ./cmd/server
```

服务默认监听 `:8081`。

## API

提交任务：

```bash
curl -X POST http://localhost:8081/jobs \
  -H 'Content-Type: application/json' \
  -d '{
    "name": "send_welcome_message",
    "runAt": "2026-07-22T20:00:00+08:00",
    "maxRetries": 3,
    "payload": {"userId": "1001"}
  }'
```

查看任务：

```bash
curl http://localhost:8081/jobs
curl http://localhost:8081/jobs/{id}
```

取消任务：

```bash
curl -X POST http://localhost:8081/jobs/{id}/cancel
```

## 你的核心练习

1. `Scheduler.Cancel`：实现任务取消状态流转。
2. 调度队列：用 `container/heap` 维护按 `RunAt` 排序的 pending jobs。
3. 执行循环：到时间取出任务，标记 running，执行 handler。
4. 失败重试：失败时增加 attempts，未超过 `MaxRetries` 则重新入队。
5. 优雅关闭：服务退出时停止接收新任务，让当前执行收尾。

搜索练习位置：

```bash
rg "TODO learner" .
```
