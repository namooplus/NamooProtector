package nm.security.namooprotector.util

import android.app.Activity
import android.graphics.Color
import kotlinx.android.synthetic.main.lockscreen.*
import kotlinx.android.synthetic.main.lockscreen_bottom_default.*
import kotlinx.android.synthetic.main.lockscreen_top_default.*

object ThemeUtil
{
    fun apply(activity: Activity)
    {
        //테마
        activity.lockscreen_base_layout.setBackgroundColor(getColorInt("baseLayoutColor", "#ff52cc9d"))
        activity.lockscreen_bottom_layout.setBackgroundColor(getColorInt("bottomLayoutColor", "#20000000"))
        activity.lockscreen_pin_indicator.setTextColor(getColorInt("pinIndicatorColor", "#ffffffff"))
        activity.lockscreen_pin_one.setTextColor(getColorInt("keyColor", "#ffffffff"))
        activity.lockscreen_pin_two.setTextColor(getColorInt("keyColor", "#ffffffff"))
        activity.lockscreen_pin_three.setTextColor(getColorInt("keyColor", "#ffffffff"))
        activity.lockscreen_pin_four.setTextColor(getColorInt("keyColor", "#ffffffff"))
        activity.lockscreen_pin_five.setTextColor(getColorInt("keyColor", "#ffffffff"))
        activity.lockscreen_pin_six.setTextColor(getColorInt("keyColor", "#ffffffff"))
        activity.lockscreen_pin_seven.setTextColor(getColorInt("keyColor", "#ffffffff"))
        activity.lockscreen_pin_eight.setTextColor(getColorInt("keyColor", "#ffffffff"))
        activity.lockscreen_pin_nine.setTextColor(getColorInt("keyColor", "#ffffffff"))
        activity.lockscreen_pin_check.setTextColor(getColorInt("keyColor", "#ffffffff"))
        activity.lockscreen_pin_zero.setTextColor(getColorInt("keyColor", "#ffffffff"))
        activity.lockscreen_pin_clear.setTextColor(getColorInt("keyColor", "#ffffffff"))
        activity.lockscreen_pattern_view.setPatternDotColor(getColorString("keyColor", "#ffffffff"))
    }

    //외부메소드
    fun getColorInt(name: String, defaultColor: String): Int
    {
        val color = DataUtil.getString(name, DataUtil.THEME, defaultColor)

        return Color.parseColor(if (color.startsWith("#") && color.length == 9) color else defaultColor)
    }
    fun getColorString(name: String, defaultColor: String): String
    {
        val color = DataUtil.getString(name, DataUtil.THEME, defaultColor)

        return if (color.startsWith("#") && color.length == 9) color else defaultColor
    }
}