package nm.security.namooprotector.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_pattern.*
import nm.security.namooprotector.R
import nm.security.namooprotector.util.*
import nm.security.namooprotector.util.DataUtil.LOCK
import nm.security.namooprotector.widget.PatternView

class PatternActivity: AppCompatActivity()
{
    var mode = 1
    var savedPattern = ""

    //라이프사이클
    override fun onCreate(savedInstanceState: Bundle?)
    {
        overridePendingTransition(R.anim.activity_scale_plus_to_zero, R.anim.activity_scale_zero_to_minus)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pattern)

        initFlag()
        initPatternView()
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
    private fun initPatternView()
    {
        pattern_pattern_view.isTactileFeedbackEnabled = false
        pattern_pattern_view.isInStealthMode = false
        pattern_pattern_view.setOnPatternListener(object : PatternView.OnPatternListener()
        {
            override fun onPatternStart()
            {
                pattern_execute_button.visibility = View.INVISIBLE

                if (mode == 1)
                {
                    pattern_state_text.text = getString(R.string.alert_pattern_new_draw)
                    pattern_execute_button.setImageResource(R.drawable.vector_next)
                }
                else if (mode == 2)
                {
                    pattern_state_text.text = getString(R.string.alert_pattern_confirm_draw)
                    pattern_execute_button.setImageResource(R.drawable.vector_check)
                }
            }
            override fun onPatternCleared()
            {

            }
            override fun onPatternCellAdded(pattern: List<PatternView.Cell>, SimplePattern: String)
            {

            }
            override fun onPatternDetected(pattern: List<PatternView.Cell>, result: String)
            {
                if (mode == 1)
                {
                    if (PasswordUtil.isValidForPattern(result))
                    {
                        savedPattern = result
                        pattern_execute_button.visibility = View.VISIBLE
                    }
                    else
                        pattern_state_text.text = getString(R.string.error_pattern_invalid)

                }
                else if (mode == 2)
                {
                    if (result != savedPattern)
                    {
                        mode = 1
                        savedPattern = ""

                        pattern_state_text.text = getString(R.string.error_pattern_incorrect)
                        pattern_execute_button.visibility = View.INVISIBLE
                    }
                    else
                        pattern_execute_button.visibility = View.VISIBLE
                }
            }
        })
    }

    //클릭 이벤트
    fun execute(view: View)
    {
        if (mode == 1)
        {
            mode = 2

            pattern_state_text.text = getString(R.string.alert_pattern_confirm_draw)
            pattern_pattern_view.clearPattern()

            pattern_execute_button.visibility = View.INVISIBLE
        }
        else if (mode == 2)
        {
            DataUtil.put("type", LOCK, "pattern")
            DataUtil.put("pattern", LOCK, savedPattern)

            finish()
        }
    }
}