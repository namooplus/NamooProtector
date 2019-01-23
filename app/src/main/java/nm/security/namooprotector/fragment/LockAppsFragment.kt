package nm.security.namooprotector.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_lock_apps.*
import nm.security.namooprotector.R
import nm.security.namooprotector.adapter.LockAppsAdapter
import nm.security.namooprotector.element.AppElement
import nm.security.namooprotector.util.DataUtil

class LockAppsFragment : Fragment()
{
    private var task = LoadAppListTask()

    private val appList = arrayListOf<AppElement>()

    //라이프사이클
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.fragment_lock_apps, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        initClick()
        initRecyclerView()

        task.execute()
    }
    override fun onDestroyView()
    {
        task.cancel(true)

        super.onDestroyView()
    }

    //설정
    private fun initClick()
    {
        //메뉴
        lock_apps_unselect_all_button.setOnClickListener{
            val dialog = AlertDialog.Builder(context)

            with(dialog)
            {
                setTitle(getString(R.string.alert_unselect_all_title))
                setMessage(getString(R.string.alert_unselect_all_message))
                setPositiveButton(getString(R.string.common_ok)) { _, _ ->
                    DataUtil.remove(DataUtil.APPS)

                    task = LoadAppListTask()
                    task.execute()
                }
                setNegativeButton(getString(R.string.common_cancel), null)
                show()
            }

        }
    }
    private fun initRecyclerView()
    {
        lock_apps_list.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        lock_apps_list.setHasFixedSize(true)
    }

    //쓰레드
    private inner class LoadAppListTask : AsyncTask<Void, Void, Void>()
    {
        override fun onPreExecute()
        {
            lock_apps_loading_layout.visibility = View.VISIBLE
            lock_apps_unselect_all_button.hide()

            appList.clear()
        }
        override fun doInBackground(vararg params: Void?): Void?
        {
            //설치된 앱 불러오기
            val installedAppList = context!!.packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
                .filterNot { context!!.packageManager.getLaunchIntentForPackage(it.packageName) == null || it.packageName == "nm.security.namooprotector" || it.packageName == "com.android.vending" || it.packageName == "com.android.settings" }
                .sortedBy { it.loadLabel(context!!.packageManager).toString() }

            installedAppList.forEach {
                appList.add(AppElement(it.loadIcon(context!!.packageManager), it.loadLabel(context!!.packageManager).toString(), it.packageName, DataUtil.getBoolean(it.packageName, DataUtil.APPS)))
            }

            //기본 앱 추가
            appList.add(0, AppElement(ResourcesCompat.getDrawable(resources, R.drawable.vector_recommend, null)!!, getString(R.string.name_default_prevent_apps_uninstalled), "com.android.packageinstaller", DataUtil.getBoolean("com.android.packageinstaller", DataUtil.APPS)))
            appList.add(0, AppElement(ResourcesCompat.getDrawable(resources, R.drawable.vector_recommend, null)!!, getString(R.string.name_default_playstore), "com.android.vending", DataUtil.getBoolean("com.android.vending", DataUtil.APPS)))
            appList.add(0, AppElement(ResourcesCompat.getDrawable(resources, R.drawable.vector_recommend, null)!!, getString(R.string.name_default_settings), "com.android.settings", DataUtil.getBoolean("com.android.settings", DataUtil.APPS)))

            return null
        }
        override fun onPostExecute(result: Void?)
        {
            lock_apps_list.adapter = LockAppsAdapter(context!!, appList)
            {
                it.state = !it.state

                DataUtil.put(it.packageName, DataUtil.APPS, it.state)
                lock_apps_list.adapter!!.notifyDataSetChanged()
            }

            lock_apps_loading_layout.visibility = View.INVISIBLE
            lock_apps_unselect_all_button.show()
        }
    }
}