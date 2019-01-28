package nm.security.namooprotector.activity

import android.animation.ObjectAnimator
import android.content.Context
import android.view.View
import nm.security.namooprotector.util.*
import android.content.Intent
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.lockscreen.*
import kotlinx.android.synthetic.main.lockscreen_bottom_default.*
import kotlinx.android.synthetic.main.lockscreen_top_default.*
import nm.security.namooprotector.widget.PatternView
import co.infinum.goldfinger.Goldfinger
import nm.security.namooprotector.service.ProtectorState
import nm.security.namooprotector.util.ThemeUtil
import nm.security.namooprotector.util.DataUtil.SETTING
import android.content.BroadcastReceiver
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.content.IntentFilter
import nm.security.namooprotector.R

class LockScreen: AppCompatActivity()
{
    private val fingerprintManager by lazy { Goldfinger.Builder(this).build() }
    private var cameraManager: CameraManager? = null
    private val vibrateManager by lazy { getSystemService(Context.VIBRATOR_SERVICE) as Vibrator }

    private val broadcastManager by lazy { LocalBroadcastManager.getInstance(this) }

    private var inputPin = ""
    private var failCount = 0

    //라이프사이클
    override fun onCreate(savedInstanceState: Bundle?)
    {
        overridePendingTransition(0, 0)
        super.onCreate(savedInstanceState)
        setContentView(nm.security.namooprotector.R.layout.lockscreen)

        initFlag()
        initReceiver()
    }
    override fun onNewIntent(intent: Intent?)
    {
        super.onNewIntent(intent)
        setIntent(intent)
    }
    override fun onResume()
    {
        super.onResume()

        ProtectorState.currentState = ProtectorState.LOCKED

        failCount = 0

        initUI()
        initTheme()
    }
    override fun onBackPressed()
    {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
    override fun onStop()
    {
        //지문인식
        fingerprintManager.cancel()

        if (cameraManager != null)
            flash(false)

        super.onStop()
    }
    override fun finish()
    {
        super.finish()
        overridePendingTransition(0, 0)
    }
    override fun onDestroy()
    {
        broadcastManager.unregisterReceiver(receiver)

        super.onDestroy()
    }

    //설정
    private fun initFlag()
    {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
    private fun initReceiver()
    {
        val intentFilter = IntentFilter()
        intentFilter.addAction("CLOSE")

        broadcastManager.registerReceiver(receiver, intentFilter)
    }

    private fun initUI()
    {
        //기본 정보
        lockscreen_icon_indicator.setImageDrawable(ConvertUtil.packageNameToAppIcon(intent.getStringExtra("packageName")))

        //잠금 방식
        when (PasswordUtil.type)
        {
            "pin" ->
            {
                lockscreen_pin_layout.visibility = View.VISIBLE
                lockscreen_pattern_layout.visibility = View.GONE

                initPin()
                initFingerprint()
            }
            "pattern" ->
            {
                lockscreen_pin_layout.visibility = View.GONE
                lockscreen_pattern_layout.visibility = View.VISIBLE

                initPattern()
                initFingerprint()
            }
            else ->
            {
                Toast.makeText(this, getString(R.string.error_lockscreen_no_password), Toast.LENGTH_SHORT).show()

                val intent = Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_HOME)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        }

        //세부 설정
        if (DataUtil.getBoolean("darkLockscreen", SETTING))
            window.attributes.screenBrightness = 0.05f

        if (DataUtil.getBoolean("cover", SETTING) && !isLockscreenForNP) //(나프 접근시 제외)
        {
            lockscreen_cover_layout.visibility = View.VISIBLE
            lockscreen_cover_layout.setOnLongClickListener {
                lockscreen_cover_layout.visibility = View.GONE
                true
            }
        }
    }
    private fun initPin()
    {
        if (DataUtil.getBoolean("hideClick", SETTING))
        {
            lockscreen_pin_one.setBackgroundColor(0x00ffffff)
            lockscreen_pin_two.setBackgroundColor(0x00ffffff)
            lockscreen_pin_three.setBackgroundColor(0x00ffffff)
            lockscreen_pin_four.setBackgroundColor(0x00ffffff)
            lockscreen_pin_five.setBackgroundColor(0x00ffffff)
            lockscreen_pin_six.setBackgroundColor(0x00ffffff)
            lockscreen_pin_seven.setBackgroundColor(0x00ffffff)
            lockscreen_pin_eight.setBackgroundColor(0x00ffffff)
            lockscreen_pin_nine.setBackgroundColor(0x00ffffff)
            lockscreen_pin_check.setBackgroundColor(0x00ffffff)
            lockscreen_pin_zero.setBackgroundColor(0x00ffffff)
            lockscreen_pin_clear.setBackgroundColor(0x00ffffff)
        }

        if (DataUtil.getBoolean("rearrangeKey", SETTING))
        {
            val keys = mutableListOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
            keys.shuffle()

            lockscreen_pin_one.text = keys[0]
            lockscreen_pin_two.text = keys[1]
            lockscreen_pin_three.text = keys[2]
            lockscreen_pin_four.text = keys[3]
            lockscreen_pin_five.text = keys[4]
            lockscreen_pin_six.text = keys[5]
            lockscreen_pin_seven.text = keys[6]
            lockscreen_pin_eight.text = keys[7]
            lockscreen_pin_nine.text = keys[8]
            lockscreen_pin_zero.text = keys[9]
        }

        if (DataUtil.getBoolean("lightKey", SETTING))
        {
            AnimationUtil.alpha(lockscreen_pin_one, 0.1f, AnimationUtil.DEFAULT_DURATION, 0)
            AnimationUtil.alpha(lockscreen_pin_two, 0.1f, AnimationUtil.DEFAULT_DURATION, 0)
            AnimationUtil.alpha(lockscreen_pin_three, 0.1f, AnimationUtil.DEFAULT_DURATION, 0)
            AnimationUtil.alpha(lockscreen_pin_four, 0.1f, AnimationUtil.DEFAULT_DURATION, 0)
            AnimationUtil.alpha(lockscreen_pin_five, 0.1f, AnimationUtil.DEFAULT_DURATION, 0)
            AnimationUtil.alpha(lockscreen_pin_six, 0.1f, AnimationUtil.DEFAULT_DURATION, 0)
            AnimationUtil.alpha(lockscreen_pin_seven, 0.1f, AnimationUtil.DEFAULT_DURATION, 0)
            AnimationUtil.alpha(lockscreen_pin_eight, 0.1f, AnimationUtil.DEFAULT_DURATION, 0)
            AnimationUtil.alpha(lockscreen_pin_nine, 0.1f, AnimationUtil.DEFAULT_DURATION, 0)
            AnimationUtil.alpha(lockscreen_pin_check, 0.1f, AnimationUtil.DEFAULT_DURATION, 0)
            AnimationUtil.alpha(lockscreen_pin_zero, 0.1f, AnimationUtil.DEFAULT_DURATION, 0)
            AnimationUtil.alpha(lockscreen_pin_clear, 0.1f, AnimationUtil.DEFAULT_DURATION, 0)
        }
    }
    private fun initPattern()
    {
        lockscreen_pattern_view.isTactileFeedbackEnabled = DataUtil.getBoolean("drawHaptic", SETTING)
        lockscreen_pattern_view.isInStealthMode = DataUtil.getBoolean("hideDraw", SETTING)
        lockscreen_pattern_view.setOnPatternListener(object : PatternView.OnPatternListener()
        {
            override fun onPatternStart()
            {

            }
            override fun onPatternCleared()
            {

            }
            override fun onPatternCellAdded(pattern: List<PatternView.Cell>, SimplePattern: String)
            {

            }
            override fun onPatternDetected(pattern: List<PatternView.Cell>, result: String)
            {
                if (PasswordUtil.pattern == result)
                    successUnlock()

                else if (result.length > 1)
                    failUnlock()
            }
        })

        if (DataUtil.getBoolean("lightDot", SETTING))
            AnimationUtil.alpha(lockscreen_pattern_view, 0.1f, AnimationUtil.DEFAULT_DURATION, 0)
    }
    private fun initFingerprint()
    {
        if (FingerprintUtil.isFingerprintActivated && FingerprintUtil.isSupportFingerprint(fingerprintManager) == FingerprintUtil.AVAILABLE)
        {
            lockscreen_fingerprint_indicator.visibility = View.VISIBLE

            fingerprintManager.authenticate(object : Goldfinger.Callback()
            {
                override fun onSuccess(value: String)
                {
                    successUnlock()
                }
                override fun onError(error: co.infinum.goldfinger.Error)
                {
                    failUnlock()
                }
            })
        }
        else
            lockscreen_fingerprint_indicator.visibility = View.INVISIBLE
    }
    private fun initTheme()
    {
        ThemeUtil.apply(this)
    }

    //클릭이벤트
    fun key(view: View)
    {
        vibrate()

        when ((view as Button).text.toString())
        {
            "V" ->
            {
                if (PasswordUtil.pin == inputPin)
                    successUnlock()

                else if (inputPin.length > 1)
                    failUnlock()
            }
            "X" ->
            {
                lockscreen_pin_indicator.text = ""
                inputPin = ""
            }
            else ->
            {
                lockscreen_pin_indicator.append("•")
                inputPin += view.text.toString()

                if (DataUtil.getBoolean("quickUnlock", SETTING) && PasswordUtil.pin == inputPin)
                    successUnlock()
            }
        }
    }

    //메소드
    private val isLockscreenForNP
        get() = intent.getStringExtra("packageName") == packageName

    private fun vibrate()
    {
        if (DataUtil.getBoolean("clickHaptic", SETTING))
        {
            if (Build.VERSION.SDK_INT >= 26)
                vibrateManager.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))

            else
                vibrateManager.vibrate(20)
        }
    }
    private fun successUnlock()
    {
        ProtectorState.currentState = ProtectorState.UNLOCKED

        finish()
    }
    private fun failUnlock()
    {
        //애니메이션
        val shake = ObjectAnimator.ofFloat(lockscreen_base_layout, "translationX", 0f, 25f, -25f, 25f, -25f, 15f, -15f, 6f, -6f, 0f)
        shake.duration = 900
        shake.start()

        //초기화
        lockscreen_pin_indicator.text = ""
        inputPin = ""
        lockscreen_pattern_view.clearPattern()

        //틀림 감지
        watchFail()
    }
    private fun watchFail()
    {
        failCount++

        if (DataUtil.getBoolean("watchFail", DataUtil.SETTING) && failCount >= 5)
        {
            failCount = 0

            flash(true)
            Handler().postDelayed({ flash(false) }, 4000)
        }
    }
    private fun flash(on: Boolean)
    {
        if (!CheckUtil.isSupportFlash)
        {
            Toast.makeText(this, getString(R.string.error_flash_not_supported), Toast.LENGTH_LONG).show()
            return
        }

        if (cameraManager == null)
            cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager

        try
        {
            cameraManager!!.setTorchMode(cameraManager!!.cameraIdList[0], on)
        }
        catch (e: CameraAccessException)
        {
            Toast.makeText(this, getString(R.string.error_camera_not_accessed), Toast.LENGTH_LONG).show()
        }
        finally
        {
            if (on) lockscreen_watcher_state_layout.visibility = View.VISIBLE
            else lockscreen_watcher_state_layout.visibility = View.GONE
        }
    }

    //리시버
    var receiver: BroadcastReceiver = object : BroadcastReceiver()
    {
        override fun onReceive(context: Context, intent: Intent)
        {
            if (intent.action == "CLOSE")
                finish()
        }
    }
}