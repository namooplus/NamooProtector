package nm.security.namooprotector.fragment

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import de.psdev.licensesdialog.LicensesDialog
import kotlinx.android.synthetic.main.fragment_about.*
import nm.security.namooprotector.service.ProtectorState
import nm.security.namooprotector.util.ServiceUtil
import android.content.Intent
import android.net.Uri
import nm.security.namooprotector.R
import nm.security.namooprotector.util.CheckUtil

class AboutFragment : Fragment()
{
    //라이프사이클
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(nm.security.namooprotector.R.layout.fragment_about, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        initClick()
        initState()
    }

    //설정
    private fun initClick()
    {
        //더보기
        about_more_license_button.setOnClickListener {
            LicensesDialog.Builder(context)
                .setNotices(nm.security.namooprotector.R.raw.license)
                .build()
                .show()
        }
        about_more_review_button.setOnClickListener {
            if (!CheckUtil.isNetworkAvailable)
                Toast.makeText(context, getString(R.string.error_network_disconnected), Toast.LENGTH_SHORT).show()

            else
            {
                Toast.makeText(context, getString(R.string.etc_rate), Toast.LENGTH_LONG).show()

                try
                {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${activity!!.packageName}")))
                }
                catch (e: ActivityNotFoundException)
                {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${activity!!.packageName}")))
                }
            }
        }
        about_more_deactivate_button.setOnClickListener {
            val dialog = AlertDialog.Builder(context)

            with(dialog)
            {
                setTitle(getString(R.string.alert_deactivation_title))
                setMessage(getString(R.string.alert_deactivation_message))
                setPositiveButton(getString(R.string.alert_deactivation_positive))
                { _, _ ->
                    //서비스 종료
                    ServiceUtil.runService(false)
                    ProtectorState.currentApp = ""
                    ProtectorState.currentState = ProtectorState.UNLOCKED

                    //앱 종료
                    Toast.makeText(context, getString(R.string.success_deactivation), Toast.LENGTH_SHORT).show()
                    activity!!.finish()
                }
                setNegativeButton(getString(R.string.common_cancel), null)
                show()
            }
        }
    }
    private fun initState()
    {
        about_app_version_viewer.text = context!!.packageManager.getPackageInfo(activity!!.packageName, 0).versionName
    }
}