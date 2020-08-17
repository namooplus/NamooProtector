package nm.security.namooprotector.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import nm.security.namooprotector.util.*
import androidx.appcompat.app.AppCompatActivity
import nm.security.namooprotector.R

class DeveloperActivity: AppCompatActivity()
{
    //라이프사이클
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_developer)

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

    //메소드
}