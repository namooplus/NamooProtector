package nm.security.namooprotector.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import nm.security.namooprotector.util.CheckUtil
import nm.security.namooprotector.util.ServiceUtil

class BootReceiver : BroadcastReceiver()
{
    override fun onReceive(context: Context, intent: Intent)
    {
        if (CheckUtil.isPasswordSet && CheckUtil.isUsageStatsPermissionGranted)
            ServiceUtil.runService(true)
    }
}