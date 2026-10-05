# DSHApp

Run the full **DeepSeek Harness (DSH)** locally on Android phones and tablets. Integrates DSH, PRoot, WebView, Debian and Node.js, together with a terminal, a file manager and other tools, into a personal AI workbench. No root required, no separate Termux installation.

> This project is a fork of [DSHBox](https://github.com/WSK-build/DSHBox).

---

## Highlights

### DeepSeek Harness embedded

- DSH ships inside the APK and is assembled into `runtime-current/dsh` on first boot (version arbitration; previous layer kept in `previous/dsh`)
- The `DSH` tab opens `http://127.0.0.1:3080` in an embedded WebView: launchToken session auth, mobile UA, keyboard adaptation, pinch-zoom, floating refresh
- Runtime hard-link compatibility shim: `node --import` preload replaces `node:fs/promises.link` at runtime, degrading to a semantically equivalent content copy when the platform rejects `link()` — **zero changes to DSH source**
- Foreground service notification with Start / Restart / Stop quick actions

### Phone assistant (DshPilot)

A mailbox channel (file delivery, no network, no shared memory) between the embedded sandbox and the Android app layer lets DSH control the phone safely.

- Execution backends: accessibility / Shizuku privileged / direct platform calls
- Execution modes: foreground / background virtual display
- Approval tiers per capability: deny / ask / always allow, with floating-window questions
- Bundled DSH plugin `@local/mobile-pilot` exposes `phone_*` native tools

### Plugin management & marketplace

- Bundled plugin toggles (`dsh-mobile-adapt`, `mobile-pilot`)
- Absolute safe mode, plugin load logs, crash-repair helper (opencode)
- Marketplace with awesome-dsh-plugin and awesome-dsh-mobile-plugins live feeds

### Layered PRoot runtime (no root)

| Layer | Content | Guest mount |
|---|---|---|
| base | Slim Debian 13 (trixie) rootfs | `/` |
| node | Node.js 24 | `/usr/local` |
| dsh | DeepSeek Harness (npm package) | `/opt/dshapp/runtime` |
| android-side | PRoot / loader / shmem | — |

- Sandbox keepalive and DSH run as two separate PRoot processes; shutdown enumerates the process tree via `/proc` and SIGKILLs children first
- Each layer carries a SHA-256 sentinel verified on startup
- `base` and `node` are installed via "Get runtime online" (multiple mirrors) or offline bundle import

### File manager

Dual-view browsing, move/rename/delete, multi-select batch ops, import/export, global search, `.deb` install into sandbox; a universal viewer/editor (text/code, images, PDF, archives, hex, Office, Markdown/HTML/SVG).

### Terminal

Multi-window terminal (full PRoot Debian environment), bundled tools (jq / sqlite3 / patch / nano / strings), two helper key rows, pinch-zoom; can run the `dsh` CLI directly (web / headless / tui / plugin).

### Updates & import (Settings)

- Update DSH (online multi-mirror / offline single-layer package import); reset any runtime layer
- Offline import of the full runtime bundle (per-layer SHA-256 verification, `previous/` single-slot rollback)
- Diagnostics & logs (DSH / sandbox / guest command logs, WebView kernel fingerprint)

## UI (5 bottom tabs)

| Tab | Purpose |
|---|---|
| Home | sandbox / DSH status cards, start/restart/stop, DshPilot & Cordis entries |
| Files | dual-view browsing, move/rename/delete, multi-select, import/export, viewers/editor |
| DSH | embedded WebView at `http://127.0.0.1:3080` |
| Terminal | multi-window terminal; run `dsh` CLI directly |
| Settings | appearance, storage & cleanup, check updates, DSH update, runtime import, diagnostics, about |

## Build from source

| Env | Version |
|---|---|
| JDK | 21 |
| Android SDK | compileSdk / targetSdk 36 · build-tools 36.0.0 |
| Gradle | wrapper 8.11.1 (AGP 8.9.2 · Kotlin 2.0.21) |

The runtime layers are not in this repository; fetch `../runtime/` first (`runtime/android-assets/dsh/` and `runtime/android-assets/runtime/`), otherwise the APK will not embed the DSH and android-side layers.

```bash
./gradlew testDebugUnitTest     # full JVM unit tests
./gradlew :app:assembleRelease  # output: app/build/outputs/apk/release/app-release.apk
```

A `Build Release APK` GitHub Actions workflow is included: it downloads the embedded runtime assets from the upstream release, signs with a CI key and produces a complete APK (artifact `dshbox-release-apk`).

## Runtime large files (not in this repository)

| Path in release | Content |
|---|---|
| `runtime/android-assets/runtime/android-side.tar.zst` | host-side PRoot / loader / shmem (embedded in APK) |
| `runtime/android-assets/dsh/<version>.tar.zst` | DSH layer (embedded in APK) |
| `runtime/offline-baseline/{base,node}.tar.zst` | Debian and Node layers (not in APK; for offline import) |

## License

GPL v3 (see [LICENSE](LICENSE)). Third-party components keep their own licenses; see `THIRD_PARTY_NOTICES.md`.
