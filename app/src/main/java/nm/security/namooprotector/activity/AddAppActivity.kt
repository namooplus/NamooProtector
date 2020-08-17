package nm.security.namooprotector.activity

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import nm.security.namooprotector.R
import nm.security.namooprotector.util.*
import nm.security.namooprotector.util.DataUtil.APPS

class AddAppActivity: Activity()
{
    //라이프사이클
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        val packageName = intent.getStringExtra("packageName")

        //추가
        if (packageName != null)
        {
            DataUtil.put(packageName, APPS, true)
            Toast.makeText(this, String.format(getString(R.string.success_add_app), ConvertUtil.packageNameToAppName(packageName)), Toast.LENGTH_SHORT).show()
        }

        //종료
        finish()
    }
}