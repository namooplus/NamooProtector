package nm.security.namooprotector.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.pvryan.easycrypt.ECResultListener
import com.pvryan.easycrypt.symmetric.ECSymmetric
import kotlinx.android.synthetic.main.fragment_backup.*
import nm.security.namooprotector.BuildConfig
import nm.security.namooprotector.R
import nm.security.namooprotector.activity.MainActivity
import nm.security.namooprotector.adapter.RestoreAdapter
import nm.security.namooprotector.element.BackupElement
import nm.security.namooprotector.util.DataUtil
import org.jetbrains.anko.runOnUiThread
import java.io.File
import java.util.*

class BackupFragment : Fragment()
{
    private var task = LoadBackupListTask()

    private val backupList = arrayListOf<BackupElement>()

    //라이프사이클
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.fragment_backup, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        initClick()
        initListener()
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
        //백업
        backup_backup_save_button.setOnClickListener { backup() }
    }
    private fun initListener()
    {
        backup_backup_save_button.isEnabled = false
        backup_backup_label_input.addTextChangedListener(object : TextWatcher
        {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int)
            {
                checkValid()
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int)
            {

            }
            override fun afterTextChanged(s: Editable)
            {

            }
        })
    }
    private fun initRecyclerView()
    {
        backup_restore_list.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        backup_restore_list.setHasFixedSize(true)
    }

    //메소드
    private fun checkValid()
    {
        backup_backup_save_button.isEnabled = backup_backup_label_input.text.toString().isNotEmpty()
    }
    private fun makeDirectory(path: String)
    {
        val directory = File(path)

        if (!directory.exists())
            directory.mkdirs()
    }
    private fun backup()
    {
        //이름/값/타입/위치
        var content = ""

        if (!backup_backup_lock_apps_checker.isChecked && !backup_backup_password_checker.isChecked && !backup_backup_settings_checker.isChecked)
        {
            Toast.makeText(context, getString(R.string.error_backup_range_not_set), Toast.LENGTH_SHORT).show()
            return
        }

        //초기 작업
        backup_backup_label_input.isEnabled = false
        backup_loading_layout.visibility = View.VISIBLE

        //데이터 수집
        if (backup_backup_lock_apps_checker.isChecked) //잠금 앱
        {
            content += "%apps#"
            context!!.getSharedPreferences(DataUtil.APPS, Context.MODE_PRIVATE).all
                .forEach { (key, value) -> content += "$key/${value.toString()}/Boolean/Apps#" }
        }
        if (backup_backup_password_checker.isChecked) //잠금 설정
        {
            content += "%lock#"
            context!!.getSharedPreferences(DataUtil.LOCK, Context.MODE_PRIVATE).all
                .filterNot { (key, _) -> key == "fingerprint" }
                .forEach { (key, value) -> content += "$key/${value.toString()}/${
                when(value)
                {
                    is String -> "String"
                    is Int -> "Int"
                    is Boolean -> "Boolean"
                    else -> "any"
                }
                }/Lock#" }
        }
        if (backup_backup_settings_checker.isChecked) //설정
        {
            content += "%setting#"
            context!!.getSharedPreferences(DataUtil.SETTING, Context.MODE_PRIVATE).all
                .forEach { (key, value) -> content += "$key/${value.toString()}/${
                when(value)
                {
                    is String -> "String"
                    is Int -> "Int"
                    is Boolean -> "Boolean"
                    else -> "any"
                }
                }/Setting#" }
        }

        //데이터 암호화
        ECSymmetric().encrypt (content, BuildConfig.BackupPassword,
            object : ECResultListener
            {
                override fun <T> onSuccess(result: T)
                {
                    //데이터 저장
                    val file = File("${Environment.getExternalStorageDirectory()}/NamooProtector/BackUp/${backup_backup_label_input.text}.npb")

                    if (file.exists())
                    {
                        context!!.runOnUiThread {
                            Toast.makeText(context, getString(R.string.error_backup_same_name), Toast.LENGTH_SHORT).show()

                            backup_backup_label_input.isEnabled = true
                            backup_loading_layout.visibility = View.INVISIBLE
                        }
                        return
                    }

                    file.writeText(result.toString())

                    context!!.runOnUiThread {
                        //백업 목록 새로고침
                        task = LoadBackupListTask()
                        task.execute()

                        Toast.makeText(context, getString(R.string.success_backup), Toast.LENGTH_SHORT).show()

                        backup_backup_label_input.isEnabled = true
                        backup_loading_layout.visibility = View.INVISIBLE
                    }
                }
                override fun onFailure(message: String, e: Exception)
                {
                    context!!.runOnUiThread {
                        Toast.makeText(context, getString(R.string.error_backup) + "\nException : $message", Toast.LENGTH_SHORT).show()

                        backup_backup_label_input.isEnabled = true
                        backup_loading_layout.visibility = View.INVISIBLE
                    }
                }
            })
    }
    private fun restore(content: String)
    {
        //초기화
        if (content.contains("%apps"))
            DataUtil.remove(DataUtil.APPS)

        if (content.contains("%lock"))
            DataUtil.remove(DataUtil.LOCK)

        if (content.contains("%setting"))
            DataUtil.remove(DataUtil.SETTING)

        //복원
        content.split("#")
            .filter { it.contains("/") }
            .forEach{
                val element = it.split("/")
                DataUtil.put(element[0], element[3], when(element[2])
                {
                    "String" -> element[1]
                    "Int" -> element[1].toInt()
                    "Boolean" -> element[1].toBoolean()
                    else -> ""
                })
            }

        //서비스 재시작
        (activity as MainActivity).init()

        //알림
        Toast.makeText(context, getString(R.string.success_restore), Toast.LENGTH_SHORT).show()
    }

    //쓰레드
    private inner class LoadBackupListTask : AsyncTask<Void, Void, Void>()
    {
        override fun onPreExecute()
        {
            backupList.clear()

            makeDirectory("${Environment.getExternalStorageDirectory()}/NamooProtector/BackUp/")
        }
        override fun doInBackground(vararg params: Void?): Void?
        {
            //저장된 백업 불러오기
            val savedBackupList = File("${Environment.getExternalStorageDirectory()}/NamooProtector/BackUp").listFiles()
                .filter { it != null && it.length() > 0 && !it.isHidden && it.isFile && it.name.endsWith(".npb") }
                .sortedBy { it.lastModified() }

            savedBackupList.forEach {
                backupList.add(BackupElement(it.name, DateFormat.format("yyyy-MM-dd HH:mm", Date(it.lastModified())).toString()))
            }

            return null
        }
        override fun onPostExecute(result: Void?)
        {
            backup_restore_list.adapter = RestoreAdapter(context!!, backupList)
            {
                //초기 작업
                backup_loading_layout.visibility = View.VISIBLE

                try
                {
                    val content = File("${Environment.getExternalStorageDirectory()}/NamooProtector/BackUp/${it.label}").readText()

                    ECSymmetric().decrypt (content, BuildConfig.BackupPassword,
                        object : ECResultListener
                        {
                            override fun <T> onSuccess(result: T)
                            {
                                context!!.runOnUiThread {
                                    restore(result.toString())
                                    backup_loading_layout.visibility = View.INVISIBLE
                                }
                            }
                            override fun onFailure(message: String, e: Exception)
                            {
                                context!!.runOnUiThread {
                                    Toast.makeText(context, getString(R.string.error_restore) + "\nExecption : $message", Toast.LENGTH_SHORT).show()
                                    backup_loading_layout.visibility = View.INVISIBLE
                                }
                            }
                        })
                }
                catch (e: java.lang.Exception)
                {
                    Toast.makeText(context, getString(R.string.error_restore) + "\nException : ${e.message}", Toast.LENGTH_SHORT).show()
                    backup_loading_layout.visibility = View.INVISIBLE
                }
            }
        }
    }
}