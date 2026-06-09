# Anchor

> 锚定今日身份，聚焦三件要事。

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Min SDK](https://img.shields.io/badge/minSdk-26-orange.svg)](app/build.gradle.kts)

**Anchor** 是一款极简风格的 Android 每日行动应用。它不追求复杂的待办体系，而是用「今日身份」帮你建立自我认同，用「今日三件事」把精力集中在当下真正重要的事上。

**仓库地址**：[github.com/eula9/Anchor](https://github.com/eula9/Anchor)

---

## 为什么叫 Anchor？

锚（Anchor）的含义很简单：在每天开始时，先确认「我是谁」，再决定「我要做什么」。身份是方向，三件事是行动，提醒与备份让这一切可以持续下去。

---

## 功能一览

| 模块 | 说明 |
|------|------|
| 今日身份 | 内置 50 条身份宣言，每天随机一条；同日不变，跨天自动切换 |
| 今日三件事 | 每天最多 3 条任务，支持勾选完成，次日自动清空 |
| 锁屏提醒 | 可自定义推送时间，在锁屏展示今日身份 |
| 深色模式 | 跟随系统 / 浅色 / 深色 |
| 数据备份 | JSON 导出与导入，兼容 Android 系统自动备份 |
| 后台维护 | WorkManager 负责定时通知与跨天数据清理 |
| 连续行动 | 完成至少一件事即计为行动，统计连续天数与最长记录 |

---

## 截图

> 运行应用后，可将截图放入 `docs/screenshots/` 并更新此处。

```
docs/screenshots/
├── home.png       # 首页：身份 + 三件事
└── settings.png   # 设置页
```

---

## 技术栈

- **Kotlin** + **Jetpack Compose** + **Material 3**
- **MVVM** + **Clean Architecture**（`data` / `domain` / `ui` 分层）
- **Room** — 今日三件事本地存储
- **DataStore** — 身份、主题、通知偏好
- **WorkManager** — 每日通知与维护任务
- **Navigation Compose** — 页面导航
- **Gradle Version Catalog** — 统一依赖版本管理

| 配置项 | 值 |
|--------|-----|
| minSdk | 26（Android 8.0） |
| targetSdk | 36 |
| compileSdk | 36 |

---

## 项目结构

```
app/src/main/java/com/example/anchor/
├── data/              # Room、DataStore、Repository 实现、通知、备份、Worker
├── domain/            # 领域模型与 Repository 接口
├── ui/                # Compose 界面、ViewModel、导航、主题
├── di/                # AppContainer 手动依赖注入
└── util/              # 常量与工具类
```

---

## 快速开始

### 克隆项目

```bash
git clone https://github.com/eula9/Anchor.git
cd Anchor
```

### 环境要求

- [Android Studio](https://developer.android.com/studio)（推荐最新稳定版）
- JDK 17+（Android Studio 自带 JBR 即可）
- Android SDK（通过 SDK Manager 安装）

### 运行

1. 用 Android Studio 打开项目根目录
2. 等待 Gradle Sync 完成（首次会自动生成 `local.properties`，该文件不会提交到 Git）
3. 连接真机或启动模拟器（API 26+）
4. 点击 **Run** 运行

### 命令行构建

```bash
# Windows
.\gradlew.bat assembleDebug

# macOS / Linux
./gradlew assembleDebug
```

Debug APK 输出路径：`app/build/outputs/apk/debug/app-debug.apk`

---

## 使用指南

### 首页

- 顶部 **今日身份** 卡片：展示当天的身份宣言
- 下方 **今日三件事**：输入后回车添加，点击勾选标记完成

### 设置页

从首页右上角齿轮图标进入：

- **深色模式** — 切换主题外观
- **每日锁屏提醒** — 授权通知并设置推送时间
- **后台任务** — 查看 WorkManager 运行状态
- **数据备份** — 导出 / 导入 JSON 备份文件

---

## 权限说明

| 权限 | 用途 |
|------|------|
| `POST_NOTIFICATIONS` | Android 13+ 发送每日身份锁屏通知 |

---

## 路线图

- [x] 今日身份（50 条宣言 + 每日随机）
- [x] 今日三件事（Room + 跨天清空）
- [x] 锁屏通知与自定义时间
- [x] 深色模式、数据备份、WorkManager
- [x] 连续行动天数统计
- [ ] 应用截图与 Release 构建

---

## 参与贡献

欢迎提交 Issue 或 Pull Request。开始前建议先 Fork 仓库，在本地完成修改后再发起 PR。

```bash
git checkout -b feature/your-feature
# 修改代码…
git commit -m "feat: 描述你的改动"
git push origin feature/your-feature
```

---

## 开源协议

本项目采用 [MIT License](LICENSE) 开源。

---

## 作者

**[eula9](https://github.com/eula9)** — [Anchor](https://github.com/eula9/Anchor)
