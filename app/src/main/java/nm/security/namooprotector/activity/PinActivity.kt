package nm.security.namooprotector.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import kotlinx.android.synthetic.main.activity_pin.*
import nm.security.namooprotector.R
import nm.security.namooprotector.util.DataUtil
import nm.security.namooprotector.util.DataUtil.LOCK
import nm.security.namooprotector.util.PasswordUtil

class PinActivity: AppCompatActivity()
{
    //라이프사이클
    override fun onCreate(savedInstanceState: Bundle?)
    {
        overridePendingTransition(R.anim.activity_scale_plus_to_zero, R.anim.activity_scale_zero_to_minus)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin)

        initFlag()
        initListener()
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
    private fun initListener()
    {
        pin_input_first.addTextChangedListener(object : TextWatcher
        {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int)
            {
                checkValid()
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int)
            {

            }
            override fun afterTextChanged(s: Editable)
            {

            }
        })

        pin_input_second.addTextChangedListener(object : TextWatcher
        {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int)
            {
                checkValid()
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int)
            {

            }
            override fun afterTextChanged(s: Editable)
            {

            }
        })
    }

    //클릭 이벤트
    fun execute(view: View)
    {
        DataUtil.put("type", LOCK, "pin")
        DataUtil.put("pin", LOCK, pin_input_first.text.toString())

        finish()
    }

    //메소드
    fun checkValid()
    {
        if (PasswordUtil.isValidForPin(pin_input_first.text.toString()) && pin_input_first.text.toString() == pin_input_second.text.toString())
            pin_execute_button.visibility = View.VISIBLE

        else
            pin_execute_button.visibility = View.INVISIBLE
    }
}