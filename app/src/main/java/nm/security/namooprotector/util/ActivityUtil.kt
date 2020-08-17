package nm.security.namooprotector.util

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.util.Pair
import android.view.View
import android.widget.TextView
import nm.security.namooprotector.R

object ActivityUtil
{
    fun initFlag(activity: Activity, darkStatusBar: Boolean)
    {
        activity.window.decorView.systemUiVisibility = if (darkStatusBar) View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR else View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }
    fun initPreviousTitle(activity: Activity)
    {
        activity.findViewById<TextView>(R.id.previous_title_indicator).text = activity.intent.getStringExtra("title")
    }

    fun startActivityWithAnimation(from: Activity, to: Class<out Activity>)
    {
        val intent = Intent(from, to)
            .putExtra("title", from.findViewById<TextView>(R.id.title_indicator).text.toString())
        val options = ActivityOptions.makeSceneTransitionAnimation(from,
            Pair.create(from.findViewById<TextView>(R.id.app_indicator) as View, "appIndicator"),
            Pair.create(from.findViewById<TextView>(R.id.title_indicator) as View, "titleIndicator"))
        from.startActivity(intent, options.toBundle())
    }
}