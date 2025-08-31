package com.topdon.lib.core.utils

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File

object AppUtil {
    
    fun isAppInstalled(context: Context, packageName: String): Boolean {
        val packageManager = context.packageManager
        // 获取系统中安装的应用包的信息
        val listPackageInfo = packageManager.getInstalledPackages(0)
        return listPackageInfo.any { it.packageName.equals(packageName, ignoreCase = true) }
    }

    @Throws(PackageManager.NameNotFoundException::class)
    fun openApp(context: Context, packageName: String) {
        val pi = context.packageManager.getPackageInfo(packageName, 0)
        val resolveIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
            `package` = pi.packageName
        }
        
        val apps = context.packageManager.queryIntentActivities(resolveIntent, 0)
        if (apps.isNullOrEmpty()) {
            return
        }
        
        apps.firstOrNull()?.let { ri ->
            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
                component = ComponentName(ri.activityInfo.packageName, ri.activityInfo.name)
            }
            context.startActivity(intent)
        }
    }

    /**
     * 应用安装
     *
     * @param context
     * @param apkPath
     */
    fun installApp(context: Context, apkPath: File) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                // 不能再用setFlags了， setflags会重置之前的设置， 要么 setflags 多个|拼接，要么addflag
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                val contentUri = FileProvider.getUriForFile(
                    context, 
                    context.packageName + ".fileprovider", 
                    apkPath
                )
                setDataAndType(contentUri, "application/vnd.android.package-archive")
            } else {
                setDataAndType(Uri.fromFile(apkPath), "application/vnd.android.package-archive")
            }
        }
        context.startActivity(intent)
    }

    /**
     * 方法描述：判断某一Service是否正在运行
     * @param context     上下文
     * @param serviceName Service的全路径： 包名 + service的类名
     * @return true 表示正在运行，false 表示没有运行
     */
    fun isProcessRunning(context: Context, serviceName: String): Boolean {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningServiceInfos = am.getRunningServices(200)
        if (runningServiceInfos.isEmpty()) {
            return false
        }
        return runningServiceInfos.any { it.process == serviceName }
    }

    /**
     * 方法描述：判断某一Service是否正在运行
     * @param context     上下文
     * @param serviceName Service的全路径： 包名 + service的类名
     * @return true 表示正在运行，false 表示没有运行
     */
    fun isServiceRunning(context: Context, serviceName: String): Boolean {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningServiceInfos = am.getRunningServices(200)
        if (runningServiceInfos.isEmpty()) {
            return false
        }
        return runningServiceInfos.any { it.service.className == serviceName }
    }

    fun getVersionName(context: Context): String {
        return try {
            val packageManager = context.packageManager
            val packInfo = packageManager.getPackageInfo(context.packageName, 0)
            packInfo.versionName ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    fun getVersionCode(context: Context): Float {
        return try {
            val packageManager = context.packageManager
            val packInfo = packageManager.getPackageInfo(context.packageName, 0)
            packInfo.versionCode.toFloat()
        } catch (e: Exception) {
            0f
        }
    }
}