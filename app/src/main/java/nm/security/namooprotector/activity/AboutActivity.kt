package nm.security.namooprotector.activity

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import de.psdev.licensesdialog.LicensesDialog
import kotlinx.android.synthetic.main.activity_about.*
import nm.security.namooprotector.R
import nm.security.namooprotector.util.ActivityUtil
import nm.security.namooprotector.util.ResourceUtil


class AboutActivity: AppCompatActivity()
{
    //라이프사이클
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        ActivityUtil.initFlag(this, true)
        ActivityUtil.initPreviousTitle(this)

        initUI()
    }

    //설정
    private fun initUI()
    {
        about_app_version_indicator.text =  packageManager.getPackageInfo(packageName, 0).versionName ?: ResourceUtil.getString(R.string.error_unknown)
    }

    //클릭 이벤트
    fun github(view: View)
    {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/namooplus")))
    }
    fun tistory(view: View)
    {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://namooplus.tistory.com")))
    }
    fun license(view: View)
    {
        LicensesDialog.Builder(this)
            .setNotices(R.raw.license)
            .build()
            .show()
    }

    //메소드
}