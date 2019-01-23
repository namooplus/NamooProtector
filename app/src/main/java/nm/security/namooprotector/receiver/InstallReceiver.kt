package nm.security.namooprotector.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import nm.security.namooprotector.R
import nm.security.namooprotector.activity.AppAddActivity
import nm.security.namooprotector.util.ConvertUtil
import nm.security.namooprotector.util.DataUtil
import nm.security.namooprotector.util.DataUtil.APPS
import nm.security.namooprotector.util.DataUtil.SETTING
import java.util.*

class InstallReceiver : BroadcastReceiver()
{
    override fun onReceive(context: Context, intent: Intent)
    {
        val packageName = intent.data.schemeSpecificPart

        if (!DataUtil.getBoolean("protectionNotification", SETTING) || DataUtil.getBoolean(packageName, APPS))
            return

        //패키지 전달
        val appAddIntent = Intent(context, AppAddActivity::class.java)
        appAddIntent.putExtra("packageName", packageName)

        //알림
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, "appadd")
            .setSmallIcon(R.drawable.icon_protection_notification)
            .setContentTitle(context.getString(R.string.notification_protection_title))
            .setContentText(String.format(context.getString(R.string.notification_protection_message), ConvertUtil.packageNameToAppName(packageName)))
            .setContentIntent(PendingIntent.getActivity(context, 0, appAddIntent, PendingIntent.FLAG_UPDATE_CURRENT))
            .setOngoing(false)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        if (Build.VERSION.SDK_INT >= 26)
        {
            val channel = NotificationChannel("appadd", context.getString(R.string.notification_protection_title), NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = context.getString(R.string.notification_protection_description)

            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify((((Date().time / 1000L) % Integer.MAX_VALUE).toInt()), notification.build())
    }
}
