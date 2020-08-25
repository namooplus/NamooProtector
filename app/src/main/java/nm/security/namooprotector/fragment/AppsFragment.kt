package nm.security.namooprotector.fragment

import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_pin.*
import kotlinx.android.synthetic.main.fragment_apps.*
import kotlinx.coroutines.*
import nm.security.namooprotector.R
import nm.security.namooprotector.adapter.AppsAdapter
import nm.security.namooprotector.bundle.AppBundle
import nm.security.namooprotector.util.DataUtil
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

class AppsFragment : Fragment()
{
    private var job: Job? = null

    //라이프사이클
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.fragment_apps, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        initClick()
        initRecyclerView()
        initListener()

        loadApps()
    }
    override fun onDestroyView()
    {
        super.onDestroyView()

        job?.cancel()
    }

    //설정
    private fun initClick()
    {
        //메뉴
        apps_toggle_button.setOnClickListener{
            val dialog = AlertDialog.Builder(context)

            with(dialog)
            {
                setMessage(getString(R.string.alert_unselect_all_apps))
                setPositiveButton(getString(R.string.common_ok)) { _, _ ->
                    DataUtil.remove(DataUtil.APPS)
                    loadApps()
                }
                setNegativeButton(getString(R.string.common_cancel), null)
                show()
            }

        }
    }
    private fun initRecyclerView()
    {
        apps_list.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        apps_list.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        apps_list.setHasFixedSize(true)
    }
    private fun initListener()
    {
        apps_search_input.addTextChangedListener(object : TextWatcher
        {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int)
            {
                (apps_list.adapter as AppsAdapter).filter.filter(s)
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int)
            {

            }
            override fun afterTextChanged(s: Editable)
            {

            }
        })
    }

    //메소드
    private fun loadApps()
    {
        job = CoroutineScope(Dispatchers.Main).launch{
            try
            {
                //준비
                apps_loading_layout.visibility = View.VISIBLE
                apps_search_container.visibility = View.GONE
                apps_toggle_button.visibility = View.GONE

                val appList = arrayListOf<AppBundle>()

                //로드
                withContext(Dispatchers.Default)
                {
                    //설치된 앱 불러오기
                    val installedAppList = context!!.packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
                        .filterNot { context!!.packageManager.getLaunchIntentForPackage(it.packageName!!) == null || it.packageName == "nm.security.namooprotector" || it.packageName == "com.android.vending" || it.packageName == "com.android.settings" }
                        .sortedBy { it.loadLabel(context!!.packageManager).toString() }

                    //추가
                    installedAppList.forEach { appList.add(AppBundle(it.loadIcon(context!!.packageManager), it.loadLabel(context!!.packageManager).toString(), it.packageName, DataUtil.getBoolean(it.packageName, DataUtil.APPS))) }

                    //기본 앱 추가
                    appList.add(0, AppBundle(ResourcesCompat.getDrawable(resources, R.drawable.vector_recommend, null)!!, getString(R.string.name_default_prevent_apps_uninstalled), "com.android.packageinstaller", DataUtil.getBoolean("com.android.packageinstaller", DataUtil.APPS)))
                    appList.add(0, AppBundle(ResourcesCompat.getDrawable(resources, R.drawable.vector_recommend, null)!!, getString(R.string.name_default_play_store), "com.android.vending", DataUtil.getBoolean("com.android.vending", DataUtil.APPS)))
                    appList.add(0, AppBundle(ResourcesCompat.getDrawable(resources, R.drawable.vector_recommend, null)!!, getString(R.string.name_default_settings), "com.android.settings", DataUtil.getBoolean("com.android.settings", DataUtil.APPS)))
                }

                //적용
                apps_list.adapter = AppsAdapter(context!!, appList)
                {
                    it.state = !it.state

                    DataUtil.put(it.packageName, DataUtil.APPS, it.state)
                    apps_list.adapter!!.notifyDataSetChanged()
                }

                apps_loading_layout.visibility = View.GONE
                apps_search_container.visibility = View.VISIBLE
                apps_toggle_button.visibility = View.VISIBLE
            }
            catch (e: Exception)
            {
                Log.e("AppsFragment", "Process cancelled")
            }
        }
    }
}