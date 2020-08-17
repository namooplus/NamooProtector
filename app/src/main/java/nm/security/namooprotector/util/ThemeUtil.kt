package nm.security.namooprotector.util

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.TypedValue
import android.view.View
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.lockscreen.*
import kotlinx.android.synthetic.main.lockscreen.view.*
import nm.security.namooprotector.R

object ThemeUtil
{
    //이전 버전 unit 차이 대응 : 2추가

    var baseLayoutColor: String
        set(value) = DataUtil.put("baseLayoutColor2", DataUtil.THEME, value)
        get() = DataUtil.getString("baseLayoutColor2", DataUtil.THEME).validColor("#FF52cc9d")

    var topLayoutColor: String
        set(value) = DataUtil.put("topLayoutColor2", DataUtil.THEME, value)
        get() = DataUtil.getString("topLayoutColor2", DataUtil.THEME).validColor("#00ffffff")

    var bottomLayoutColor: String
        set(value) = DataUtil.put("bottomLayoutColor2", DataUtil.THEME, value)
        get() = DataUtil.getString("bottomLayoutColor2", DataUtil.THEME).validColor("#20000000")

    var iconContainerSize: Float
        set(value) = DataUtil.put("iconContainerSize2", DataUtil.THEME, value)
        get() = DataUtil.getFloat("iconContainerSize2", DataUtil.THEME).validSize(90f)

    var iconContainerVisibility: Boolean
        set(value) = DataUtil.put("iconContainerVisibility2", DataUtil.THEME, value)
        get() = DataUtil.getBoolean("iconContainerVisibility2", DataUtil.THEME, true)

    var pinIndicatorTextSize: Float
        set(value) = DataUtil.put("pinIndicatorTextSize2", DataUtil.THEME, value)
        get() = DataUtil.getFloat("pinIndicatorTextSize2", DataUtil.THEME).validSize(30f)

    var pinIndicatorTextColor: String
        set(value) = DataUtil.put("pinIndicatorTextColor2", DataUtil.THEME, value)
        get() = DataUtil.getString("pinIndicatorTextColor2", DataUtil.THEME).validColor("#FFffffff")

    var pinIndicatorVisibility: Boolean
        set(value) = DataUtil.put("pinIndicatorVisibility2", DataUtil.THEME, value)
        get() = DataUtil.getBoolean("pinIndicatorVisibility2", DataUtil.THEME, true)

    var keyTextSize: Float
        set(value) = DataUtil.put("keyTextSize2", DataUtil.THEME, value)
        get() = DataUtil.getFloat("keyTextSize2", DataUtil.THEME).validSize(25f)

    var keyTextColor: String
        set(value) = DataUtil.put("keyTextColor2", DataUtil.THEME, value)
        get() = DataUtil.getString("keyTextColor2", DataUtil.THEME).validColor("#FFffffff")

    var patternColor: String
        set(value) = DataUtil.put("patternColor2", DataUtil.THEME, value)
        get() = DataUtil.getString("patternColor2", DataUtil.THEME).validColor("#FFffffff")

    //외부 메소드
    fun apply(activity: Activity)
    {
        activity.lockscreen_base_layout.setBackgroundColor(Color.parseColor(baseLayoutColor))
        activity.lockscreen_top_layout.setBackgroundColor(Color.parseColor(topLayoutColor))
        activity.lockscreen_bottom_layout.setBackgroundColor(Color.parseColor(bottomLayoutColor))

        activity.lockscreen_icon_container.layoutParams.width = ConvertUtil.dpToPx(iconContainerSize).toInt()
        activity.lockscreen_icon_container.layoutParams.height = ConvertUtil.dpToPx(iconContainerSize).toInt()
        activity.lockscreen_icon_container.setImageResource(if (iconContainerVisibility) R.drawable.background_circle_white_translucent else 0)

        activity.lockscreen_pin_indicator.setTextSize(TypedValue.COMPLEX_UNIT_SP, pinIndicatorTextSize)
        activity.lockscreen_pin_indicator.setTextColor(if (pinIndicatorVisibility) Color.parseColor(pinIndicatorTextColor) else 0x00ffffff)

        activity.lockscreen_key_1.setTextSize(TypedValue.COMPLEX_UNIT_SP, keyTextSize)
        activity.lockscreen_key_2.setTextSize(TypedValue.COMPLEX_UNIT_SP, keyTextSize)
        activity.lockscreen_key_3.setTextSize(TypedValue.COMPLEX_UNIT_SP, keyTextSize)
        activity.lockscreen_key_4.setTextSize(TypedValue.COMPLEX_UNIT_SP, keyTextSize)
        activity.lockscreen_key_5.setTextSize(TypedValue.COMPLEX_UNIT_SP, keyTextSize)
        activity.lockscreen_key_6.setTextSize(TypedValue.COMPLEX_UNIT_SP, keyTextSize)
        activity.lockscreen_key_7.setTextSize(TypedValue.COMPLEX_UNIT_SP, keyTextSize)
        activity.lockscreen_key_8.setTextSize(TypedValue.COMPLEX_UNIT_SP, keyTextSize)
        activity.lockscreen_key_9.setTextSize(TypedValue.COMPLEX_UNIT_SP, keyTextSize)
        activity.lockscreen_key_ok.setTextSize(TypedValue.COMPLEX_UNIT_SP, keyTextSize)
        activity.lockscreen_key_0.setTextSize(TypedValue.COMPLEX_UNIT_SP, keyTextSize)
        activity.lockscreen_key_clear.setTextSize(TypedValue.COMPLEX_UNIT_SP, keyTextSize)

        activity.lockscreen_key_1.setTextColor(Color.parseColor(keyTextColor))
        activity.lockscreen_key_2.setTextColor(Color.parseColor(keyTextColor))
        activity.lockscreen_key_3.setTextColor(Color.parseColor(keyTextColor))
        activity.lockscreen_key_4.setTextColor(Color.parseColor(keyTextColor))
        activity.lockscreen_key_5.setTextColor(Color.parseColor(keyTextColor))
        activity.lockscreen_key_6.setTextColor(Color.parseColor(keyTextColor))
        activity.lockscreen_key_7.setTextColor(Color.parseColor(keyTextColor))
        activity.lockscreen_key_8.setTextColor(Color.parseColor(keyTextColor))
        activity.lockscreen_key_9.setTextColor(Color.parseColor(keyTextColor))
        activity.lockscreen_key_ok.setTextColor(Color.parseColor(keyTextColor))
        activity.lockscreen_key_0.setTextColor(Color.parseColor(keyTextColor))
        activity.lockscreen_key_clear.setTextColor(Color.parseColor(keyTextColor))

        activity.lockscreen_pattern_view.normalStateColor = Color.parseColor(patternColor)
        activity.lockscreen_pattern_view.correctStateColor = Color.parseColor(patternColor)
        activity.lockscreen_pattern_view.wrongStateColor = Color.parseColor(patternColor)
        activity.lockscreen_pattern_view.invalidate()
    }
    fun apply(fragment: Fragment)
    {
        fragment.lockscreen_base_layout.setBackgroundColor(Color.parseColor(baseLayoutColor))
        fragment.lockscreen_top_layout.setBackgroundColor(Color.parseColor(topLayoutColor))
        fragment.lockscreen_bottom_layout.setBackgroundColor(Color.parseColor(bottomLayoutColor))

        fragment.lockscreen_icon_container.layoutParams.width = ConvertUtil.dpToPx(iconContainerSize).toInt()
        fragment.lockscreen_icon_container.layoutParams.height = ConvertUtil.dpToPx(iconContainerSize).toInt()
        fragment.lockscreen_icon_container.setImageResource(if (iconContainerVisibility) R.drawable.background_circle_white_translucent else 0)

        fragment.lockscreen_pin_indicator.setTextSize(TypedValue.COMPLEX_UNIT_SP, pinIndicatorTextSize)
        fragment.lockscreen_pin_indicator.setTextColor(if (pinIndicatorVisibility) Color.parseColor(pinIndicatorTextColor) else 0x00ffffff)

        fragment.lockscreen_key_1.setTextSize(TypedValue.COMPLEX_UNIT_SP, keyTextSize)
        fragment.lockscreen_key_2.setTextSize(TypedValue.COMPLEX_UNIT_SP, keyTextSize)
        fragment.lockscreen_key_3.setTextSize(TypedValue.COMPLEX_UNIT_SP, keyTextSize)
        fragment.lockscreen_key_4.setTextSize(TypedValue.COMPLEX_UNIT_SP, keyTextSize)
        fragment.lockscreen_key_5.setTextSize(TypedValue.COMPLEX_UNIT_SP, keyTextSize)
        fragment.lockscreen_key_6.setTextSize(TypedValue.COMPLEX_UNIT_SP, keyTextSize)
        fragment.lockscreen_key_7.setTextSize(TypedValue.COMPLEX_UNIT_SP, keyTextSize)
        fragment.lockscreen_key_8.setTextSize(TypedValue.COMPLEX_UNIT_SP, keyTextSize)
        fragment.lockscreen_key_9.setTextSize(TypedValue.COMPLEX_UNIT_SP, keyTextSize)
        fragment.lockscreen_key_ok.setTextSize(TypedValue.COMPLEX_UNIT_SP, keyTextSize)
        fragment.lockscreen_key_0.setTextSize(TypedValue.COMPLEX_UNIT_SP, keyTextSize)
        fragment.lockscreen_key_clear.setTextSize(TypedValue.COMPLEX_UNIT_SP, keyTextSize)

        fragment.lockscreen_key_1.setTextColor(Color.parseColor(keyTextColor))
        fragment.lockscreen_key_2.setTextColor(Color.parseColor(keyTextColor))
        fragment.lockscreen_key_3.setTextColor(Color.parseColor(keyTextColor))
        fragment.lockscreen_key_4.setTextColor(Color.parseColor(keyTextColor))
        fragment.lockscreen_key_5.setTextColor(Color.parseColor(keyTextColor))
        fragment.lockscreen_key_6.setTextColor(Color.parseColor(keyTextColor))
        fragment.lockscreen_key_7.setTextColor(Color.parseColor(keyTextColor))
        fragment.lockscreen_key_8.setTextColor(Color.parseColor(keyTextColor))
        fragment.lockscreen_key_9.setTextColor(Color.parseColor(keyTextColor))
        fragment.lockscreen_key_ok.setTextColor(Color.parseColor(keyTextColor))
        fragment.lockscreen_key_0.setTextColor(Color.parseColor(keyTextColor))
        fragment.lockscreen_key_clear.setTextColor(Color.parseColor(keyTextColor))

        fragment.lockscreen_pattern_view.normalStateColor = Color.parseColor(patternColor)
        fragment.lockscreen_pattern_view.correctStateColor = Color.parseColor(patternColor)
        fragment.lockscreen_pattern_view.wrongStateColor = Color.parseColor(patternColor)
        fragment.lockscreen_pattern_view.invalidate()
    }

    //내부 메소드
    private fun String.validColor(defaultColor: String): String
    {
        return if (this.startsWith("#") && this.length == 9) this else defaultColor
    }
    private fun Float.validSize(defaultSize: Float): Float
    {
        return if (this > 0f) this else defaultSize
    }
}