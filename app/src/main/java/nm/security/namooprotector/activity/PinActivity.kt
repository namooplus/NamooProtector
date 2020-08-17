package nm.security.namooprotector.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import kotlinx.android.synthetic.main.activity_pin.*
import nm.security.namooprotector.R
import nm.security.namooprotector.util.ActivityUtil
import nm.security.namooprotector.util.SettingsUtil

class PinActivity: AppCompatActivity()
{
    //라이프사이클
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin)

        ActivityUtil.initFlag(this, true)
        ActivityUtil.initPreviousTitle(this)

        initListener()
    }

    //설정
    private fun initListener()
    {
        pin_input_1.addTextChangedListener(object : TextWatcher
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
        pin_input_2.addTextChangedListener(object : TextWatcher
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
    fun ok(view: View)
    {
        SettingsUtil.lockType = "pin"
        SettingsUtil.pin = pin_input_1.text.toString()

        finishAfterTransition()
    }

    //메소드
    private fun checkValid()
    {
        val inputPin1 = pin_input_1.text.toString()
        val inputPin2 = pin_input_2.text.toString()

        if (inputPin1.isValid() && inputPin1 == inputPin2)
        {
            pin_ok_button.isEnabled = true
            pin_ok_button.visibility = View.VISIBLE
        }
        else
        {
            pin_ok_button.isEnabled = false
            pin_ok_button.visibility = View.GONE
        }
    }
    private fun String.isValid(): Boolean
    {
        return this.length in 4..12 && TextUtils.isDigitsOnly(this)
    }
}