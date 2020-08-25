package nm.security.namooprotector.fragment

import android.Manifest
import android.app.AlertDialog
import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import co.infinum.goldfinger.Goldfinger
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.fragment_settings.*
import nm.security.namooprotector.R
import nm.security.namooprotector.activity.*
import nm.security.namooprotector.service.ProtectorServiceHelper
import nm.security.namooprotector.util.ActivityUtil
import nm.security.namooprotector.util.CheckUtil
import nm.security.namooprotector.util.ResourceUtil
import nm.security.namooprotector.util.SettingsUtil
import kotlin.reflect.KMutableProperty0

class SettingsFragment : Fragment()
{
    private val fingerprintManager by lazy { Goldfinger.Builder(context!!).build() }
    private val policyManager by lazy { context!!.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager }
    private val deviceAdmin by lazy { ComponentName(context!!, DeviceAdminHelper::class.java) }

    //라이프사이클
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        initClick()
    }
    override fun onResume()
    {
        super.onResume()

        initState()
    }

    //설정
    private fun initClick()
    {
        //잠금 방식
        settings_lock_type_pin.setOnClickListener { ActivityUtil.startActivityWithAnimation(activity!!, PinActivity::class.java) }
        settings_lock_type_pattern.setOnClickListener { ActivityUtil.startActivityWithAnimation(activity!!, PatternActivity::class.java) }
        settings_lock_type_fingerprint.setOnClickListener { toggleStateFingerprint() }

        //PIN 추가 설정
        settings_pin_additional_settings_click_haptic_button.setOnClickListener { toggleState(SettingsUtil::clickHaptic) }
        settings_pin_additional_settings_hide_click_button.setOnClickListener { toggleState(SettingsUtil::hideClick) }
        settings_pin_additional_settings_quick_unlock_button.setOnClickListener { toggleState(SettingsUtil::quickUnlock) }
        settings_pin_additional_settings_rearrange_key_button.setOnClickListener { toggleState(SettingsUtil::rearrangeKey) }
        settings_pin_additional_settings_light_key_button.setOnClickListener { toggleState(SettingsUtil::lightKey) }

        //패턴 추가 설정
        settings_pattern_additional_settings_draw_haptic_button.setOnClickListener { toggleState(SettingsUtil::drawHaptic) }
        settings_pattern_additional_settings_hide_draw_button.setOnClickListener { toggleState(SettingsUtil::hideDraw) }
        settings_pattern_additional_settings_light_dot_button.setOnClickListener { toggleState(SettingsUtil::lightDot) }

        //보안
        settings_security_prevent_uninstall_button.setOnClickListener { toggleStatePreventUninstall() }
        settings_security_dark_lockscreen_button.setOnClickListener { toggleState(SettingsUtil::darkLockscreen) }
        settings_security_watch_fail_button.setOnClickListener { toggleStateWatchFail() }
        settings_security_cover_button.setOnClickListener { toggleState(SettingsUtil::cover) }

        //편의 기능
        settings_convenience_protection_notification_button.setOnClickListener { toggleState(SettingsUtil::protectionNotification) }
        settings_convenience_lock_delay_button.setOnClickListener { toggleStateLockDelay(it) }
    }
    private fun initState()
    {
        //잠금 방식
        when (SettingsUtil.lockType)
        {
            "pin" ->
            {
                settings_lock_type_pin.isChecked = true
                settings_lock_type_pattern.isChecked = false

                settings_pin_additional_settings_container.visibility = View.VISIBLE
                settings_pattern_additional_settings_container.visibility = View.GONE
            }
            "pattern" ->
            {
                settings_lock_type_pin.isChecked = false
                settings_lock_type_pattern.isChecked = true

                settings_pin_additional_settings_container.visibility = View.GONE
                settings_pattern_additional_settings_container.visibility = View.VISIBLE
            }
        }
        settings_lock_type_fingerprint.isChecked = SettingsUtil.fingerprint

        //PIN 추가 설정
        settings_pin_additional_settings_click_haptic_button.isChecked = SettingsUtil.clickHaptic
        settings_pin_additional_settings_hide_click_button.isChecked = SettingsUtil.hideClick
        settings_pin_additional_settings_quick_unlock_button.isChecked = SettingsUtil.quickUnlock
        settings_pin_additional_settings_rearrange_key_button.isChecked = SettingsUtil.rearrangeKey
        settings_pin_additional_settings_light_key_button.isChecked = SettingsUtil.lightKey

        //패턴 추가 설정
        settings_pattern_additional_settings_draw_haptic_button.isChecked = SettingsUtil.drawHaptic
        settings_pattern_additional_settings_hide_draw_button.isChecked = SettingsUtil.hideDraw
        settings_pattern_additional_settings_light_dot_button.isChecked = SettingsUtil.lightDot

        //보안
        settings_security_prevent_uninstall_button.isChecked = policyManager.isAdminActive(deviceAdmin)
        settings_security_dark_lockscreen_button.isChecked = SettingsUtil.darkLockscreen
        settings_security_watch_fail_button.isChecked = SettingsUtil.watchFail
        settings_security_cover_button.isChecked = SettingsUtil.cover

        //알림
        settings_convenience_protection_notification_button.isChecked = SettingsUtil.protectionNotification
        if (SettingsUtil.lockDelay == 0)
        {
            settings_convenience_lock_delay_button.isChecked = false
            settings_convenience_lock_delay_button.setDescription(ResourceUtil.getString(R.string.common_none))
        }
        else
        {
            settings_convenience_lock_delay_button.isChecked = true
            settings_convenience_lock_delay_button.setDescription(SettingsUtil.lockDelay.toString() + ResourceUtil.getString(R.string.common_seconds))
        }
    }

    //메소드
    private fun toggleState(property: KMutableProperty0<Boolean>)
    {
        property.set(!property.get())
        initState()
    }
    private fun toggleStateFingerprint()
    {
        if (SettingsUtil.fingerprint) //비활성화
        {
            SettingsUtil.fingerprint = false
            initState()
        }
        else //활성화
        {
            when
            {
                !fingerprintManager.hasFingerprintHardware() -> Toast.makeText(context, getString(R.string.error_fingerprint_no_hardware), Toast.LENGTH_LONG).show()
                !fingerprintManager.hasEnrolledFingerprint() -> Toast.makeText(context, getString(R.string.error_fingerprint_not_enrolled), Toast.LENGTH_LONG).show()
                SettingsUtil.lockType == null -> Toast.makeText(context, getString(R.string.error_fingerprint_no_password), Toast.LENGTH_LONG).show()
                else ->
                {
                    SettingsUtil.fingerprint = true
                    initState()
                }
            }
        }
    }
    private fun toggleStatePreventUninstall()
    {
        if (policyManager.isAdminActive(deviceAdmin)) //비활성화
        {
            with(AlertDialog.Builder(context))
            {
                setMessage(getString(R.string.alert_release_admin))
                setPositiveButton(getString(R.string.common_release))
                { _, _ ->
                    policyManager.removeActiveAdmin(deviceAdmin)
                    settings_security_prevent_uninstall_button.isChecked = false
                }
                setNegativeButton(getString(R.string.common_cancel), null)
                show()
            }
        }
        else //활성화
        {
            with(AlertDialog.Builder(context))
            {
                setTitle(getString(R.string.permission_device_admin_title))
                setMessage(getString(R.string.permission_device_admin_message))
                setPositiveButton(getString(R.string.common_grant))
                { _, _ ->
                    val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, deviceAdmin)
                    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getString(R.string.permission_device_admin_message))
                    startActivity(intent)
                }
                setNegativeButton(getString(R.string.common_cancel), null)
                show()
            }
        }
    }
    private fun toggleStateWatchFail()
    {
        if (SettingsUtil.watchFail) //비활성화
        {
            SettingsUtil.watchFail = false
            initState()
        }
        else //활성화
        {
            if (CheckUtil.isFlashSupported)
            {
                SettingsUtil.watchFail = true
                initState()
            }
            else Toast.makeText(activity, ResourceUtil.getString(R.string.error_flash_not_supported), Toast.LENGTH_SHORT).show()
        }
    }
    private fun toggleStateLockDelay(view: View)
    {
        with(PopupMenu(context!!, view))
        {
            menu.add(ResourceUtil.getString(R.string.common_none))
            menu.add("5" + ResourceUtil.getString(R.string.common_seconds))
            menu.add("10" + ResourceUtil.getString(R.string.common_seconds))
            menu.add("30" + ResourceUtil.getString(R.string.common_seconds))
            menu.add("60" + ResourceUtil.getString(R.string.common_seconds))

            setOnMenuItemClickListener {
                when (it.title.toString())
                {
                    ResourceUtil.getString(R.string.common_none) -> SettingsUtil.lockDelay = 0
                    else -> SettingsUtil.lockDelay = it.title.toString().replace(ResourceUtil.getString(R.string.common_seconds), "").toInt()
                }

                initState()
                ProtectorServiceHelper.resetAuthorizedApps()

                true
            }
            show()
        }
    }

    //클래스
    class DeviceAdminHelper : DeviceAdminReceiver()
    {
        override fun onEnabled(context: Context, intent: Intent)
        {

        }
        override fun onDisabled(context: Context, intent: Intent)
        {

        }
    }
}