# 🪞 镜界 (Lens Browser)

[![Build APK](https://github.com/obrige/lens-browser/actions/workflows/build.yml/badge.svg)](https://github.com/obrige/lens-browser/actions/workflows/build.yml)

轻量 Android WebView 指纹隔离浏览器 · 48 项指纹向量全部隔离。

## 本地构建

```bash
# 前提条件
# - JDK 17+
# - Android SDK (compileSdk 35)

# 克隆项目
git clone https://github.com/obrige/lens-browser.git
cd lens-browser

# 生成图标（可选）
python3 scripts/generate_icons.py

# 构建 Debug APK
./gradlew assembleDebug
# 输出: app/build/outputs/apk/debug/app-debug.apk
```

## 在线构建

每次 push 到 `main` 分支，GitHub Actions 自动构建 APK → [Actions 页签](https://github.com/obrige/lens-browser/actions)。

## 架构

```
shield/
  ShieldProfile.kt          ← 伪装身份（改一处改全局）
  ShieldPatch.kt            ← 补丁接口
  ShieldAssembler.kt        ← 组装所有补丁
  patches/
    NavPatch.kt             ← Navigator 属性
    ScreenPatch.kt          ← Screen / Window 尺寸
    CanvasPatch.kt          ← Canvas 像素噪声
    WebGLPatch.kt           ← GPU 参数
    AudioPatch.kt           ← AudioContext 噪声
    RtcPatch.kt             ← WebRTC 阻断
    PerfPatch.kt            ← Performance API
    TimezonePatch.kt        ← 时区 / Intl
    MiscPatch.kt            ← 字体 / Notification / SW...
browser/
    LensWebView.kt          ← WebView 隐私配置
    LensWebViewClient.kt    ← HTML 拦截注入
    LensChromeClient.kt     ← 权限控制
```

## 添加新补丁

1. 在 `shield/patches/` 新建类实现 `ShieldPatch`
2. 在 `ShieldAssembler.patches` 列表加一行
3. 完成

## 验证

打开 [browserleaks.com/canvas](https://browserleaks.com/canvas) 或 [fingerprint.com/demo](https://fingerprint.com/demo) 测试。

## 许可证

MIT License
