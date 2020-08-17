package nm.security.namooprotector

import android.app.Application
import android.content.Context
import com.google.android.gms.ads.MobileAds

class NamooProtector : Application()
{
    init
    {
        instance = this
    }
    companion object
    {
        private var instance: NamooProtector? = null

        val context : Context
            get() = instance!!.applicationContext
    }

//    override fun onCreate()
//    {
//        super.onCreate()
//
////        MobileAds.initialize(this, BuildConfig.ProjectAdKey)
//    }
}