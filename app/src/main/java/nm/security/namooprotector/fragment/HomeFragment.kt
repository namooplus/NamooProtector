package nm.security.namooprotector.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.psdev.licensesdialog.LicensesDialog
import kotlinx.android.synthetic.main.fragment_home.*
import nm.security.namooprotector.R
import nm.security.namooprotector.activity.AboutActivity
import nm.security.namooprotector.activity.SupportActivity
import nm.security.namooprotector.util.ActivityUtil
import nm.security.namooprotector.util.CheckUtil
import nm.security.namooprotector.util.ResourceUtil
import nm.security.namooprotector.util.ServiceUtil

class HomeFragment : Fragment()
{
    //라이프사이클
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        initClick()
    }
    override fun onResume()
    {
        super.onResume()

        initState()
    }

    //설정
    private fun initClick()
    {
        //활성화
        home_activator_background.setOnClickListener {
            ServiceUtil.runService(!CheckUtil.isServiceRunning)
            initState()
        }

        //더보기
        home_more_info.setOnClickListener { ActivityUtil.startActivityWithAnimation(activity!!, AboutActivity::class.java) }
        home_more_support.setOnClickListener { ActivityUtil.startActivityWithAnimation(activity!!, SupportActivity::class.java) }
    }
    private fun initState()
    {
        //활성화
        if (CheckUtil.isServiceRunning)
        {
            home_activator_background.setCardBackgroundColor(resources.getColor(R.color.positive_color, null))
            home_activator_state_text.text = ResourceUtil.getString(R.string.alert_service_on)
            home_activator_state_image.setImageResource(R.drawable.vector_lock)
        }
        else
        {
            home_activator_background.setCardBackgroundColor(resources.getColor(R.color.negative_color, null))
            home_activator_state_text.text = ResourceUtil.getString(R.string.alert_service_off)
            home_activator_state_image.setImageResource(R.drawable.vector_unlock)
        }
    }
}