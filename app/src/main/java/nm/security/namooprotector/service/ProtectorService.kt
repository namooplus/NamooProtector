package nm.security.namooprotector.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.app.NotificationManager
import android.app.NotificationChannel
import android.os.Build
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.util.Log
import nm.security.namooprotector.R
import nm.security.namooprotector.activity.LockScreen
import nm.security.namooprotector.util.DataUtil
import nm.security.namooprotector.util.DataUtil.APPS

class ProtectorService : Service()
{
    private val usageStatsManager by lazy { getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager }

    private var thread: Thread? = null

    private var running = false

    //라이프사이클
    override fun onBind(arg0: Intent): IBinder?
    {
        return null
    }
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int
    {
        setForeground()
        startSearching()

        return Service.START_STICKY
    }
    override fun onDestroy()
    {
        running = false
        stopForeground(true)

        super.onDestroy()
    }

    //메소드
    private fun setForeground()
    {
        val notification = NotificationCompat.Builder(this, "namooprotector")
                .setSmallIcon(nm.security.namooprotector.R.drawable.icon_np_text)
                .setContentTitle(getString(R.string.notification_service_title))
                .setContentText(getString(R.string.notification_service_message))
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_MIN)

        if (Build.VERSION.SDK_INT >= 26)
        {
            val channel = NotificationChannel("namooprotector", getString(R.string.notification_service_title), NotificationManager.IMPORTANCE_MIN)
            channel.description = getString(R.string.notification_service_description)

            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
        }
        startForeground(1, notification.build())
    }
    private fun startSearching()
    {
        running = true

        if (thread == null) //중복쓰레드 방지
        {
            thread = Thread(Runnable
            {
                while (running)
                {
                    val (openedApp, openedClass) = getCurrentApp()

                    if (openedApp == "" || openedClass == "nm.security.namooprotector.activity.LockScreen") //잠금화면 상태 또는 잘못된 접근
                        continue

                    if (DataUtil.getBoolean(openedApp, APPS) || openedApp == packageName) //잠금 앱
                    {
                        when (ProtectorState.currentState)
                        {
                            //잠금해제된 경우
                            ProtectorState.UNLOCKED -> if (openedApp != ProtectorState.currentApp) lock(openedApp)

                            //잠금화면 상태에서 다시 같은 앱이 실행되거나 다른 잠금 앱이 실행된 경우
                            ProtectorState.LOCKED -> lock(openedApp)
                        }
                    }
                    else //미 잠금 앱
                    {
                        if (openedApp != ProtectorState.currentApp) unlock()
                    }

                    if (openedApp != ProtectorState.currentApp) ProtectorState.currentApp = openedApp
                }
            })
            thread!!.start()
        }
    }
    private fun getCurrentApp(): Pair<String, String>
    {
        val currentTime = System.currentTimeMillis()

        val usageEvent = usageStatsManager.queryEvents(currentTime - 1000 * 5, currentTime)
        val event = UsageEvents.Event()

        while (usageEvent.hasNextEvent())
            usageEvent.getNextEvent(event)

        return if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) Pair(event.packageName, event.className) else Pair("", "")
    }
    private fun lock(appToLock: String)
    {
        val intent = Intent(this, LockScreen::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("packageName", appToLock)
        startActivity(intent)
    }
    private fun unlock()
    {
        ProtectorState.currentState = ProtectorState.UNLOCKED

        LocalBroadcastManager.getInstance(this).sendBroadcast(Intent("CLOSE"))
    }
}
