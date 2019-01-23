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
import android.widget.Toast
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.fragment_settings.*
import nm.security.namooprotector.R
import nm.security.namooprotector.util.DataUtil

class SettingsFragment : Fragment()
{
    private val policyManager by lazy { context!!.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager }
    private val deviceAdmin by lazy { ComponentName(context, SettingsFragment.DeviceAdminHelper:: class.java) }

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
        //보안
        settings_security_prevent_uninstall_button.setOnClickListener { changeAdministratorPermission() }
        settings_security_dark_lockscreen_button.setOnClickListener { changeState("darkLockscreen") }
        settings_security_watch_fail_button.setOnClickListener {
            if (DataUtil.getBoolean("watchFail", DataUtil.SETTING))
            {
                DataUtil.put("watchFail", DataUtil.SETTING, false)
                initState()
            }
            else
            {
                TedPermission
                    .with(context)
                    .setPermissionListener(object: PermissionListener
                    {
                        override fun onPermissionGranted()
                        {
                            DataUtil.put("watchFail", DataUtil.SETTING, true)
                            initState()

                            Toast.makeText(activity, getString(R.string.alert_readme), Toast.LENGTH_SHORT).show()
                        }
                        override fun onPermissionDenied(deniedPermissions: MutableList<String>?)
                        {
                            Toast.makeText(context, getString(R.string.error_permission_denied), Toast.LENGTH_SHORT).show()
                        }
                    })
                    .setRationaleTitle(getString(R.string.permission_camera_title))
                    .setRationaleMessage(getString(R.string.permission_camera_message))
                    .setDeniedMessage(getString(R.string.permission_denied_message))
                    .setPermissions(Manifest.permission.CAMERA)
                    .check()
            }
        }
        settings_security_cover_button.setOnClickListener {
            if (DataUtil.getBoolean("cover", DataUtil.SETTING))
                DataUtil.put("cover", DataUtil.SETTING, false)

            else
            {
                DataUtil.put("cover", DataUtil.SETTING, true)

                Toast.makeText(activity, getString(R.string.alert_readme), Toast.LENGTH_SHORT).show()
            }

            initState()
        }

        //알림
        settings_notification_protection_notification_button.setOnClickListener { changeState("protectionNotification") }

        //도움말
        settings_help_watch_fail_button.setOnClickListener {
            val dialog = AlertDialog.Builder(context)

            with(dialog)
            {
                setTitle(getString(R.string.alert_watch_fail_help_title))
                setMessage(getString(R.string.alert_watch_fail_help_message))
                setCancelable(false)
                setPositiveButton(getString(R.string.alert_watch_fail_help_positive), null)
                show()
            }
        }
        settings_help_cover_button.setOnClickListener {
            val dialog = AlertDialog.Builder(context)

            with(dialog)
            {
                setTitle(getString(R.string.alert_cover_help_title))
                setMessage(getString(R.string.alert_cover_help_message))
                setCancelable(false)
                setPositiveButton(getString(R.string.alert_cover_help_positive), null)
                show()
            }
        }
    }
    private fun initState()
    {
        //보안
        settings_security_prevent_uninstall_button.setChecked(policyManager.isAdminActive(deviceAdmin))
        settings_security_dark_lockscreen_button.setChecked(DataUtil.getBoolean("darkLockscreen", DataUtil.SETTING))
        settings_security_watch_fail_button.setChecked(DataUtil.getBoolean("watchFail", DataUtil.SETTING))
        settings_security_cover_button.setChecked(DataUtil.getBoolean("cover", DataUtil.SETTING))

        //알림
        settings_notification_protection_notification_button.setChecked(DataUtil.getBoolean("protectionNotification", DataUtil.SETTING))
    }

    //메소드
    private fun changeState(name: String)
    {
        DataUtil.put(name, DataUtil.SETTING, !DataUtil.getBoolean(name, DataUtil.SETTING))

        initState()
    }
    private fun changeAdministratorPermission()
    {
        if (policyManager.isAdminActive(deviceAdmin))
        {
            val dialog = AlertDialog.Builder(context)

            with(dialog)
            {
                setMessage(getString(R.string.alert_release_admin_message))
                setPositiveButton(getString(R.string.alert_release_admin_positive))
                { _, _ ->
                    policyManager.removeActiveAdmin(deviceAdmin)
                    settings_security_prevent_uninstall_button.setChecked(false)
                }
                setNegativeButton(getString(R.string.common_cancel), null)
                show()
            }
        }
        else
        {
            val dialog = AlertDialog.Builder(context)

            with(dialog)
            {
                setTitle(getString(R.string.permission_device_admin_title))
                setMessage(getString(R.string.permission_device_admin_message))
                setPositiveButton(getString(R.string.permission_grant))
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