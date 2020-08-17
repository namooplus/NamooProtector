package nm.security.namooprotector.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import nm.security.namooprotector.util.CheckUtil
import nm.security.namooprotector.util.ServiceUtil

class UpdateReceiver : BroadcastReceiver()
{
    override fun onReceive(context: Context, intent: Intent)
    {
        if (CheckUtil.isNPValid && CheckUtil.isServiceRunning)
            ServiceUtil.runService(true)
    }
}
