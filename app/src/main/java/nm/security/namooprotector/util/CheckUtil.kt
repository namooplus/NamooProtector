package nm.security.namooprotector.util

import android.app.AppOpsManager
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.provider.Settings
import co.infinum.goldfinger.Goldfinger
import nm.security.namooprotector.NamooProtector.Companion.context

object CheckUtil
{
    //필수 설정 요소
    val isNPValid: Boolean
        get() = isPasswordSet && isUsageStatsPermissionGranted && isOverlayPermissionGranted

    val isPasswordSet: Boolean
        get() = SettingsUtil.lockType != null

    val isUsageStatsPermissionGranted: Boolean
        get() = (context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager).checkOpNoThrow("android:get_usage_stats", android.os.Process.myUid(), context.packageName) == AppOpsManager.MODE_ALLOWED

    val isOverlayPermissionGranted: Boolean
        get() = Settings.canDrawOverlays(context)

    //서비스
    var isServiceRunning: Boolean
        set(value) = DataUtil.put("activated", DataUtil.NP, value)
        get() = DataUtil.getBoolean("activated", DataUtil.NP)

    //광고
    var isAdRemoved: Boolean
        set(value) = DataUtil.put("removeAds", DataUtil.NP, value)
        get() = DataUtil.getBoolean("removeAds", DataUtil.NP)

    //지원
    fun isFingerprintAvailable(fingerprintManager: Goldfinger) =
        fingerprintManager.hasFingerprintHardware() && fingerprintManager.hasEnrolledFingerprint() &&  SettingsUtil.lockType != null

    val isFlashSupported: Boolean
        get() = context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)

    //기타
    val isNetworkAvailable: Boolean
        get()
        {
            val activeNetwork: NetworkInfo? = (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo

            return activeNetwork?.isConnected ?: false
        }
}