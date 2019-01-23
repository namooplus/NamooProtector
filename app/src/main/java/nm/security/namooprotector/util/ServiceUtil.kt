package nm.security.namooprotector.util

import android.content.Intent
import android.os.Build
import nm.security.namooprotector.NamooProtector.Companion.context
import nm.security.namooprotector.service.ProtectorService

object ServiceUtil
{
    fun runService(activate: Boolean)
    {
        if (activate)
        {
            if (Build.VERSION.SDK_INT >= 26) context.startForegroundService(Intent(context, ProtectorService::class.java))
            else context.startService(Intent(context, ProtectorService::class.java))
        }
        else
            context.stopService(Intent(context, ProtectorService::class.java))
    }
}