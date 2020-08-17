package nm.security.namooprotector.activity

import android.content.Context
import android.os.Bundle
import nm.security.namooprotector.util.*
import androidx.appcompat.app.AppCompatActivity
import nm.security.namooprotector.R

class RestoreActivity: AppCompatActivity()
{
    //라이프사이클
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restore)

        ActivityUtil.initFlag(this, true)
        ActivityUtil.initPreviousTitle(this)
    }

    //설정

    //클릭 이벤트

    //메소드
}