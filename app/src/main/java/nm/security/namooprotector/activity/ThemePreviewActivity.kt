package nm.security.namooprotector.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.lockscreen_bottom_default.*
import kotlinx.android.synthetic.main.lockscreen_top_default.*
import nm.security.namooprotector.R
import nm.security.namooprotector.util.PasswordUtil
import nm.security.namooprotector.util.ThemeUtil

class ThemePreviewActivity: AppCompatActivity()
{
    //라이프사이클
    override fun onCreate(savedInstanceState: Bundle?)
    {
        overridePendingTransition(R.anim.activity_scale_plus_to_zero, R.anim.activity_scale_zero_to_minus)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lockscreen)

        initFlag()
        initUI()
        initTheme()
    }
    override fun finish()
    {
        super.finish()

        overridePendingTransition(R.anim.activity_scale_minus_to_zero, R.anim.activity_scale_zero_to_plus)
    }

    //설정
    private fun initFlag()
    {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
    private fun initUI()
    {
        //기본 정보
        lockscreen_icon_indicator.setImageResource(R.drawable.icon_np)

        //잠금 방식
        when (PasswordUtil.type)
        {
            "pin" ->
            {
                lockscreen_pin_layout.visibility = View.VISIBLE
                lockscreen_pattern_layout.visibility = View.GONE
            }
            "pattern" ->
            {
                lockscreen_pin_layout.visibility = View.GONE
                lockscreen_pattern_layout.visibility = View.VISIBLE

                lockscreen_pattern_view.isTactileFeedbackEnabled = false
            }
        }
    }
    private fun initTheme()
    {
        ThemeUtil.apply(this)
    }

    //클릭 이벤트
    fun key(view: View)
    {

    }
}