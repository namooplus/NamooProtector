package nm.security.namooprotector.util

import android.text.TextUtils

object PasswordUtil
{
    //불러오기
    val type: String
        get()
        {
            return when (DataUtil.getString("type", DataUtil.LOCK))
            {
                "pin" -> if (isValidForPin(pin)) "pin" else ""
                "pattern" -> if (isValidForPattern(pattern)) "pattern" else ""
                else -> ""
            }
        }

    val pin: String
        get() = DataUtil.getString("pin", DataUtil.LOCK)

    val pattern: String
        get() = DataUtil.getString("pattern", DataUtil.LOCK)

    fun isValidForPin(pin: String): Boolean = pin.length in 4..12 && !pin.contains(" ") && TextUtils.isDigitsOnly(pin)
    fun isValidForPattern(pattern: String): Boolean = pattern.length in 2..9 && !pattern.contains(" ") && TextUtils.isDigitsOnly(pattern)
}