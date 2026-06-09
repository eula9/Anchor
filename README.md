# Anchor

一款极简风格的 Android 每日行动应用。用「今日身份」锚定自我认知，用「今日三件事」聚焦当下行动，通过锁屏提醒与数据备份让习惯可持续。

## 功能特性

- **今日身份** — 内置 50 条身份宣言，每天随机一条，同日不变、跨天自动切换
- **今日三件事** — 每天最多 3 条任务，支持勾选完成，次日自动清空
- **每日锁屏提醒** — 可自定义推送时间，在锁屏展示今日身份
- **深色模式** — 跟随系统 / 浅色 / 深色三种主题
- **数据备份** — 支持 JSON 导出与导入，兼容系统自动备份
- **后台任务** — WorkManager 定时通知与数据维护

## 技术栈

| 类别 | 技术 |
|------|------|
| 语言 | Kotlin |
| UI | Jetpack Compose + Material 3 |
| 架构 | MVVM + Clean Architecture |
| 导航 | Navigation Compose |
| 本地存储 | Room + DataStore |
| 后台任务 | WorkManager |
| 依赖管理 | Gradle Version Catalog |

- 最低支持：**Android 8.0（API 26）**
- 编译 SDK：36

## 项目结构

```
app/src/main/java/com/example/anchor/
├── data/           # 数据层（Room、DataStore、Worker、通知）
├── domain/         # 领域层（Model、Repository 接口）
├── ui/             # 表现层（Compose 界面、ViewModel）
├── di/             # 依赖容器 AppContainer
└── util/           # 工具类与常量
```

## 环境要求

- [Android Studio](https://developer.android.com/studio)（推荐最新稳定版）
- JDK 17+（Android Studio 自带 JBR 即可）
- Android SDK（通过 Android Studio SDK Manager 安装）

## 快速开始

### 1. 克隆项目

```bash
git clone https://github.com/你的用户名/Anchor.git
cd Anchor
```

### 2. 配置本地 SDK 路径

首次打开项目时，Android Studio 会自动生成 `local.properties`（已加入 `.gitignore`，不会上传到 GitHub）。

若需手动创建，在项目根目录新建 `local.properties`：

```properties
sdk.dir=C\:\\Users\\你的用户名\\AppData\\Local\\Android\\Sdk
```

### 3. 同步并运行

1. 用 Android Studio 打开项目根目录
2. 点击 **Sync Project with Gradle Files**
3. 连接真机或启动模拟器（API 26+）
4. 点击 **Run** 运行应用

### 命令行构建

```bash
# Windows
.\gradlew.bat assembleDebug

# macOS / Linux
./gradlew assembleDebug
```

生成的 APK 位于：`app/build/outputs/apk/debug/app-debug.apk`

## 使用说明

### 首页

- 顶部展示**今日身份**卡片
- 下方管理**今日三件事**（输入后按回车添加）

### 设置（首页右上角齿轮图标）

| 模块 | 说明 |
|------|------|
| 深色模式 | 跟随系统 / 浅色 / 深色 |
| 每日锁屏提醒 | 开启通知权限、设定推送时间 |
| 后台任务 | 查看 WorkManager 任务状态 |
| 数据备份 | 导出 / 导入 JSON 备份文件 |

## 权限说明

| 权限 | 用途 |
|------|------|
| `POST_NOTIFICATIONS` | Android 13+ 发送每日身份锁屏通知 |

## 开源协议

本项目采用 [MIT License](LICENSE) 开源。

## 致谢

基于 Android Jetpack 官方推荐架构与实践构建。
