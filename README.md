# Anchor

> 锚定身份，每日行动。

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Version](https://img.shields.io/badge/version-1.1-blue.svg)](app/build.gradle.kts)
[![Min SDK](https://img.shields.io/badge/minSdk-26-orange.svg)](app/build.gradle.kts)

**Anchor** 是一款极简风格的 Android 每日行动应用。通过「身份锚点」建立自我认同，用固定任务与可选任务驱动每日行动，辅以启动页激励语与可靠的后台提醒，让习惯可持续。

**仓库地址**：[github.com/eula9/Anchor](https://github.com/eula9/Anchor)

**最新版本**：[v1.1 下载 APK](https://github.com/eula9/Anchor/releases/download/v1.1/Anchor.apk) · [Release 说明](https://github.com/eula9/Anchor/releases/tag/v1.1)

---

## 为什么叫 Anchor？

锚（Anchor）的含义很简单：先确认「我是谁」，再完成「我今天该做的事」。身份是方向，任务是行动，提醒与统计让这一切可以持续下去。

---

## 功能一览

| 模块 | 说明 |
|------|------|
| **启动页** | 纯文字界面：居中展示身份宣言与「今日一句」激励语，点击屏幕进入首页 |
| **身份锚点** | 自定义身份宣言（≤30 字）、周期 7/14/30 天或自定义 1~365 天 |
| **固定任务** | 3~6 条每日必做任务，全部完成计连续行动天数 |
| **可选任务** | 每天最多 3 条临时想做的事 |
| **今日一句** | 内置 100 条激励语（现代励志为主），每天随机一条 |
| **连续行动** | 完成全部固定任务延续连续记录，展示最长记录 |
| **统计** | 行动率、连续天数、任务完成总数、近 7 日柱状图 |
| **每日提醒** | AlarmManager 闹钟 + WorkManager 备用，支持国产机后台引导 |
| **深色模式** | 跟随系统 / 浅色 / 深色 |
| **数据备份** | JSON 导出与导入（备份版本 v3） |

---

## 版本更新（v1.1）

- 产品重构为「身份锚点 + 固定/可选任务」体系
- 新增启动页（身份宣言 + 每日激励语，无图标纯文字）
- 新增统计页与底部导航（首页 / 统计 / 设置）
- 通知改用 `AlarmManager.setAlarmClock`，划掉后台后更可靠
- 支持小米 / 华为 / OPPO / vivo 等国产机后台权限引导
- 每次打开应用自动重登记闹钟，备用 Worker 兜底通知

---

## 截图

> 运行应用后，可将截图放入 `docs/screenshots/` 并更新此处。

```
docs/screenshots/
├── launch.png     # 启动页：身份宣言 + 今日一句
├── home.png       # 首页：锚点、连续行动、任务列表
├── stats.png      # 统计页
└── settings.png   # 设置页
```

---

## 技术栈

- **Kotlin** + **Jetpack Compose** + **Material 3**
- **MVVM** + **Clean Architecture**（`data` / `domain` / `ui` 分层）
- **Room v3** — 任务与每日打卡记录
- **DataStore** — 身份锚点、主题、通知、连续天数、激励语
- **AlarmManager** — 每日定时提醒（主通道）
- **WorkManager** — 数据维护、备用通知、锚点到期检查
- **Navigation Compose** — 启动页 / 首页 / 统计 / 设置
- **SplashScreen API** — 无图标纯白启动闪屏

| 配置项 | 值 |
|--------|-----|
| versionName | 1.1 |
| versionCode | 2 |
| minSdk | 26（Android 8.0） |
| targetSdk | 36 |
| compileSdk | 36 |

---

## 项目结构

```
app/src/main/java/com/example/anchor/
├── data/              # Room、DataStore、Repository、通知、备份、Worker
│   ├── notification/  # AlarmManager 调度、BroadcastReceiver
│   ├── source/        # 100 条内置激励语
│   └── worker/        # 维护、备用通知、锚点到期
├── domain/            # 领域模型与 Repository 接口
├── ui/
│   ├── launch/        # 启动页
│   ├── home/          # 首页
│   ├── stats/         # 统计
│   ├── setup/         # 首次引导
│   └── settings/      # 设置
├── di/                # AppContainer 手动依赖注入
└── util/              # 权限、OEM 引导、常量
```

---

## 快速开始

### 直接安装（推荐）

无需编译，下载 [v1.1 APK](https://github.com/eula9/Anchor/releases/download/v1.1/Anchor.apk) 安装即可。

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

Debug APK 输出路径：

```
app/build/outputs/apk/debug/app-debug.apk
```

> APK 为构建产物，已由 `.gitignore` 排除，不会提交到 Git。请从 [Releases](https://github.com/eula9/Anchor/releases) 下载，或本地执行 `assembleDebug` 构建。

---

## 使用指南

### 首次使用

1. 设置身份宣言、锚点周期与 3~6 条固定任务
2. 进入启动页查看今日身份与激励语，点击进入首页

### 启动页

- 屏幕中央：**身份宣言**（上）+ **今日一句**（下）
- 无图标、无卡片，点击任意位置进入首页

### 首页

- **身份锚点**：当前宣言与周期进度
- **连续行动**：今日固定任务是否全部完成
- **固定任务**：勾选完成（不可取消）
- **可选任务**：每天最多添加 3 条

### 统计页

- 行动率、连续天数、任务完成总数
- 近 7 日完成趋势柱状图

### 设置页

- **深色模式** — 切换主题
- **每日锁屏提醒** — 通知 / 精确闹钟 / 电池优化 / 厂商后台引导
- **后台任务** — WorkManager 状态
- **数据备份** — 导出 / 导入 JSON

### 国产手机提醒设置建议

开启每日提醒时，请依次完成系统权限，并在厂商设置中：

- 开启 **自启动**
- 省电策略选 **无限制** / **不优化**
- 多任务界面 **锁定应用**

完成后点击 **「我已完成设置」**（系统无法自动检测自启动状态）。

---

## 权限说明

| 权限 | 用途 |
|------|------|
| `POST_NOTIFICATIONS` | Android 13+ 发送每日身份提醒 |
| `SCHEDULE_EXACT_ALARM` | 精确定时闹钟 |
| `RECEIVE_BOOT_COMPLETED` | 开机后恢复闹钟 |
| `WAKE_LOCK` | 闹钟触发时唤醒设备 |
| `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` | 申请忽略电池优化 |

---

## 路线图

- [x] 身份锚点体系（宣言 + 周期 + 固定任务）
- [x] 启动页激励语（100 条）
- [x] 固定 / 可选任务、连续行动、统计页
- [x] AlarmManager 可靠通知 + 国产机后台引导
- [x] 数据备份 v3、深色模式
- [x] v1.1 Release 调试包
- [ ] 应用截图与 Release 签名包

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
