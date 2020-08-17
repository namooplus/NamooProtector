package nm.security.namooprotector.util

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.util.TypedValue
import com.andrognito.patternlockview.PatternLockView
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
            context.resources.getDrawable(R.drawable.ic_launcher, null)
        }
    }

    fun intColorToStringColor(color: Int): String
    {
        return String.format("#%08X", -0x1 and color)
    }
    fun dpToPx(dimen: Float): Float
    {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dimen, context.resources.displayMetrics)
    }

    fun patternToString(patternView: PatternLockView, pattern: List<PatternLockView.Dot>?): String
    {
        if (pattern == null) return ""

        val stringBuilder = StringBuilder()
        for (dot in pattern) stringBuilder.append(dot.row * patternView.dotCount + dot.column + 1)

        return stringBuilder.toString()
    }
}