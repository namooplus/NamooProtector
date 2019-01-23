package nm.security.namooprotector.fragment

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.pes.androidmaterialcolorpickerdialog.ColorPicker
import kotlinx.android.synthetic.main.fragment_theme.*
import nm.security.namooprotector.util.ThemeUtil
import nm.security.namooprotector.util.ConvertUtil
import nm.security.namooprotector.util.DataUtil
import androidx.appcompat.widget.PopupMenu
import nm.security.namooprotector.R
import nm.security.namooprotector.activity.ThemePreviewActivity

class ThemeFragment : Fragment()
{
    //라이프사이클
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(nm.security.namooprotector.R.layout.fragment_theme, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        initClick()
        initState()
    }

    //설정
    private fun initClick()
    {
        //메뉴
        theme_more_button.setOnClickListener { view ->

            val popup = PopupMenu(context!!, view)
            popup.menu.add(getString(R.string.name_preview))
            popup.menu.add(getString(R.string.name_reset))
            popup.setOnMenuItemClickListener {
                when (it.title.toString())
                {
                    getString(R.string.name_preview) ->
                    {
                        startActivity(Intent(activity, ThemePreviewActivity::class.java))
                    }
                    getString(R.string.name_reset) ->
                    {
                        val dialog = AlertDialog.Builder(context)

                        with(dialog)
                        {
                            setTitle(getString(R.string.alert_reset_theme_title))
                            setMessage(getString(R.string.alert_reset_theme_message))
                            setPositiveButton(getString(R.string.common_ok)) { _, _ ->
                                DataUtil.remove(DataUtil.THEME)
                                initState()
                            }
                            setNegativeButton(getString(R.string.common_cancel), null)
                            show()
                        }
                    }
                }

                true
            }
            popup.show()
        }

        //에디터
        theme_background_base_button.setOnClickListener { generateColorPicker("baseLayoutColor", ThemeUtil.getColorInt("baseLayoutColor", "#ff52cc9d")) }
        theme_background_bottom_button.setOnClickListener { generateColorPicker("bottomLayoutColor", ThemeUtil.getColorInt("bottomLayoutColor", "#20000000")) }
        theme_text_color_pin_indicator_button.setOnClickListener { generateColorPicker("pinIndicatorColor", ThemeUtil.getColorInt("pinIndicatorColor", "#ffffffff")) }
        theme_text_color_key_button.setOnClickListener { generateColorPicker("keyColor", ThemeUtil.getColorInt("keyColor", "#ffffffff")) }
        theme_visibility_top_layout_button.setOnClickListener { changeState("hideTopLayout") }
    }
    private fun initState()
    {
        theme_background_base_button.setTint(ThemeUtil.getColorString("baseLayoutColor", "#ff52cc9d"))
        theme_background_bottom_button.setTint(ThemeUtil.getColorString("bottomLayoutColor", "#20000000"))
        theme_text_color_pin_indicator_button.setTint(ThemeUtil.getColorString("pinIndicatorColor", "#ffffffff"))
        theme_text_color_key_button.setTint(ThemeUtil.getColorString("keyColor", "#ffffffff"))
        theme_visibility_top_layout_button.setChecked(DataUtil.getBoolean("hideTopLayout", DataUtil.SETTING))
    }

    //메소드
    private fun generateColorPicker(name: String, currentColor: Int)
    {
        val colorPicker = ColorPicker(activity, Color.alpha(currentColor), Color.red(currentColor), Color.green(currentColor), Color.blue(currentColor))
        colorPicker.enableAutoClose()
        colorPicker.setCallback{ color ->
            DataUtil.put(name, DataUtil.THEME, ConvertUtil.intColorToStringColor(color))
            initState()
        }
        colorPicker.show()
    }
    private fun changeState(name: String)
    {
        DataUtil.put(name, DataUtil.SETTING, !DataUtil.getBoolean(name, DataUtil.SETTING))

        initState()
    }
}