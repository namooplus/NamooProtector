package nm.security.namooprotector.activity

import android.Manifest
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import nm.security.namooprotector.util.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import nm.security.namooprotector.fragment.*
import nm.security.namooprotector.R
import com.google.android.gms.ads.*
import nm.security.namooprotector.BuildConfig

class MainActivity: AppCompatActivity()
{
    //광고
    private var interstitialAd: InterstitialAd? = null

    private var tabClick = 0

    private var previousTab = 1
    private var currentTab = 1

    //라이프사이클
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initFlag()
        initVariables()
        initFragment()
    }
    override fun onResume()
    {
        super.onResume()

        init()
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
        (main_ad_view.parent as ViewGroup).removeView(main_ad_view)

        super.onDestroy()
    }

    //설정
    private fun initFlag()
    {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
    private fun initVariables()
    {
        tabClick = 0

        previousTab = 1
        currentTab = 1
    }
    private fun initFragment()
    {
        supportFragmentManager
            .beginTransaction()
            .add(R.id.main_content, LockAppsFragment())
            .commit()

        changeAlpha(1f, main_menu_lock_apps)
        changeAlpha(0.5f, main_menu_lock_settings, main_menu_settings, main_menu_backup, main_menu_theme, main_menu_support, main_menu_about)
    }

    fun init()
    {
        if (CheckUtil.isPasswordSet && CheckUtil.isUsageStatsPermissionGranted) //일반적인 상태
        {
            ServiceUtil.runService(true)

            main_menu.visibility = View.VISIBLE
            main_line.visibility = View.VISIBLE

            if (currentTab == 0)
                changeFragment(previousTab)
        }
        else //초기설정 필요
        {
            ServiceUtil.runService(false)

            main_menu.visibility = View.GONE
            main_line.visibility = View.GONE

            changeFragment(0)
        }
    }
    fun initAd()
    {
        if (DataUtil.getBoolean("removeAds", DataUtil.NP)) //광고 제거
        {
            main_ad_view.visibility = View.GONE
            interstitialAd = null
        }
        else
        {
            //배너광고
            if (!main_ad_view.isShown) //재로드 방지
            {
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

            //전면광고
            if (interstitialAd == null) //재로드 방지
            {
                interstitialAd = InterstitialAd(this)
                interstitialAd!!.adUnitId = BuildConfig.InterstitialAdKey
                interstitialAd!!.adListener = object: AdListener()
                {
                    override fun onAdClosed()
                    {
                        if (interstitialAd != null) interstitialAd!!.loadAd(AdRequest.Builder().build())
                    }
                }
                interstitialAd!!.loadAd(AdRequest.Builder().build())
            }
        }
    }

    //클릭 이벤트
    fun lockApps(view: View)
    {
        changeFragment(1)

        changeAlpha(1f, main_menu_lock_apps)
        changeAlpha(0.5f, main_menu_lock_settings, main_menu_settings, main_menu_backup, main_menu_theme, main_menu_support, main_menu_about)
    }
    fun lockSettings(view: View)
    {
        changeFragment(2)

        changeAlpha(1f, main_menu_lock_settings)
        changeAlpha(0.5f, main_menu_lock_apps, main_menu_settings, main_menu_backup, main_menu_theme, main_menu_support, main_menu_about)
    }
    fun settings(view: View)
    {
        changeFragment(3)

        changeAlpha(1f, main_menu_settings)
        changeAlpha(0.5f, main_menu_lock_apps, main_menu_lock_settings, main_menu_backup, main_menu_theme, main_menu_support, main_menu_about)
    }
    fun backup(view: View)
    {
        TedPermission
            .with(this)
            .setPermissionListener(object: PermissionListener
            {
                override fun onPermissionGranted()
                {
                    changeFragment(4)

                    changeAlpha(1f, main_menu_backup)
                    changeAlpha(0.5f, main_menu_lock_apps, main_menu_lock_settings, main_menu_settings, main_menu_theme, main_menu_support, main_menu_about)
                }
                override fun onPermissionDenied(deniedPermissions: MutableList<String>?)
                {
                    Toast.makeText(this@MainActivity, getString(R.string.error_permission_denied), Toast.LENGTH_SHORT).show()
                }
            })
            .setRationaleTitle(getString(R.string.permission_storage_title))
            .setRationaleMessage(getString(R.string.permission_storage_message))
            .setDeniedMessage(getString(R.string.permission_denied_message))
            .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .check()
    }
    fun theme(view: View)
    {
        changeFragment(5)

        changeAlpha(1f, main_menu_theme)
        changeAlpha(0.5f, main_menu_lock_apps, main_menu_lock_settings, main_menu_settings, main_menu_backup, main_menu_support, main_menu_about)
    }
    fun support(view: View)
    {
        if (!CheckUtil.isNetworkAvailable)
            Toast.makeText(this, getString(R.string.error_network_disconnected), Toast.LENGTH_SHORT).show()

        else
        {
            changeFragment(6)

            changeAlpha(1f, main_menu_support)
            changeAlpha(0.5f, main_menu_lock_apps, main_menu_lock_settings, main_menu_settings, main_menu_backup, main_menu_theme, main_menu_about)
        }
    }
    fun about(view: View)
    {
        changeFragment(7)

        changeAlpha(1f, main_menu_about)
        changeAlpha(0.5f, main_menu_lock_apps, main_menu_lock_settings, main_menu_settings, main_menu_backup, main_menu_theme, main_menu_support)
    }

    //메소드
    private fun changeFragment(tabNumber: Int)
    {
        //중복 방지
        if (tabNumber == currentTab)
            return

        //재지정
        previousTab = currentTab
        currentTab = tabNumber

        //변경
        supportFragmentManager
            .beginTransaction()
            .replace(nm.security.namooprotector.R.id.main_content, when(tabNumber)
            {
                1 -> LockAppsFragment()
                2 -> LockSettingsFragment()
                3 -> SettingsFragment()
                4 -> BackupFragment()
                5 -> ThemeFragment()
                6 -> SupportFragment()
                7 -> AboutFragment()
                else -> WizardFragment()
            })
            .commit()

        //광고 표시
        tabClick++

        if (tabClick >= 4 && interstitialAd != null && interstitialAd!!.isLoaded)
        {
            tabClick = 0
            interstitialAd!!.show()
        }
    }
    private fun changeAlpha(alpha: Float, vararg views: View)
    {
        views.forEach { it.alpha = alpha }
    }
}