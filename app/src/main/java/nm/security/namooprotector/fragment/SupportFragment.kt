package nm.security.namooprotector.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.android.billingclient.api.*
import kotlinx.android.synthetic.main.fragment_support.*
import nm.security.namooprotector.R
import nm.security.namooprotector.util.BillingUtil
import nm.security.namooprotector.util.DataUtil
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.Purchase
import nm.security.namooprotector.activity.MainActivity

class SupportFragment : Fragment(), PurchasesUpdatedListener
{
    private val billingClient by lazy { BillingClient.newBuilder(context!!).setListener(this)!!.build() }

    private val skuList = mutableListOf<SkuDetails>()

    //라이프사이클
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.fragment_support, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        initClient()
    }
    override fun onDestroyView()
    {
        billingClient.endConnection()

        super.onDestroyView()
    }

    //설정
    private fun initClient()
    {
        billingClient.startConnection(object : BillingClientStateListener
        {
            override fun onBillingSetupFinished(@BillingClient.BillingResponse billingResponseCode: Int)
            {
                if (billingResponseCode == BillingClient.BillingResponse.OK)
                    initItem()
            }
            override fun onBillingServiceDisconnected()
            {
                Toast.makeText(context, getString(R.string.error_standard), Toast.LENGTH_LONG).show()
            }
        })
    }
    private fun initItem()
    {
        val itemList = ArrayList<String>()
        itemList.add(BillingUtil.REMOVE_AD)
        itemList.add(BillingUtil.COKE)
        itemList.add(BillingUtil.MEAL)
        itemList.add(BillingUtil.ECO_BAG)
        itemList.add(BillingUtil.COLLECT_FOR_LAPTOP)

        val itemCollection = SkuDetailsParams.newBuilder()
        itemCollection.setSkusList(itemList).setType(BillingClient.SkuType.INAPP)

        billingClient.querySkuDetailsAsync(itemCollection.build()) { responseCode, itemDetails ->
            if (responseCode == BillingClient.BillingResponse.OK && itemDetails != null)
            {
                skuList.addAll(itemDetails)

                initSku()
                initClick()
            }
            else
                Toast.makeText(context, getString(nm.security.namooprotector.R.string.error_billing_get_item), Toast.LENGTH_SHORT).show()
        }
    }
    private fun initSku()
    {
        skuList.forEach {
            when (it.sku)
            {
                BillingUtil.REMOVE_AD ->
                {
                    support_purchase_remove_ad_button.setDescription(it.price)
                    support_purchase_remove_ad_button.setChecked(DataUtil.getBoolean("removeAds", DataUtil.NP))
                }
                BillingUtil.COKE -> support_donate_coke_button.setDescription(it.price)
                BillingUtil.MEAL -> support_donate_meal_button.setDescription(it.price)
                BillingUtil.ECO_BAG -> support_donate_eco_bag_button.setDescription(it.price)
                BillingUtil.COLLECT_FOR_LAPTOP -> support_donate_collect_for_laptop_button.setDescription(it.price)
            }
        }
    }
    private fun initClick()
    {
        //구매
        support_purchase_remove_ad_button.setOnClickListener { purchase(BillingUtil.REMOVE_AD) }

        //기부
        support_donate_coke_button.setOnClickListener { purchase(BillingUtil.COKE) }
        support_donate_meal_button.setOnClickListener { purchase(BillingUtil.MEAL) }
        support_donate_eco_bag_button.setOnClickListener { purchase(BillingUtil.ECO_BAG) }
        support_donate_collect_for_laptop_button.setOnClickListener { purchase(BillingUtil.COLLECT_FOR_LAPTOP) }
    }

    //상속
    override fun onPurchasesUpdated(responseCode: Int, purchases: MutableList<Purchase>?)
    {
        if (responseCode == BillingClient.BillingResponse.OK && purchases != null)
            purchases.forEach { successPurchase(it) }

        else if (responseCode == BillingClient.BillingResponse.USER_CANCELED)
            Toast.makeText(context, getString(R.string.error_billing_user_canceled), Toast.LENGTH_SHORT).show()

        else if (responseCode == BillingClient.BillingResponse.ITEM_ALREADY_OWNED)
        {
            //제거
            DataUtil.put("removeAds", DataUtil.NP, true)

            //적용
            (activity as MainActivity).initAd()
            support_purchase_remove_ad_button.setChecked(true)

            Toast.makeText(context, getString(R.string.error_billing_already_purchased), Toast.LENGTH_LONG).show()
        }

        else
            Toast.makeText(context, getString(R.string.error_standard), Toast.LENGTH_SHORT).show()
    }

    //메소드
    private fun purchase(id: String)
    {
        val flowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(skuList.find { it.sku == id })
            .build()

        billingClient.launchBillingFlow(activity, flowParams)
    }
    private fun successPurchase(item: Purchase)
    {
        when
        {
            BillingUtil.isPurchaseId(item.sku) -> //구매
            {
                if (item.sku == BillingUtil.REMOVE_AD) //광고 제거
                {
                    //제거
                    DataUtil.put("removeAds", DataUtil.NP, true)

                    //적용
                    (activity as MainActivity).initAd()
                    support_purchase_remove_ad_button.setChecked(true)

                    //알림
                    Toast.makeText(context, getString(R.string.success_remove_ad), Toast.LENGTH_LONG).show()
                }
            }
            BillingUtil.isDonateId(item.sku) -> //기부
            {
                billingClient.consumeAsync(item.purchaseToken) { responseCode, _ ->
                    if (responseCode == BillingClient.BillingResponse.OK)
                        Toast.makeText(context, getString(R.string.success_donate), Toast.LENGTH_LONG).show()

                    else
                        Toast.makeText(context, getString(R.string.error_standard), Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
