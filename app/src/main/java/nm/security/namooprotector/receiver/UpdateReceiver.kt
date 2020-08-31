package nm.security.namooprotector.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import nm.security.namooprotector.R
import nm.security.namooprotector.util.CheckUtil
import nm.security.namooprotector.util.ResourceUtil
import nm.security.namooprotector.util.ServiceUtil

class UpdateReceiver : BroadcastReceiver()
{
    override fun onReceive(context: Context, intent: Intent)
    {
        if (CheckUtil.isNPValid && CheckUtil.isServiceRunning)
            ServiceUtil.runService(true)

        Toast.makeText(context, ResourceUtil.getString(R.string.alert_np_updated), Toast.LENGTH_LONG).show()
    }
}
