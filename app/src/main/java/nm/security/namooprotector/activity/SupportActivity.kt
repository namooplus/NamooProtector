package nm.security.namooprotector.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import nm.security.namooprotector.util.*
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.*
import kotlinx.android.synthetic.main.activity_support.*
import kotlinx.coroutines.*
import nm.security.namooprotector.R

class SupportActivity: AppCompatActivity(), PurchasesUpdatedListener
{
    private val billingClient by lazy { BillingClient.newBuilder(this).enablePendingPurchases().setListener(this).build() }
    private var skuDetailsList: List<SkuDetails>? = null

    private var job: Job? = null

    private val REMOVE_AD = "namooprotector_premium_ad_free_0107"
    private val COKE = "namooprotector_donate_coke"
    private val MEAL = "namooprotector_donate_meal"
    private val ECO_BAG = "namooprotector_donate_eco_bag"
    private val COLLECT_FOR_LAPTOP = "namooprotector_donate_collect_for_laptop"

    //라이프사이클
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support)

        ActivityUtil.initFlag(this, true)
        ActivityUtil.initPreviousTitle(this)

        initClient()
        initState()
    }
    override fun onDestroy()
    {
        super.onDestroy()

        job?.cancel()
    }

    //설정
    private fun initClient()
    {
        billingClient.startConnection(object: BillingClientStateListener
        {
            override fun onBillingServiceDisconnected()
            {
                Toast.makeText(this@SupportActivity, getString(R.string.error_billing_get_item), Toast.LENGTH_LONG).show()
            }
            override fun onBillingSetupFinished(result: BillingResult)
            {
                if (result.responseCode ==  BillingClient.BillingResponseCode.OK) job = CoroutineScope(Dispatchers.Main).launch { loadSku() }
                else Toast.makeText(this@SupportActivity, getString(R.string.error_billing_get_item), Toast.LENGTH_LONG).show()
            }
        })
    }
    private suspend fun loadSku()
    {
        val preSkuDetails = SkuDetailsParams.newBuilder()
        preSkuDetails.setSkusList(listOf(REMOVE_AD, COKE, MEAL, ECO_BAG, COLLECT_FOR_LAPTOP)).setType(BillingClient.SkuType.INAPP)

        skuDetailsList = withContext(Dispatchers.IO) { billingClient.querySkuDetails(preSkuDetails.build()).skuDetailsList }
        skuDetailsList?.forEach {
            when (it.sku)
            {
                REMOVE_AD -> support_premium_remove_ad_button.setDescription(it.price)
                COKE -> support_donate_coke_button.setDescription(it.price)
                MEAL -> support_donate_meal_button.setDescription(it.price)
                ECO_BAG -> support_donate_eco_bag_button.setDescription(it.price)
                COLLECT_FOR_LAPTOP -> support_donate_collect_for_laptop_button.setDescription(it.price)
            }
        }
    }
    private fun initState()
    {
        support_premium_remove_ad_button.isChecked = CheckUtil.isAdRemoved
    }

    //클릭 이벤트
    fun purchase(view: View)
    {
        val chosenSkuDetails = skuDetailsList?.find { it.sku == view.tag }

        if (chosenSkuDetails != null)
        {
            val flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(chosenSkuDetails)
                .build()
            billingClient.launchBillingFlow(this, flowParams)
        }
        else Toast.makeText(this@SupportActivity, getString(R.string.error_standard), Toast.LENGTH_LONG).show()
    }

    //메소드
    private fun completePurchase(purchase: Purchase)
    {
        when
        {
            purchase.sku == REMOVE_AD -> // 광고 제거
            {
                job = CoroutineScope(Dispatchers.Main).launch {
                    //구매 확인
                    val preToken = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.purchaseToken)
                    withContext(Dispatchers.IO) { billingClient.acknowledgePurchase(preToken.build()) }

                    //적용
                    CheckUtil.isAdRemoved = true
                    Toast.makeText(this@SupportActivity, getString(R.string.success_remove_ads), Toast.LENGTH_LONG).show()

                    initState()
                }
            }
            purchase.sku.startsWith("namooprotector_donate") -> //기부
            {
                //소비
                val preToken = ConsumeParams.newBuilder().setPurchaseToken(purchase.purchaseToken)
                billingClient.consumeAsync(preToken.build()){ result, _ ->
                    if (result.responseCode == BillingClient.BillingResponseCode.OK)
                        Toast.makeText(this, getString(R.string.success_donate), Toast.LENGTH_LONG).show()

                    else
                        Toast.makeText(this, getString(R.string.error_standard), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    //상속
    override fun onPurchasesUpdated(result: BillingResult, purchases: MutableList<Purchase>?)
    {
        if (result.responseCode == BillingClient.BillingResponseCode.OK && purchases != null)
            purchases.forEach { completePurchase(it) }

        else if (result.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) //구매 확인
        {
            CheckUtil.isAdRemoved = true
            Toast.makeText(this, getString(R.string.error_billing_already_purchased), Toast.LENGTH_LONG).show()

            initState()
        }

        else if (result.responseCode == BillingClient.BillingResponseCode.USER_CANCELED)
            Toast.makeText(this, getString(R.string.error_billing_user_canceled), Toast.LENGTH_SHORT).show()

        else
            Toast.makeText(this, getString(R.string.error_standard), Toast.LENGTH_SHORT).show()
    }
}