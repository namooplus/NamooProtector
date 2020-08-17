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
import nm.security.namooprotector.activity.AddAppActivity
import nm.security.namooprotector.util.ConvertUtil
import nm.security.namooprotector.util.DataUtil
import nm.security.namooprotector.util.DataUtil.APPS
import nm.security.namooprotector.util.SettingsUtil
import java.util.*

class InstallReceiver : BroadcastReceiver()
{
    override fun onReceive(context: Context, intent: Intent)
    {
        val packageName = intent.data!!.schemeSpecificPart

        if (!SettingsUtil.protectionNotification || DataUtil.getBoolean(packageName, APPS))
            return

        //패키지 전달
        val addAppIntent = Intent(context, AddAppActivity::class.java)
        addAppIntent.putExtra("packageName", packageName)

        //알림
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, "namooprotector_appadd")
            .setSmallIcon(R.drawable.icon_np)
            .setContentTitle(context.getString(R.string.notification_protection_title))
            .setContentText(String.format(context.getString(R.string.notification_protection_message), ConvertUtil.packageNameToAppName(packageName)))
            .setContentIntent(PendingIntent.getActivity(context, 0, addAppIntent, PendingIntent.FLAG_UPDATE_CURRENT))
            .setOngoing(false)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        if (Build.VERSION.SDK_INT >= 26)
        {
            val channel = NotificationChannel("namooprotector_appadd", context.getString(R.string.notification_protection_title), NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = context.getString(R.string.notification_protection_description)

            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify((((Date().time / 1000L) % Integer.MAX_VALUE).toInt()), notification.build())
    }
}
