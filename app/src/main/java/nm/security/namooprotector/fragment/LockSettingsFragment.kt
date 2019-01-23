package nm.security.namooprotector.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import co.infinum.goldfinger.Goldfinger
import kotlinx.android.synthetic.main.fragment_lock_settings.*
import nm.security.namooprotector.R
import nm.security.namooprotector.activity.PatternActivity
import nm.security.namooprotector.activity.PinActivity
import nm.security.namooprotector.util.DataUtil
import nm.security.namooprotector.util.FingerprintUtil
import nm.security.namooprotector.util.PasswordUtil

class LockSettingsFragment : Fragment()
{
    //라이프사이클
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.fragment_lock_settings, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        initClick()
    }
    override fun onResume()
    {
        super.onResume()

        initState()
    }

    //설정
    private fun initClick()
    {
        //방식
        lock_settings_type_pin_button.setOnClickListener { startActivity(Intent(context, PinActivity::class.java)) }
        lock_settings_type_pattern_button.setOnClickListener { startActivity(Intent(context, PatternActivity::class.java)) }
        lock_settings_type_fingerprint_button.setOnClickListener {
            if (FingerprintUtil.isFingerprintActivated)
                DataUtil.put("fingerprint", DataUtil.LOCK, false)

            else
            {
                when (val message = FingerprintUtil.isSupportFingerprint(Goldfinger.Builder(context).build()))
                {
                    FingerprintUtil.AVAILABLE -> DataUtil.put("fingerprint", DataUtil.LOCK, true)
                    else -> generateFingerprintErrorMessage(message)
                }
            }

            initState()
        }

        //PIN 설정
        lock_settings_pin_settings_click_haptic_button.setOnClickListener { changeState("clickHaptic") }
        lock_settings_pin_settings_hide_click_button.setOnClickListener { changeState("hideClick") }
        lock_settings_pin_settings_quick_unlock_button.setOnClickListener { changeState("quickUnlock") }
        lock_settings_pin_settings_rearrange_key_button.setOnClickListener { changeState("rearrangeKey") }
        lock_settings_pin_settings_light_key_button.setOnClickListener { changeState("lightKey") }

        //패턴 설정
        lock_settings_pattern_settings_draw_haptic_button.setOnClickListener { changeState("drawHaptic") }
        lock_settings_pattern_settings_hide_draw_button.setOnClickListener { changeState("hideDraw") }
        lock_settings_pattern_settings_light_dot_button.setOnClickListener { changeState("lightDot") }
    }
    private fun initState()
    {
        //방식
        when (PasswordUtil.type)
        {
            "pin" ->
            {
                lock_settings_type_pin_button.setChecked(true)
                lock_settings_type_pattern_button.setChecked(false)
            }
            "pattern" ->
            {
                lock_settings_type_pin_button.setChecked(false)
                lock_settings_type_pattern_button.setChecked(true)
            }
        }
        lock_settings_type_fingerprint_button.setChecked(FingerprintUtil.isFingerprintActivated)

        //PIN 설정
        lock_settings_pin_settings_click_haptic_button.setChecked(DataUtil.getBoolean("clickHaptic", DataUtil.SETTING))
        lock_settings_pin_settings_hide_click_button.setChecked(DataUtil.getBoolean("hideClick", DataUtil.SETTING))
        lock_settings_pin_settings_quick_unlock_button.setChecked(DataUtil.getBoolean("quickUnlock", DataUtil.SETTING))
        lock_settings_pin_settings_rearrange_key_button.setChecked(DataUtil.getBoolean("rearrangeKey", DataUtil.SETTING))
        lock_settings_pin_settings_light_key_button.setChecked(DataUtil.getBoolean("lightKey", DataUtil.SETTING))

        //패턴 설정
        lock_settings_pattern_settings_draw_haptic_button.setChecked(DataUtil.getBoolean("drawHaptic", DataUtil.SETTING))
        lock_settings_pattern_settings_hide_draw_button.setChecked(DataUtil.getBoolean("hideDraw", DataUtil.SETTING))
        lock_settings_pattern_settings_light_dot_button.setChecked(DataUtil.getBoolean("lightDot", DataUtil.SETTING))
    }

    //메소드
    private fun changeState(name: String)
    {
        DataUtil.put(name, DataUtil.SETTING, !DataUtil.getBoolean(name, DataUtil.SETTING))

        initState()
    }
    private fun generateFingerprintErrorMessage(errorType: Int)
    {
        val errorMessage = when (errorType)
        {
            FingerprintUtil.NO_HARDWARE -> getString(R.string.error_fingerprint_no_hardware)
            FingerprintUtil.NO_ENROLLED_FINGERPRINT -> getString(R.string.error_fingerprint_not_enrolled)
            FingerprintUtil.NO_PASSWORD -> getString(R.string.error_fingerprint_no_password)
            else -> getString(R.string.error_standard)
        }

        val dialog = AlertDialog.Builder(context)

        with(dialog)
        {
            setTitle(getString(R.string.alert_fingerprint_error_title))
            setMessage(errorMessage)
            setPositiveButton(getString(R.string.common_ok), null)
            show()
        }
    }
}