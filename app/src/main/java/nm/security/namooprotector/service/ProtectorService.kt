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
import android.os.Handler
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*
import nm.security.namooprotector.R
import nm.security.namooprotector.activity.LockScreen
import nm.security.namooprotector.util.CheckUtil
import nm.security.namooprotector.util.DataUtil
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
                .setContentTitle(getString(R.string.notification_service_title))
                .setContentText(getString(R.string.notification_service_message))
                .setColor(ContextCompat.getColor(this, R.color.color_accent))
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_MIN)

            if (Build.VERSION.SDK_INT >= 26)
            {
                val channel = NotificationChannel("namooprotector", getString(R.string.notification_service_title), NotificationManager.IMPORTANCE_MIN)
                channel.description = getString(R.string.notification_service_description)

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

        //앱 변화 감지
        onAppChanged {
            //나무프로텍터 잠금 방지
            if (it == packageName) return@onAppChanged

            //잠금
            if (DataUtil.getBoolean(it, DataUtil.APPS) && !ProtectorServiceHelper.isAuthorized(it)) lock(it)
            //갱신
            else if (ProtectorServiceHelper.isAuthorized(it)) ProtectorServiceHelper.addAuthorizedApp(it, SettingsUtil.lockDelay.toLong())

            //임시 잠금해제 인증앱 초기화
            ProtectorServiceHelper.cleanTemporaryAuthorizedApps()
        }
    }

    //메소드
    private fun onAppChanged(invoker: ((packageName: String) -> Unit))
    {
        //검색
        var previousEventApp = ""
        var recentEventApp = ""
        var recentEventTime: Long = 0

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
                        recentEventApp = scannedEventApp
                        recentEventTime = scannedEventTime
                    }
                }

                //잠금 해제 인증앱 업데이트
                ProtectorServiceHelper.updateAuthorizedApps()

                //앱 변화 감지
                if (previousEventApp != recentEventApp)
                {
                    Handler(Looper.getMainLooper()).post(Runnable { invoker.invoke(recentEventApp) })

                    previousEventApp = recentEventApp
                }
            }
        })
        searchTask!!.start()
    }

    private fun lock(packageName: String)
    {
        val lockScreen = Intent(this, LockScreen::class.java)
        lockScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        lockScreen.putExtra("packageName", packageName)
        startActivity(lockScreen)
    }
}
