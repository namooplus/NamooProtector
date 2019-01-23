package nm.security.namooprotector.util

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import nm.security.namooprotector.NamooProtector.Companion.context
import nm.security.namooprotector.R

object ConvertUtil
{
    fun packageNameToAppName(packageName: String): String
    {
        return try
        {
            context.packageManager.getApplicationLabel(context.packageManager.getApplicationInfo(packageName, 0)).toString()
        }
        catch (e: PackageManager.NameNotFoundException)
        {
            context.getString(R.string.error_name_not_found)
        }
    }
    fun packageNameToAppIcon(packageName: String): Drawable
    {
        return try
        {
            context.packageManager.getApplicationIcon(packageName)
        }
        catch (e: PackageManager.NameNotFoundException)
        {
            context.resources.getDrawable(R.drawable.icon_np_text, null)
        }
    }
    fun intColorToStringColor(color: Int): String
    {
        return String.format("#%08X", -0x1 and color)
    }
}