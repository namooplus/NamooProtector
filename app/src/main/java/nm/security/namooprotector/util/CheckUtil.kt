package nm.security.namooprotector.util

import android.app.AppOpsManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import nm.security.namooprotector.NamooProtector.Companion.context

object CheckUtil
{
    val isPasswordSet: Boolean
        get() = PasswordUtil.type != ""

    val isUsageStatsPermissionGranted: Boolean
        get() = (context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager).checkOpNoThrow("android:get_usage_stats", android.os.Process.myUid(), context.packageName) == AppOpsManager.MODE_ALLOWED

    val isNetworkAvailable: Boolean
        get()
        {
            val activeNetwork: NetworkInfo? = (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo

            return activeNetwork?.isConnected ?: false
        }

    val isSupportFlash: Boolean
        get() = context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)

}