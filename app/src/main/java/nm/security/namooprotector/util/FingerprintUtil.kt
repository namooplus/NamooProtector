package nm.security.namooprotector.util

import co.infinum.goldfinger.Goldfinger

object FingerprintUtil
{
    const val AVAILABLE = 100
    const val NO_HARDWARE = 10
    const val NO_ENROLLED_FINGERPRINT = 11
    const val NO_PASSWORD = 12

    val isFingerprintActivated: Boolean
        get() = DataUtil.getBoolean("fingerprint", DataUtil.LOCK)

    fun isSupportFingerprint(fingerprintManager: Goldfinger): Int
    {
        return if (!fingerprintManager.hasFingerprintHardware())
            NO_HARDWARE

        else if (!fingerprintManager.hasEnrolledFingerprint())
            NO_ENROLLED_FINGERPRINT

        else if (PasswordUtil.type == "")
            NO_PASSWORD

        else
            AVAILABLE
    }
}