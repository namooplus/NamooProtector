package nm.security.namooprotector.util

import android.content.Context
import nm.security.namooprotector.NamooProtector.Companion.context

object DataUtil
{
    const val NP = "NP" //광고
    const val LOCK = "Lock" //잠금방식
    const val APPS = "Apps" //잠금 앱
    const val SETTING = "Setting" //세부설정
    const val THEME = "Theme"

    fun put(name: String, location: String, data: Any)
    {
        val sharedPreference = context.getSharedPreferences(location, Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()

        when (data)
        {
            is String -> editor.putString(name, data)
            is Int -> editor.putInt(name, data)
            is Boolean -> editor.putBoolean(name, data)
        }

        editor.apply()
    }
    fun getString(name: String, location: String, defaultValue: String = ""): String = context.getSharedPreferences(location, Context.MODE_PRIVATE).getString(name, defaultValue)
    fun getInt(name: String, location: String, defaultValue: Int = 0): Int = context.getSharedPreferences(location, Context.MODE_PRIVATE).getInt(name, defaultValue)
    fun getBoolean(name: String, location: String, defaultValue: Boolean = false): Boolean = context.getSharedPreferences(location, Context.MODE_PRIVATE).getBoolean(name, defaultValue)

    fun remove(location: String)
    {
        val sharedPreference = context.getSharedPreferences(location, Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()

        editor.clear()
        editor.apply()
    }
}