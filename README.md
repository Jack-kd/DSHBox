# DSHApp

在安卓手机和平板上本机运行完整 **DeepSeek Harness（DSH）** 的开源应用。集成 DSH、PRoot、WebView、Debian 与 Node.js，配套终端、文件管理等工具，组成个人 AI 工作台。无需 Root，无需单独安装 Termux。

> 本项目由 [DSHBox](https://github.com/WSK-build/DSHBox) 二次开发而来。

---

## 核心特性

### DeepSeek Harness 全内嵌

- DSH 随 APK 内置，首启按版本仲裁装配到 `runtime-current/dsh`，换层时旧层备份到 `previous/dsh`
- `DSH` 标签页内嵌 WebView 打开 `http://127.0.0.1:3080`：自动解析 launchToken 完成会话认证、移动 UA、键盘自适应、双指缩放、悬浮刷新
- 运行期硬链接兼容垫片：`node --import` 预加载，运行期替换 `node:fs/promises` 的 `link`，被平台拒绝时退化为语义等价的内容拷贝——**DSH 源码不改一个字节**
- 前台服务通知带「启动 / 重启 / 关闭」快捷操作

### 手机助手（DshPilot）

内建手机操控能力表，在沙箱与安卓应用层之间建立信箱通道（文件投递，无网络、无共享内存），让沙箱内的 DSH 安全可控地操控手机。

- 执行通路：无障碍模式 / Shizuku 特权模式 / 平台直连调用
- 执行模式：前台 / 后台虚拟屏
- 审批约束：按能力选择「禁止 / 询问审批 / 完全访问」，支持悬浮窗提问交互
- 随包 DSH 插件 `@local/mobile-pilot` 提供 `phone_*` 原生工具

### 插件管理与插件市场

- 随包插件开关（`dsh-mobile-adapt` 移动端适配、`mobile-pilot` 手机操控）
- 绝对安全模式、插件加载记录、崩溃修复辅助（opencode）
- 插件市场：内置 awesome-dsh-plugin 与 awesome-dsh-mobile-plugins 实时数据源

### PRoot 分层运行环境（无需 Root）

| 层 | 内容 | guest 挂载点 |
|---|---|---|
| base | Debian 13 (trixie) 精简版 rootfs | `/` |
| node | Node.js 24 | `/usr/local` |
| dsh | DeepSeek Harness（npm 包） | `/opt/dshapp/runtime` |
| android-side | PRoot / loader / shmem | — |

- 沙箱 keepalive 与 DSH 为两个独立 PRoot 进程，停机按 `/proc` 枚举整棵进程树、子进程优先 SIGKILL
- 每层带 SHA-256 哨兵，启动时逐层校验完整性
- `base` 与 `node` 两层由「在线获取运行环境」（多镜像源）或「离线导入整包」安装

### 文件管理

双视图浏览、移动/重命名/删除、多选批量、导入/导出、全局搜索、`.deb` 安装到沙箱；通用查看器/编辑器（文本/代码、图片、PDF、压缩包、十六进制、Office、Markdown/HTML/SVG）。

### 终端

多窗口终端（PRoot Debian 完整环境）、随包小工具（jq / sqlite3 / patch / nano / strings）、两行辅助按键栏、双指缩放；可直接运行 `dsh` CLI（web / headless / tui / plugin）。

### 更新与导入（设置页）

- 更新 DSH（在线多镜像源 / 离线导入单文件层包），可自由重置任意运行环境层
- 离线导入运行环境整包（逐层 SHA-256 校验，`previous/` 单份可回滚）
- 诊断与日志（DSH / 沙箱 / 访客命令日志，含 WebView 真实内核指纹）

## 界面（底部 5 个标签）

| 标签 | 功能 |
|---|---|
| 首页 | 沙箱 / DSH 状态卡片、启动/重启/关闭、DshPilot 与 Cordis 入口 |
| 文件 | 双视图浏览、移动/重命名/删除、多选批量、导入/导出、查看器/编辑器 |
| DSH | 内嵌 WebView 加载 `http://127.0.0.1:3080` |
| 终端 | 多窗口终端、可直接运行 `dsh` CLI |
| 设置 | 外观、存储与清理、检查更新、DSH 更新、导入运行环境、诊断与日志、关于 |

## 从源码构建

| 环境 | 版本 |
|---|---|
| JDK | 21 |
| Android SDK | compileSdk / targetSdk 36 · build-tools 36.0.0 |
| Gradle | wrapper 8.11.1（AGP 8.9.2 · Kotlin 2.0.21） |

运行环境大层不在本仓库，构建前请先获取 `../runtime/`（`runtime/android-assets/dsh/` 与 `runtime/android-assets/runtime/`），否则 APK 不内嵌 DSH 层与 android-side 层。

```bash
./gradlew testDebugUnitTest     # 全量 JVM 单测
./gradlew :app:assembleRelease  # 产物：app/build/outputs/apk/release/app-release.apk
```

GitHub Actions 已内置 `Build Release APK` 工作流：自动从上游 Release 下载内嵌运行环境资产、CI 签名并产出完整 APK（artifact `dshbox-release-apk`）。

## 运行环境大文件（不在本仓库）

| 发布包内路径 | 内容 |
|---|---|
| `runtime/android-assets/runtime/android-side.tar.zst` | 宿主侧 PRoot / loader / shmem（内嵌进 APK） |
| `runtime/android-assets/dsh/<version>.tar.zst` | DSH 层（内嵌进 APK） |
| `runtime/offline-baseline/{base,node}.tar.zst` | Debian 层与 Node 层（不进 APK，离线导入用） |

## 许可证

本项目采用 **GPL v3**（见 [LICENSE](LICENSE)）。第三方组件按其各自原许可继续适用，详见 `THIRD_PARTY_NOTICES.md`。
