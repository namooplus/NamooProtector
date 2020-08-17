package nm.security.namooprotector.service

object ProtectorServiceHelper
{
    //잠금 해제가 인증된 앱
    private val authorizedApps = mutableListOf<AuthorizeBundle>()

    fun addAuthorizedApp(packageName: String, authorizedTime: Long)
    {
        if (authorizedTime == 0L) authorizedApps.add(AuthorizeBundle(packageName, 0L))
        else authorizedApps.add(AuthorizeBundle(packageName, System.currentTimeMillis() + authorizedTime * 1000))
    }
    fun cleanTemporaryAuthorizedApps()
    {
        authorizedApps.removeAll { it.authorizedTime == 0L }
    }
    fun updateAuthorizedApps()
    {
        authorizedApps.removeAll { it.authorizedTime > 0L && it.authorizedTime < System.currentTimeMillis() }
    }
    fun isAuthorized(packageName: String): Boolean
    {
        return authorizedApps.find { it.packageName == packageName } != null
    }
    fun resetAuthorizedApps()
    {
        authorizedApps.clear()
    }

    data class AuthorizeBundle(val packageName: String, var authorizedTime: Long)
}