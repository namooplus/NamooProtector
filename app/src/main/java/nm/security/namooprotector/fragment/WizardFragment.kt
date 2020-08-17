package nm.security.namooprotector.fragment

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_wizard.*
import nm.security.namooprotector.R
import nm.security.namooprotector.activity.MainActivity
import nm.security.namooprotector.activity.PatternActivity
import nm.security.namooprotector.activity.PinActivity
import nm.security.namooprotector.util.ActivityUtil
import nm.security.namooprotector.util.CheckUtil

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

    //클릭 이벤트
    private fun initClick()
    {
        wizard_essential_password_button.setOnClickListener {
            with(AlertDialog.Builder(context))
            {
                setTitle("잠금 방식 선택")
                setItems(arrayOf("PIN", "패턴")){ _, index: Int ->
                    when (index)
                    {
                        0 -> ActivityUtil.startActivityWithAnimation(activity!!, PinActivity::class.java)
                        1 -> ActivityUtil.startActivityWithAnimation(activity!!, PatternActivity::class.java)
                    }
                }
                show()
            }
        }
        wizard_essential_usage_stats_permission_button.setOnClickListener {
            try
            {
                startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS, Uri.parse("package:" + activity!!.packageName)))
            }
            catch (e: ActivityNotFoundException)
            {
                Toast.makeText(context, "나무프로텍터를 찾아 앱 사용정보 권한을 활성화해주세요.", Toast.LENGTH_LONG).show()
                startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            }
        }
        wizard_essential_overlay_permission_button.setOnClickListener {
            try
            {
                startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,  Uri.parse("package:" + activity!!.packageName)))
            }
            catch (e: ActivityNotFoundException)
            {
                Toast.makeText(context, "나무프로텍터를 찾아 오버레이 권한을 활성화해주세요.", Toast.LENGTH_LONG).show()
                startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION))
            }
        }

        wizard_start_button.setOnClickListener {
            (activity as MainActivity).home(view!!)
        }
    }

    //메소드
    private fun initState()
    {
        wizard_essential_password_button.isChecked = CheckUtil.isPasswordSet
        wizard_essential_usage_stats_permission_button.isChecked = CheckUtil.isUsageStatsPermissionGranted
        wizard_essential_overlay_permission_button.isChecked = CheckUtil.isOverlayPermissionGranted

        if (CheckUtil.isNPValid)
        {
            wizard_start_button.isEnabled = true
            wizard_start_button.visibility = View.VISIBLE
        }
        else
        {
            wizard_start_button.isEnabled = false
            wizard_start_button.visibility = View.GONE
        }
    }
}