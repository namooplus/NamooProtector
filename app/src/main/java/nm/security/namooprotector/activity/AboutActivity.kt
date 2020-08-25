package nm.security.namooprotector.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import nm.security.namooprotector.util.*
import androidx.appcompat.app.AppCompatActivity
import de.psdev.licensesdialog.LicensesDialog
import nm.security.namooprotector.R

class AboutActivity: AppCompatActivity()
{
    //라이프사이클
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        ActivityUtil.initFlag(this, true)
        ActivityUtil.initPreviousTitle(this)
    }

    //설정

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