package com.tencent.shadow.loader.blocs

import android.content.Context
import com.tencent.hydevteam.pluginframework.installedplugin.InstalledPlugin
import com.tencent.shadow.loader.classloaders.PluginClassLoader
import com.tencent.shadow.loader.exceptions.LoadApkException
import java.io.File

/**
 * 加载插件到ClassLoader中
 *
 * @author cubershi
 */
object LoadApkBloc {
    /**
     * 加载插件到ClassLoader中.
     *
     * @param installedPlugin    已安装（PluginManager已经下载解包）的插件
     * @return 加载了插件的ClassLoader
     */
    @Throws(LoadApkException::class)
    fun loadPlugin(hostAppContext: Context, installedPlugin: InstalledPlugin, soDir: File): PluginClassLoader {
        val pluginLoaderClassLoader = LoadApkBloc::class.java.classLoader
        val hostAppClassLoader = pluginLoaderClassLoader.parent
        val apk = installedPlugin.pluginFile
        val odexDir = File(apk.parent, apk.name + "_odex_${installedPlugin.pluginVersionForPluginLoaderManage}")
        prepareDirs(odexDir, soDir)
        return PluginClassLoader(
                hostAppContext,
                apk.absolutePath,
                odexDir.absolutePath,
                soDir.absolutePath,
                hostAppClassLoader
        )
    }

    @Throws(LoadApkException::class)
    private fun prepareDirs(odexDir: File, libDir: File) {
        if (odexDir.exists() && !odexDir.isDirectory) {
            throw LoadApkException("odexDir目标路径" + odexDir.absolutePath
                    + "已被其他文件占用")
        } else if (!odexDir.exists()) {
            val success = odexDir.mkdir()
            if (!success) {
                throw LoadApkException("odexDir目标路径" + odexDir.absolutePath
                        + "创建目录失败")
            }
        }

        if (!libDir.exists()) {
            if (!libDir.mkdirs()) {
                throw LoadApkException("libDir目标路径" + libDir.absolutePath
                        + "创建目录失败")
            }
        }
    }
}