package nm.security.namooprotector.activity

import android.app.ActivityOptions
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import nm.security.namooprotector.util.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*
import nm.security.namooprotector.fragment.*
import nm.security.namooprotector.R
import com.google.android.gms.ads.*
import nm.security.namooprotector.BuildConfig
import nm.security.namooprotector.util.AnimationUtil.alpha
import nm.security.namooprotector.util.AnimationUtil.scale

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
    }

    //설정
    private fun initFragment()
    {
        supportFragmentManager
            .beginTransaction()
            .add(R.id.main_content, HomeFragment())
            .commit()

        changeTab("Home", main_tab_home, main_tab_apps, main_tab_settings, main_tab_theme)
    }
    private fun initState()
    {
        if (!CheckUtil.isNPValid)
        {
            main_tab_container.visibility = View.GONE

            changeFragment(WizardFragment())
            title_indicator.text = "Initial Settings"
        }
    }

    fun init()
    {
//        if (CheckUtil.isPasswordSet && CheckUtil.isUsageStatsPermissionGranted) //일반적인 상태
//        {
//            ServiceUtil.runService(true)
//
//            main_menu.visibility = View.VISIBLE
//            main_line.visibility = View.VISIBLE
//
//            if (currentTab == 0)
//                changeFragment(previousTab)
//        }
//        else //초기설정 필요
//        {
//            ServiceUtil.runService(false)
//
//            main_menu.visibility = View.GONE
//            main_line.visibility = View.GONE
//
//            changeFragment(0)
//        }
    }

    //클릭 이벤트
    fun home(view: View)
    {
        changeFragment(HomeFragment())
        changeTab("Home", main_tab_home, main_tab_apps, main_tab_settings, main_tab_theme)
    }
    fun apps(view: View)
    {
        changeFragment(AppsFragment())
        changeTab("Apps", main_tab_apps, main_tab_home, main_tab_settings, main_tab_theme)
    }
    fun settings(view: View)
    {
        changeFragment(SettingsFragment())
        changeTab("Settings", main_tab_settings, main_tab_home, main_tab_apps, main_tab_theme)
    }
    fun theme(view: View)
    {
        changeFragment(ThemeFragment())
        changeTab("Theme", main_tab_theme, main_tab_home, main_tab_apps, main_tab_settings)
    }

    //메소드
    private fun changeFragment(fragment: Fragment)
    {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_content, fragment)
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