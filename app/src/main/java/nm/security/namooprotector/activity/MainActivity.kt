package nm.security.namooprotector.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_main.*
import nm.security.namooprotector.R
import nm.security.namooprotector.fragment.*
import nm.security.namooprotector.service.ProtectorServiceHelper
import nm.security.namooprotector.util.ActivityUtil
import nm.security.namooprotector.util.AnimationUtil
import nm.security.namooprotector.util.AnimationUtil.alpha
import nm.security.namooprotector.util.AnimationUtil.scale
import nm.security.namooprotector.util.CheckUtil
import nm.security.namooprotector.util.ResourceUtil

class MainActivity: AppCompatActivity()
{
    //라이프사이클
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ActivityUtil.initFlag(this, true)

        initFragment()
    }
    override fun onResume()
    {
        super.onResume()

        initState()
        initAd()

        main_ad_view.resume()
    }
    override fun onPause()
    {
        main_ad_view.pause()

        super.onPause()
    }
    override fun onDestroy()
    {
        main_ad_view.destroy()

        ProtectorServiceHelper.clearTemporaryAuthorizedApp()

        super.onDestroy()
    }

    //설정
    private fun initFragment()
    {
        val title = ResourceUtil.getString(R.string.name_home)

        supportFragmentManager
            .beginTransaction()
            .add(R.id.main_content, HomeFragment(), title)
            .commit()

        changeTab(title, main_tab_home, main_tab_apps, main_tab_settings, main_tab_theme)
    }
    private fun initState()
    {
        if (!CheckUtil.isNPValid)
        {
            main_tab_container.visibility = View.GONE

            val title = ResourceUtil.getString(R.string.name_wizard)

            changeFragment(WizardFragment(), title)
            title_indicator.text = title
        }

        //잠금화면
        if (CheckUtil.isPasswordSet && !ProtectorServiceHelper.isAuthorized(packageName)) startActivity(Intent(this, LockScreen::class.java))
    }
    private fun initAd()
    {
        MobileAds.initialize(this) {}

        //광고 제거
        if (CheckUtil.isAdRemoved) main_ad_view.visibility = View.GONE
        //광고 표시
        else
        {
            //재로드 방지
            if (main_ad_view.visibility == View.VISIBLE) return

            main_ad_view.visibility = View.VISIBLE
            main_ad_view.adListener = object: AdListener()
            {
                override fun onAdFailedToLoad(errorCode: Int)
                {
                    main_ad_view.visibility = View.GONE
                }
            }
            main_ad_view.loadAd(AdRequest.Builder().build())
        }
    }

    //클릭 이벤트
    fun home(view: View)
    {
        val title = ResourceUtil.getString(R.string.name_home)

        changeFragment(HomeFragment(), title)
        changeTab(title, main_tab_home, main_tab_apps, main_tab_settings, main_tab_theme)
    }
    fun apps(view: View)
    {
        val title = ResourceUtil.getString(R.string.name_apps)

        changeFragment(AppsFragment(), title)
        changeTab(title, main_tab_apps, main_tab_home, main_tab_settings, main_tab_theme)
    }
    fun settings(view: View)
    {
        val title = ResourceUtil.getString(R.string.name_settings)

        changeFragment(SettingsFragment(), title)
        changeTab(title, main_tab_settings, main_tab_home, main_tab_apps, main_tab_theme)
    }
    fun theme(view: View)
    {
        val title = ResourceUtil.getString(R.string.name_theme)

        changeFragment(ThemeFragment(), title)
        changeTab(title, main_tab_theme, main_tab_home, main_tab_apps, main_tab_settings)
    }

    //메소드
    private fun changeFragment(fragment: Fragment, tag: String)
    {
        //동일 탭 실행 방지
        if (supportFragmentManager.findFragmentById(R.id.main_content)!!.tag == tag) return

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_content, fragment, tag)
            .commit()
    }
    private fun changeTab(title: String, selectedView: View, vararg unselectedViews: View)
    {
        //타이틀 변경
        title_indicator.text = title

        //탭 보이기
        main_tab_container.visibility = View.VISIBLE

        //탭 선택
        selectedView.alpha(1f, AnimationUtil.DEFAULT_DURATION).scale(1.1f, AnimationUtil.DEFAULT_DURATION)

        //탭 선택 해제
        unselectedViews.forEach { it.alpha(0.5f, AnimationUtil.DEFAULT_DURATION).scale(0.9f, AnimationUtil.DEFAULT_DURATION) }
    }
}