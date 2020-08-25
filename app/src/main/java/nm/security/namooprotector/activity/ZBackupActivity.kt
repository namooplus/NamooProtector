package nm.security.namooprotector.activity

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import nm.security.namooprotector.util.*
import androidx.appcompat.app.AppCompatActivity
import nm.security.namooprotector.R
import kotlinx.android.synthetic.main.z_activity_backup.*
import kotlinx.android.synthetic.main.z_activity_backup.backup_loading_layout
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.Main
import java.io.File

class ZBackupActivity: AppCompatActivity()
{
    //라이프사이클
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.z_activity_backup)

        ActivityUtil.initFlag(this, true)
        ActivityUtil.initPreviousTitle(this)

        initListener()
    }

    //설정
    private fun initListener()
    {
        backup_label_input.addTextChangedListener(object : TextWatcher
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

    //클릭 이벤트
    fun rangeApps(view: View)
    {
        backup_range_apps.isChecked = !backup_range_apps.isChecked
        checkValid()
    }
    fun rangeSettings(view: View)
    {
        backup_range_settings.isChecked = !backup_range_settings.isChecked
        checkValid()
    }
    fun ok(view: View)
    {
        Toast.makeText(this, "제한된 기능", Toast.LENGTH_SHORT).show()
        return

        //초기 작업
        backup_ok_button.isEnabled = false
        backup_ok_button.visibility = View.GONE
        backup_loading_layout.visibility = View.VISIBLE

        //백업
        CoroutineScope(Default).launch {
            var content = "namupeurotekteo/"

            //데이터 수집 (key:value:type:location)
            if (backup_range_apps.isChecked)
            {
                getSharedPreferences(DataUtil.APPS, Context.MODE_PRIVATE).all.forEach { (key, value) ->
                    content += "$key:$value:Boolean:${DataUtil.APPS}/"
                }
            }
            if (backup_range_settings.isChecked)
            {
                getSharedPreferences(DataUtil.SETTING, Context.MODE_PRIVATE).all.forEach { (key, value) ->
                    content += "$key:$value:Boolean:${DataUtil.SETTING}/"
                }
            }

            //저장
            val file = File(filesDir, "${backup_label_input.text}.npb")

            //동일 이름 방지
            if (file.exists())
            {
                withContext(Main) {
                    Toast.makeText(this@ZBackupActivity, "같은 이름의 백업 파일이 존재합니다. 이름을 다시 입력해주세요.", Toast.LENGTH_SHORT).show()

                    backup_loading_layout.visibility = View.GONE
                }

                cancel()
            }

            file.writeText(content)

            withContext(Main) {
                Toast.makeText(this@ZBackupActivity, "백업에 성공했습니다. ${filesDir.absolutePath}", Toast.LENGTH_SHORT).show()
                finishAfterTransition()
            }
        }
    }

    //메소드
    private fun checkValid()
    {
        if (backup_label_input.text.toString().isNotEmpty() && (backup_range_apps.isChecked || backup_range_settings.isChecked))
        {
            backup_ok_button.isEnabled = true
            backup_ok_button.visibility = View.VISIBLE
        }
        else
        {
            backup_ok_button.isEnabled = false
            backup_ok_button.visibility = View.GONE
        }
    }
}