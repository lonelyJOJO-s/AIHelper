# System Design 101 每日学习

一个基于 [ByteByteGoHq/system-design-101](https://github.com/ByteByteGoHq/system-design-101) 的持续系统设计学习项目。目标是用短时、持续、可讨论、可追踪的方式，从基础到进阶构建 API/Web、数据库与存储、缓存与性能、消息队列/分布式系统及真实案例的知识体系。

## 学习来源

每次学习从 system-design-101 仓库的最新 README 及其中直接链接的内容选择主题，并保留准确来源链接。内容使用中文整理，不大段复制原文。

## 云端学习任务

- 任务名称：System Design 101 每日学习
- 时区：Asia/Singapore
- 运行时间：每天 09:00、20:00
- 执行方式：云端运行，不依赖本地电脑或本地工作目录
- 学习时长：每次约 5–10 分钟阅读量
- 晚间策略：若上一主题尚未完成，优先复习、答疑或补充

## 讨论与结束口令

推送学习内容后，可以继续追问、讨论和纠错。只有明确说出以下口令或同义表达时，才结束并总结本次学习：

- “总结”
- “本次结束”
- “学完了”

未收到结束口令时，不生成 session 总结，也不把当前主题标记为完成。

## GitHub 持久化规则

- 所有状态均持久化到本仓库默认分支，不依赖本地文件。
- 每次开始前读取 `learning/system-design-101/progress.md`，用于避免重复、控制难度和类别轮换。
- 结束后将完整总结写入 `learning/system-design-101/sessions/YYYY-MM-DD-HHmm-主题短名.md`。
- 总结包含日期时间、主题和来源、核心知识、用户观点、纠正的误区、待解决问题及下一步建议。
- 同步更新 `learning/system-design-101/progress.md`。
- 学习会话提交信息格式：`docs(system-design): add learning session YYYY-MM-DD HHmm`。
- GitHub 写入失败时必须报告实际错误，不得声称已保存。

## 目录结构

```text
learning/system-design-101/
├── README.md
├── progress.md
└── sessions/
    ├── .gitkeep
    └── YYYY-MM-DD-HHmm-主题短名.md
```
