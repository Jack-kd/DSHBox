package com.dshbox.app.common

/**
 * 在线获取 base 层（Debian）可用的 archive 源。
 *
 * 每个源都必须能提供 `dists/<suite>/Release`（含各索引文件 SHA256，作完整性锚点）与
 * `dists/<suite>/main/binary-<arch>/Packages.gz`，且对应 pool .deb 可下载。
 * URL 末尾不带斜杠（探测/拼接时统一处理）；[note] 是展示给用户的补充说明。
 * 运行时不标注地区：并行探测按「是否含所需内容 + 延迟」自动排序，用户自选。
 */
data class DebianArchiveSource(
    /** 展示名（可本地化 [UiText]）。 */
    val name: UiText,
    val url: String,
    /** 展示给用户的补充说明（可本地化）。 */
    val note: UiText,
) {
    /** Release 文件（内含 Packages.gz 的 SHA256 声明，作下载完整性锚点）。 */
    fun releaseUrl(suite: String): String = "$url/dists/$suite/Release"

    fun packagesIndexUrl(suite: String, debArch: String): String =
        "$url/dists/$suite/main/binary-$debArch/Packages.gz"
}

object DebianSources {
    /** 我们的 base 层构建所用的 Debian 套件（runtime-bundle/build_base.sh 默认值）。 */
    const val SUITE = "trixie"

    /** APK ABI → Debian 架构名（模拟器 x86_64 对应 amd64）。 */
    fun debArch(supportedAbi: String?): String = when (supportedAbi) {
        "x86_64" -> "amd64"
        else -> "arm64"
    }

    /** APK ABI → 架构三元组（层内 `usr/lib/<triple>/` 路径与 dpkg 库中的架构字样用）。 */
    fun debTriple(supportedAbi: String?): String = when (supportedAbi) {
        "x86_64" -> "x86_64-linux-gnu"
        else -> "aarch64-linux-gnu"
    }

    /**
     * 探测与安装的源清单（4 源，二创精简版）。
     * 仅保留 Debian 官方 + 腾讯云 / 华为云 / 中科大镜像。
     */
    val ALL: List<DebianArchiveSource> = listOf(
        DebianArchiveSource(
            name = UiText.Res(R.string.deb_source_official_name),
            url = "https://deb.debian.org/debian",
            note = UiText.Res(R.string.deb_source_official_note),
        ),
        DebianArchiveSource(
            name = UiText.Res(R.string.deb_source_tencent_name),
            url = "https://mirrors.cloud.tencent.com/debian",
            note = UiText.Res(R.string.deb_source_tencent_note),
        ),
        DebianArchiveSource(
            name = UiText.Res(R.string.deb_source_huawei_name),
            url = "https://repo.huaweicloud.com/debian",
            note = UiText.Res(R.string.deb_source_huawei_note),
        ),
        DebianArchiveSource(
            name = UiText.Res(R.string.deb_source_ustc_name),
            url = "https://mirrors.ustc.edu.cn/debian",
            note = UiText.Res(R.string.deb_source_ustc_note),
        ),
    )
}
