package nm.security.namooprotector.activity

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.media.AudioAttributes
import android.os.*
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import co.infinum.goldfinger.Goldfinger
import co.infinum.goldfinger.Goldfinger.PromptParams
import com.andrognito.patternlockview.PatternLockView
import com.andrognito.patternlockview.listener.PatternLockViewListener
import kotlinx.android.synthetic.main.lockscreen.*
import nm.security.namooprotector.R
import nm.security.namooprotector.service.ProtectorServiceHelper
import nm.security.namooprotector.util.*
import nm.security.namooprotector.util.AnimationUtil.alpha
import java.lang.Exception

class LockScreen: AppCompatActivity()
{
    private val fingerprintManager by lazy { Goldfinger.Builder(this).build() }
    private val vibrateManager by lazy { getSystemService(Context.VIBRATOR_SERVICE) as Vibrator }
    private var cameraManager: CameraManager? = null

    private var inputPin = ""
    private var failCount = 0

    //라이프사이클
    override fun onCreate(savedInstanceState: Bundle?)
    {
        overridePendingTransition(0, 0)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lockscreen)

        ActivityUtil.initFlag(this, false)
    }
    override fun onNewIntent(intent: Intent?)
    {
        super.onNewIntent(intent)
        setIntent(intent)
    }
    override fun onResume()
    {
        super.onResume()

        failCount = 0

        initUI()
        initClick()
        initTheme()
    }
    override fun onStop()
    {
        //지문인식
        fingerprintManager.cancel()

        //틀림 감지
        if (cameraManager != null) flash(false)

        super.onStop()
    }
    override fun onBackPressed()
    {
        val home = Intent(Intent.ACTION_MAIN)
        home.addCategory(Intent.CATEGORY_HOME)
        home.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(home)
    }
    override fun finish()
    {
        super.finish()
        overridePendingTransition(0, 0)
    }

    //설정
    private fun initUI()
    {
        //기본 정보
        lockscreen_icon_indicator.setImageDrawable(ConvertUtil.packageNameToAppIcon(intent.getStringExtra("packageName") ?: packageName))

        //잠금 방식
        when (SettingsUtil.lockType)
        {
            "pin" ->
            {
                lockscreen_pin_indicator.visibility = View.VISIBLE
                lockscreen_key_container.visibility = View.VISIBLE
                lockscreen_pattern_container.visibility = View.GONE

                initPin()
                initFingerprint()
            }
            "pattern" ->
            {
                lockscreen_pin_indicator.visibility = View.GONE
                lockscreen_key_container.visibility = View.GONE
                lockscreen_pattern_container.visibility = View.VISIBLE

                initPattern()
                initFingerprint()
            }
            else ->
            {
                Toast.makeText(this, getString(R.string.error_lockscreen_no_password), Toast.LENGTH_SHORT).show()

                finish()
            }
        }

        //세부 설정
        if (SettingsUtil.darkLockscreen)
            window.attributes.screenBrightness = 0.05f

        if (SettingsUtil.cover)
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
        if (SettingsUtil.hideClick)
        {
            lockscreen_key_1.setBackgroundColor(0x00ffffff)
            lockscreen_key_2.setBackgroundColor(0x00ffffff)
            lockscreen_key_3.setBackgroundColor(0x00ffffff)
            lockscreen_key_4.setBackgroundColor(0x00ffffff)
            lockscreen_key_5.setBackgroundColor(0x00ffffff)
            lockscreen_key_6.setBackgroundColor(0x00ffffff)
            lockscreen_key_7.setBackgroundColor(0x00ffffff)
            lockscreen_key_8.setBackgroundColor(0x00ffffff)
            lockscreen_key_9.setBackgroundColor(0x00ffffff)
            lockscreen_key_ok.setBackgroundColor(0x00ffffff)
            lockscreen_key_0.setBackgroundColor(0x00ffffff)
            lockscreen_key_clear.setBackgroundColor(0x00ffffff)
        }

        if (SettingsUtil.rearrangeKey)
        {
            val keys = mutableListOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
            keys.shuffle()

            lockscreen_key_1.text = keys[0]
            lockscreen_key_2.text = keys[1]
            lockscreen_key_3.text = keys[2]
            lockscreen_key_4.text = keys[3]
            lockscreen_key_5.text = keys[4]
            lockscreen_key_6.text = keys[5]
            lockscreen_key_7.text = keys[6]
            lockscreen_key_8.text = keys[7]
            lockscreen_key_9.text = keys[8]
            lockscreen_key_0.text = keys[9]
        }

        if (SettingsUtil.lightKey)
        {
            lockscreen_key_1.alpha(0.1f, AnimationUtil.DEFAULT_DURATION)
            lockscreen_key_2.alpha(0.1f, AnimationUtil.DEFAULT_DURATION)
            lockscreen_key_3.alpha(0.1f, AnimationUtil.DEFAULT_DURATION)
            lockscreen_key_4.alpha(0.1f, AnimationUtil.DEFAULT_DURATION)
            lockscreen_key_5.alpha(0.1f, AnimationUtil.DEFAULT_DURATION)
            lockscreen_key_6.alpha(0.1f, AnimationUtil.DEFAULT_DURATION)
            lockscreen_key_7.alpha(0.1f, AnimationUtil.DEFAULT_DURATION)
            lockscreen_key_8.alpha(0.1f, AnimationUtil.DEFAULT_DURATION)
            lockscreen_key_9.alpha(0.1f, AnimationUtil.DEFAULT_DURATION)
            lockscreen_key_ok.alpha(0.1f, AnimationUtil.DEFAULT_DURATION)
            lockscreen_key_0.alpha(0.1f, AnimationUtil.DEFAULT_DURATION)
            lockscreen_key_clear.alpha(0.1f, AnimationUtil.DEFAULT_DURATION)
        }
    }
    private fun initPattern()
    {
        lockscreen_pattern_view.isTactileFeedbackEnabled = SettingsUtil.drawHaptic
        lockscreen_pattern_view.isInStealthMode = SettingsUtil.hideDraw
        lockscreen_pattern_view.addPatternLockListener(object: PatternLockViewListener
        {
            override fun onStarted()
            {

            }
            override fun onCleared()
            {

            }
            override fun onProgress(progressPattern: MutableList<PatternLockView.Dot>?)
            {

            }
            override fun onComplete(pattern: MutableList<PatternLockView.Dot>?)
            {
                val result = ConvertUtil.patternToString(lockscreen_pattern_view, pattern)

                if (SettingsUtil.pattern == result) successUnlock()
                else if (result.length > 1) failUnlock()
            }
        })

        if (SettingsUtil.lightDot)
            lockscreen_pattern_view.alpha(0.1f, AnimationUtil.DEFAULT_DURATION)
    }
    private fun initFingerprint()
    {
        if (SettingsUtil.fingerprint && CheckUtil.isFingerprintAvailable(fingerprintManager))
        {
            lockscreen_fingerprint_indicator.visibility = View.VISIBLE

            val params: PromptParams = PromptParams.Builder(this)
                .title("나무프로텍터")
                .subtitle(ConvertUtil.packageNameToAppName(intent.getStringExtra("packageName") ?: packageName))
                .description("지문인식으로 앱 잠금을 해제합니다.")
                .negativeButtonText("취소")
                .build()

            fingerprintManager.authenticate(params, object: Goldfinger.Callback {
                override fun onResult(result: Goldfinger.Result)
                {
                    when (result.type())
                    {
                        Goldfinger.Type.SUCCESS -> successUnlock()
                        Goldfinger.Type.ERROR -> failUnlock()
                    }
                }
                override fun onError(e: Exception)
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
    private fun initClick()
    {
        arrayOf(lockscreen_key_1, lockscreen_key_2, lockscreen_key_3, lockscreen_key_4, lockscreen_key_5, lockscreen_key_6,
            lockscreen_key_7, lockscreen_key_8, lockscreen_key_9, lockscreen_key_ok, lockscreen_key_0, lockscreen_key_clear).forEach {
            it.setOnClickListener { view -> keyClick(view) }
        }
    }

    //메소드
    private fun keyClick(view: View)
    {
        vibrate()

        when ((view as Button).text.toString())
        {
            "V" ->
            {
                if (SettingsUtil.pin == inputPin)
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

                if (SettingsUtil.quickUnlock && SettingsUtil.pin == inputPin)
                    successUnlock()
            }
        }
    }
    private fun vibrate()
    {
        if (SettingsUtil.clickHaptic)
        {
            if (Build.VERSION.SDK_INT >= 26)
            {
                val effect = VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE)
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()

                vibrateManager.vibrate(effect, audioAttributes)
            }
            else vibrateManager.vibrate(20)
        }
    }

    private fun successUnlock()
    {
        //잠금 해제
        ProtectorServiceHelper.addAuthorizedApp(intent.getStringExtra("packageName") ?: packageName, SettingsUtil.lockDelay.toLong())
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

        if (SettingsUtil.watchFail && failCount >= 5)
        {
            failCount = 0

            flash(true)
            Handler().postDelayed({ flash(false) }, 4000)
        }
    }
    private fun flash(on: Boolean)
    {
        if (!CheckUtil.isFlashSupported)
            return

        //필요시 등록
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
            if (on) lockscreen_watcher_layout.visibility = View.VISIBLE
            else lockscreen_watcher_layout.visibility = View.GONE
        }
    }
}