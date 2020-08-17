package nm.security.namooprotector.util

import android.text.TextUtils

object SettingsUtil
{
    //잠금 방식
    var lockType: String?
        set(value) = DataUtil.put("type", DataUtil.LOCK, value!!)
        get() = DataUtil.getString("type", DataUtil.LOCK).validLockType() //신뢰 가능

    var pin: String?
        set(value) = DataUtil.put("pin", DataUtil.LOCK, value!!)
        get() = DataUtil.getString("pin", DataUtil.LOCK).validPin() //신뢰 가능

    var pattern: String?
        set(value) = DataUtil.put("pattern", DataUtil.LOCK, value!!)
        get() = DataUtil.getString("pattern", DataUtil.LOCK).validPattern() //신뢰 가능

    var fingerprint: Boolean
        set(value) = DataUtil.put("fingerprint", DataUtil.LOCK, value)
        get() = DataUtil.getBoolean("fingerprint", DataUtil.LOCK)

    //PIN 추가 설정
    var clickHaptic: Boolean
        set(value) = DataUtil.put("clickHaptic", DataUtil.SETTING, value)
        get() = DataUtil.getBoolean("clickHaptic", DataUtil.SETTING)

    var hideClick: Boolean
        set(value) = DataUtil.put("hideClick", DataUtil.SETTING, value)
        get() = DataUtil.getBoolean("hideClick", DataUtil.SETTING)

    var quickUnlock: Boolean
        set(value) = DataUtil.put("quickUnlock", DataUtil.SETTING, value)
        get() = DataUtil.getBoolean("quickUnlock", DataUtil.SETTING)

    var rearrangeKey: Boolean
        set(value) = DataUtil.put("rearrangeKey", DataUtil.SETTING, value)
        get() = DataUtil.getBoolean("rearrangeKey", DataUtil.SETTING)

    var lightKey: Boolean
        set(value) = DataUtil.put("lightKey", DataUtil.SETTING, value)
        get() = DataUtil.getBoolean("lightKey", DataUtil.SETTING)

    //패턴 추가 설정
    var drawHaptic: Boolean
        set(value) = DataUtil.put("drawHaptic", DataUtil.SETTING, value)
        get() = DataUtil.getBoolean("drawHaptic", DataUtil.SETTING)

    var hideDraw: Boolean
        set(value) = DataUtil.put("hideDraw", DataUtil.SETTING, value)
        get() = DataUtil.getBoolean("hideDraw", DataUtil.SETTING)

    var lightDot: Boolean
        set(value) = DataUtil.put("lightDot", DataUtil.SETTING, value)
        get() = DataUtil.getBoolean("lightDot", DataUtil.SETTING)

    //보안
    var darkLockscreen: Boolean
        set(value) = DataUtil.put("darkLockscreen", DataUtil.SETTING, value)
        get() = DataUtil.getBoolean("darkLockscreen", DataUtil.SETTING)

    var watchFail: Boolean
        set(value) = DataUtil.put("watchFail", DataUtil.SETTING, value)
        get() = DataUtil.getBoolean("watchFail", DataUtil.SETTING)

    var cover: Boolean
        set(value) = DataUtil.put("cover", DataUtil.SETTING, value)
        get() = DataUtil.getBoolean("cover", DataUtil.SETTING)

    //편의 기능
    var protectionNotification: Boolean
        set(value) = DataUtil.put("protectionNotification", DataUtil.SETTING, value)
        get() = DataUtil.getBoolean("protectionNotification", DataUtil.SETTING)

    var lockDelay: Int
        set(value) = DataUtil.put("lockDelay", DataUtil.SETTING, value)
        get() = DataUtil.getInt("lockDelay", DataUtil.SETTING).validLockDelay()


    //내부 메소드
    private fun String.validLockType(): String?
    {
        return if ((this == "pin" && pin != null) || (this == "pattern" && pattern != null)) this else null
    }
    private fun String.validPin(): String?
    {
        return if (this.length in 4..12 && TextUtils.isDigitsOnly(this)) this else null
    }
    private fun String.validPattern(): String?
    {
        return if (this.length in 2..9 && TextUtils.isDigitsOnly(this)) this else null
    }
    private fun Int.validLockDelay(): Int
    {
        return if (this in 1..60) this else 0
    }
}
