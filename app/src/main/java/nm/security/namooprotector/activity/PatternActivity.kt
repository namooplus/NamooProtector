package nm.security.namooprotector.activity

import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.andrognito.patternlockview.PatternLockView
import com.andrognito.patternlockview.listener.PatternLockViewListener
import com.andrognito.patternlockview.utils.PatternLockUtils
import kotlinx.android.synthetic.main.activity_pattern.*
import nm.security.namooprotector.R
import nm.security.namooprotector.util.*

class PatternActivity: AppCompatActivity()
{
    var stage = 1
    var savedPattern = ""

    //라이프사이클
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pattern)

        ActivityUtil.initFlag(this, true)
        ActivityUtil.initPreviousTitle(this)

        initPatternView()
    }

    //설정
    private fun initPatternView()
    {
        pattern_pattern_view.isTactileFeedbackEnabled = false
        pattern_pattern_view.isInStealthMode = false
        pattern_pattern_view.addPatternLockListener(object: PatternLockViewListener {
            override fun onStarted()
            {
                pattern_ok_button.visibility = View.GONE

                pattern_state_indicator.text = when (stage)
                {
                    1 -> ResourceUtil.getString(R.string.alert_draw_new_pattern)
                    2 -> ResourceUtil.getString(R.string.alert_confirm_new_pattern)
                    else -> ResourceUtil.getString(R.string.error_invalid_access)
                }
            }
            override fun onCleared()
            {

            }
            override fun onProgress(progressPattern: MutableList<PatternLockView.Dot>?)
            {

            }
            override fun onComplete(pattern: MutableList<PatternLockView.Dot>?)
            {
                val result = ConvertUtil.patternToString(pattern_pattern_view, pattern)

                when (stage)
                {
                    1 ->
                    {
                        if (result.isValid())
                        {
                            savedPattern = result

                            pattern_ok_button.setText(ResourceUtil.getString(R.string.common_next))
                            pattern_ok_button.visibility = View.VISIBLE
                        }
                        else pattern_state_indicator.text = ResourceUtil.getString(R.string.error_pattern_invalid)
                    }
                    2 ->
                    {
                        if (result == savedPattern)
                        {
                            pattern_ok_button.setText(ResourceUtil.getString(R.string.common_ok))
                            pattern_ok_button.visibility = View.VISIBLE
                        }
                        else
                        {
                            stage = 1
                            savedPattern = ""

                            pattern_state_indicator.text = ResourceUtil.getString(R.string.error_pattern_incorrect)
                        }
                    }
                }
            }

        })
    }

    //클릭 이벤트
    fun ok(view: View)
    {
        when (stage)
        {
            1 ->
            {
                stage = 2

                pattern_state_indicator.text = ResourceUtil.getString(R.string.alert_confirm_new_pattern)
                pattern_pattern_view.clearPattern()
                pattern_ok_button.visibility = View.GONE
            }
            2 ->
            {
                SettingsUtil.lockType = "pattern"
                SettingsUtil.pattern = savedPattern

                finishAfterTransition()
            }
        }
    }

    //메소드
    private fun String.isValid(): Boolean
    {
        return this.length in 2..9 && TextUtils.isDigitsOnly(this)
    }
}