package nm.security.namooprotector.fragment

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import kotlinx.android.synthetic.main.fragment_wizard.*
import nm.security.namooprotector.R
import nm.security.namooprotector.activity.PatternActivity
import nm.security.namooprotector.activity.PinActivity
import nm.security.namooprotector.util.*

class WizardFragment : Fragment()
{
    //라이프사이클
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.fragment_wizard, container, false)
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
        //잠금방식 설정
        wizard_lock_type_pin_button.setOnClickListener { startActivity(Intent(activity, PinActivity::class.java)) }
        wizard_lock_type_pattern_button.setOnClickListener { startActivity(Intent(activity, PatternActivity::class.java)) }

        //권한 설정
        wizard_permission_usage_stats_permission_button.setOnClickListener { startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)) }
    }
    private fun initState()
    {
        //잠금방식 설정
        when (PasswordUtil.type)
        {
            "pin" ->
            {
                wizard_lock_type_pin_button.setChecked(true)
                wizard_lock_type_pattern_button.setChecked(false)
            }
            "pattern" ->
            {
                wizard_lock_type_pin_button.setChecked(false)
                wizard_lock_type_pattern_button.setChecked(true)
            }
            else ->
            {
                wizard_lock_type_pin_button.setChecked(false)
                wizard_lock_type_pattern_button.setChecked(false)
            }
        }

        //권한 설정
        wizard_permission_usage_stats_permission_button.setChecked(CheckUtil.isUsageStatsPermissionGranted)
    }
}