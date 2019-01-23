package nm.security.namooprotector.service

object ProtectorState
{
    const val LOCKED = 1
    const val UNLOCKED = 2

    var currentApp = ""
    var currentState = UNLOCKED
}