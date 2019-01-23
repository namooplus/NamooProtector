package nm.security.namooprotector.util

object BillingUtil
{
    //종류
    const val REMOVE_AD = "namooprotector_premium_ad_free_0107"

    const val COKE = "namooprotector_donate_coke"
    const val MEAL = "namooprotector_donate_meal"
    const val ECO_BAG = "namooprotector_donate_eco_bag"
    const val COLLECT_FOR_LAPTOP = "namooprotector_donate_collect_for_laptop"

    fun isPurchaseId(id: String): Boolean
    {
        return id.startsWith("namooprotector_premium_")
    }
    fun isDonateId(id: String): Boolean
    {
        return id.startsWith("namooprotector_donate_")
    }
}