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
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*
import nm.security.namooprotector.NamooProtector.Companion.context
import nm.security.namooprotector.R
import nm.security.namooprotector.activity.LockScreen
import nm.security.namooprotector.util.CheckUtil
import nm.security.namooprotector.util.DataUtil
import nm.security.namooprotector.util.ResourceUtil
import nm.security.namooprotector.util.SettingsUtil

class ProtectorService : Service()
{
    private val usageStatsManager by lazy { getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager }

    private var searchTask: Thread? = null
    private var search = false

    //라이프사이클
    override fun onBind(arg0: Intent): IBinder?
    {
        return null
    }
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int
    {
        initForeground(true)
        initProtector()

        return START_STICKY
    }
    override fun onDestroy()
    {
        CheckUtil.isServiceRunning = false

        initForeground(false)

        super.onDestroy()
    }

    //설정
    private fun initForeground(on: Boolean)
    {
        if (on)
        {
            //포그라운드 설정
            val notification = NotificationCompat.Builder(this, "namooprotector")
                .setSmallIcon(R.drawable.icon_np)
                .setContentTitle(ResourceUtil.getString(R.string.app_name))
                .setContentText(ResourceUtil.getString(R.string.alert_service_on))
                .setColor(ContextCompat.getColor(this, R.color.color_accent))
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_MIN)

            if (Build.VERSION.SDK_INT >= 26)
            {
                val channel = NotificationChannel("namooprotector", ResourceUtil.getString(R.string.app_name), NotificationManager.IMPORTANCE_MIN)
                channel.description = ResourceUtil.getString(R.string.notification_service_description)

                (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
            }
            startForeground(1, notification.build())

            //검색 시작
            search = true
        }
        else
        {
            //검색 종료
            search = false

            //포그라운드 해제
            stopForeground(true)
        }
    }
    private fun initProtector()
    {
        //중복 방지
        if (searchTask != null)
            return

        //검색
        var previousEventApp = ""
        var recentEventApp = ""
        var recentEventTime = 0L
        var tempRecentEventApp = ""
        var tempRecentEventTime = 0L

        searchTask = Thread(Runnable {
            while (search)
            {
                //현재 포그라운드 앱 검색
                val currentTime = System.currentTimeMillis()
                val usageEvents = usageStatsManager.queryEvents(currentTime - 5 * 1000, currentTime)

                val scannedEvent = UsageEvents.Event()

                while (usageEvents.hasNextEvent())
                {
                    usageEvents.getNextEvent(scannedEvent)

                    val scannedEventType = scannedEvent.eventType
                    val scannedEventApp = scannedEvent.packageName
                    val scannedEventTime = scannedEvent.timeStamp

                    if (scannedEventType == UsageEvents.Event.ACTIVITY_RESUMED && scannedEventTime > recentEventTime)
                    {
                        tempRecentEventApp = scannedEventApp
                        tempRecentEventTime = scannedEventTime
                    }
                }

                if (tempRecentEventTime > recentEventTime)
                {
                    //짧은 중복 인식 -> 앱 변화로 간주
                    if (tempRecentEventApp == recentEventApp) previousEventApp = packageName

                    recentEventApp = tempRecentEventApp
                    recentEventTime = tempRecentEventTime
                }

                //앱 변화 감지
                if (recentEventApp != previousEventApp)
                {
                    //잠금 해제 인증앱 업데이트
                    ProtectorServiceHelper.updateAuthorizedApps()

                    //잠금 딜레이
                    if (ProtectorServiceHelper.isAuthorized(previousEventApp))
                    {
                        ProtectorServiceHelper.clearTemporaryAuthorizedApp()
                        ProtectorServiceHelper.addAuthorizedApp(previousEventApp, SettingsUtil.lockDelay.toLong())
                    }

                    //잠금
                    if (ProtectorServiceHelper.isAuthorized(recentEventApp))
                        ProtectorServiceHelper.addTemporaryAuthorizedApp(recentEventApp)

                    if (DataUtil.getBoolean(recentEventApp, DataUtil.APPS) && !ProtectorServiceHelper.isAuthorized(recentEventApp) && recentEventApp != packageName)
                        lock(recentEventApp)


                    previousEventApp = recentEventApp
                }
            }
        })
        searchTask!!.start()
    }

    //메소드
    private fun lock(packageName: String)
    {
        val lockScreen = Intent(context, LockScreen::class.java)
        lockScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
        lockScreen.putExtra("packageName", packageName)
        context.startActivity(lockScreen)
    }
}
