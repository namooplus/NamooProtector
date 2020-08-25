package nm.security.namooprotector.fragment

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_theme.*
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.forEach
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import kotlinx.android.synthetic.main.activity_pattern.*
import kotlinx.android.synthetic.main.lockscreen.*
import kotlinx.android.synthetic.main.view_size_picker.view.*
import nm.security.namooprotector.R
import nm.security.namooprotector.util.AnimationUtil
import nm.security.namooprotector.util.AnimationUtil.rotateY
import nm.security.namooprotector.util.AnimationUtil.scale
import nm.security.namooprotector.util.AnimationUtil.translateX
import nm.security.namooprotector.util.AnimationUtil.translateY
import nm.security.namooprotector.util.ResourceUtil
import nm.security.namooprotector.util.SettingsUtil
import nm.security.namooprotector.util.ThemeUtil
import kotlin.reflect.KMutableProperty0

class ThemeFragment : Fragment()
{
    private val themeElement by lazy { arrayOf(lockscreen_base_layout, lockscreen_top_layout, lockscreen_bottom_layout,
        lockscreen_icon_container, lockscreen_pin_indicator, lockscreen_key_container, lockscreen_pattern_container) }

    //라이프사이클
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.fragment_theme, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        initUI()
        initAnimation()
        initClick()
    }

    //설정
    private fun initUI()
    {
        //잠금 방식
        when (SettingsUtil.lockType)
        {
            "pin" ->
            {
                lockscreen_pin_indicator.visibility = View.VISIBLE
                lockscreen_key_container.visibility = View.VISIBLE
                lockscreen_pattern_container.visibility = View.GONE

                lockscreen_pin_indicator.text = "••••••"
            }
            "pattern" ->
            {
                lockscreen_pin_indicator.visibility = View.GONE
                lockscreen_key_container.visibility = View.GONE
                lockscreen_pattern_container.visibility = View.VISIBLE
            }
            else ->
            {
                lockscreen_pin_indicator.visibility = View.GONE
                lockscreen_key_container.visibility = View.GONE
                lockscreen_pattern_container.visibility = View.GONE
            }
        }

        //테마 적용
        ThemeUtil.apply(this)

        //에디터 모드
        themeElement.forEach {
            it.isClickable = true
            it.isFocusable = true
            it.foreground = ResourcesCompat.getDrawable(resources, R.drawable.foreground_border_clickable, null)
        }

        //키 and 패턴 이벤트 차단
        arrayOf(lockscreen_key_1, lockscreen_key_2, lockscreen_key_3, lockscreen_key_4, lockscreen_key_5, lockscreen_key_6,
            lockscreen_key_7, lockscreen_key_8, lockscreen_key_9, lockscreen_key_ok, lockscreen_key_0, lockscreen_key_clear).forEach {
            it.isClickable = false
            it.isFocusable = false
            it.background = null
        }
        lockscreen_pattern_view.isInputEnabled = false
    }
    private fun initAnimation()
    {
        lockscreen_base_layout.scaleX = 0.6f
        lockscreen_base_layout.scaleY = 0.6f

        lockscreen_top_layout.translateX(80f, AnimationUtil.DEFAULT_DURATION, 0)
        lockscreen_top_layout.translateY(-80f, AnimationUtil.DEFAULT_DURATION, 0)
        lockscreen_bottom_layout.translateX(80f, AnimationUtil.DEFAULT_DURATION, 0)
        lockscreen_bottom_layout.translateY(80f, AnimationUtil.DEFAULT_DURATION, 0)
        theme_lockscreen_container.rotateY(10f, AnimationUtil.DEFAULT_DURATION, 0)
    }
    private fun initClick()
    {
        //테마 요소
        theme_base.setOnClickListener { changeSelectedElement(null) }
        themeElement.forEach { element -> element.setOnClickListener { changeSelectedElement(it) } }

        //테마 메뉴
        theme_menu_color.setOnClickListener {
            when
            {
                lockscreen_base_layout.isSelected -> showColorPicker(ResourceUtil.getString(R.string.name_element_base_layout_color), ThemeUtil::baseLayoutColor)
                lockscreen_top_layout.isSelected -> showColorPicker(ResourceUtil.getString(R.string.name_element_top_layout_color), ThemeUtil::topLayoutColor)
                lockscreen_bottom_layout.isSelected -> showColorPicker(ResourceUtil.getString(R.string.name_element_bottom_layout_color), ThemeUtil::bottomLayoutColor)
                lockscreen_pin_indicator.isSelected -> showColorPicker(ResourceUtil.getString(R.string.name_element_pin_indicator_color), ThemeUtil::pinIndicatorTextColor)
                lockscreen_key_container.isSelected -> showColorPicker(ResourceUtil.getString(R.string.name_element_key_color), ThemeUtil::keyTextColor)
                lockscreen_pattern_container.isSelected -> showColorPicker(ResourceUtil.getString(R.string.name_element_pattern_color), ThemeUtil::patternColor)
                else -> Toast.makeText(context, ResourceUtil.getString(R.string.error_invalid_access), Toast.LENGTH_SHORT).show()
            }
        }
        theme_menu_size.setOnClickListener {
            when
            {
                lockscreen_icon_container.isSelected -> showSizePicker(ResourceUtil.getString(R.string.name_element_icon_size), ThemeUtil::iconContainerSize, 120, 60)
                lockscreen_pin_indicator.isSelected -> showSizePicker(ResourceUtil.getString(R.string.name_element_pin_indicator_size), ThemeUtil::pinIndicatorTextSize, 45, 15)
                lockscreen_key_container.isSelected -> showSizePicker(ResourceUtil.getString(R.string.name_element_key_size), ThemeUtil::keyTextSize, 40, 10)
                else -> Toast.makeText(context, ResourceUtil.getString(R.string.error_invalid_access), Toast.LENGTH_SHORT).show()
            }
        }
        theme_menu_visibility.setOnClickListener {
            when
            {
                lockscreen_icon_container.isSelected -> showStatePicker(ResourceUtil.getString(R.string.name_element_icon_background_visibility), ThemeUtil::iconContainerVisibility)
                lockscreen_pin_indicator.isSelected -> showStatePicker(ResourceUtil.getString(R.string.name_element_pin_indicator_visibility), ThemeUtil::pinIndicatorVisibility)
                else -> Toast.makeText(context, ResourceUtil.getString(R.string.error_invalid_access), Toast.LENGTH_SHORT).show()
            }
        }
    }

    //메소드
    private fun changeSelectedElement(selectedView: View?)
    {
        themeElement.forEach { it.isSelected = false }

        if (selectedView != null)
        {
            selectedView.isSelected = true

            //자식 뷰 propagate 방지
            if (selectedView is ViewGroup) selectedView.forEach { it.isSelected = false }

            //테마 요소 표시
            theme_element_indicator.visibility = View.VISIBLE
            theme_element_indicator.text = when (selectedView)
            {
                lockscreen_base_layout ->  String.format(ResourceUtil.getString(R.string.alert_element_selected), ResourceUtil.getString(R.string.name_element_base_layout))
                lockscreen_top_layout -> String.format(ResourceUtil.getString(R.string.alert_element_selected), ResourceUtil.getString(R.string.name_element_top_layout))
                lockscreen_bottom_layout -> String.format(ResourceUtil.getString(R.string.alert_element_selected), ResourceUtil.getString(R.string.name_element_bottom_layout))
                lockscreen_icon_container -> String.format(ResourceUtil.getString(R.string.alert_element_selected), ResourceUtil.getString(R.string.name_element_icon))
                lockscreen_pin_indicator -> String.format(ResourceUtil.getString(R.string.alert_element_selected), ResourceUtil.getString(R.string.name_element_pin_indicator))
                lockscreen_key_container -> String.format(ResourceUtil.getString(R.string.alert_element_selected), ResourceUtil.getString(R.string.name_element_key))
                lockscreen_pattern_container -> String.format(ResourceUtil.getString(R.string.alert_element_selected), ResourceUtil.getString(R.string.name_element_pattern))
                else -> ResourceUtil.getString(R.string.error_invalid_access)
            }
        }
        else theme_element_indicator.visibility = View.GONE

        changeMenu()
    }
    private fun changeMenu()
    {
        when {
            lockscreen_base_layout.isSelected || lockscreen_top_layout.isSelected || lockscreen_bottom_layout.isSelected
                    || lockscreen_pattern_container.isSelected ->
            {
                theme_menu.visibility = View.VISIBLE
                theme_menu_color.visibility = View.VISIBLE
                theme_menu_size.visibility = View.GONE
                theme_menu_visibility.visibility = View.GONE
            }
            lockscreen_icon_container.isSelected ->
            {
                theme_menu.visibility = View.VISIBLE
                theme_menu_color.visibility = View.GONE
                theme_menu_size.visibility = View.VISIBLE
                theme_menu_visibility.visibility = View.VISIBLE
            }
            lockscreen_pin_indicator.isSelected ->
            {
                theme_menu.visibility = View.VISIBLE
                theme_menu_color.visibility = View.VISIBLE
                theme_menu_size.visibility = View.VISIBLE
                theme_menu_visibility.visibility = View.VISIBLE
            }
            lockscreen_key_container.isSelected ->
            {
                theme_menu.visibility = View.VISIBLE
                theme_menu_color.visibility = View.VISIBLE
                theme_menu_size.visibility = View.VISIBLE
                theme_menu_visibility.visibility = View.GONE
            }
            else -> theme_menu.visibility = View.GONE
        }
    }

    private fun showColorPicker(title: String, property: KMutableProperty0<String>)
    {
        with(ColorPickerDialog.Builder(context))
        {
            setTitle(title)
            setPositiveButton(ResourceUtil.getString(R.string.common_choose), ColorEnvelopeListener { colorEnvelope: ColorEnvelope, _ ->
                property.set("#" + colorEnvelope.hexCode)
                initUI()
            })
            setNeutralButton(ResourceUtil.getString(R.string.common_default)) { _, _ ->
                property.set("")
                initUI()
            }
            setNegativeButton(ResourceUtil.getString(R.string.common_cancel), null)
            attachAlphaSlideBar(true)
            attachBrightnessSlideBar(true)
            show()
        }
    }
    private fun showSizePicker(title: String, property: KMutableProperty0<Float>, maxValue: Int, minValue: Int)
    {
        val dialog = AlertDialog.Builder(context)

        val view = layoutInflater.inflate(R.layout.view_size_picker, null)
        view.view_size_picker_seek_bar.max = maxValue - minValue
        view.view_size_picker_seek_bar.progress = property.get().toInt() - minValue
        view.view_size_picker_seek_indicator.text = property.get().toInt().toString()
        view.view_size_picker_seek_bar.setOnSeekBarChangeListener(object: OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean)
            {

            }
            override fun onStartTrackingTouch(seekBar: SeekBar)
            {

            }
            override fun onStopTrackingTouch(seekBar: SeekBar)
            {
                view.view_size_picker_seek_indicator.text = (seekBar.progress + minValue).toString()
            }
        })

        dialog.setTitle(title)
        dialog.setView(view)
        dialog.setPositiveButton(ResourceUtil.getString(R.string.common_choose)) { _, _ ->
            property.set((view.view_size_picker_seek_bar.progress + minValue).toFloat())
            initUI()
        }
        dialog.setNeutralButton(ResourceUtil.getString(R.string.common_default)) { _, _ ->
            property.set(0f)
            initUI()
        }
        dialog.setNegativeButton(ResourceUtil.getString(R.string.common_cancel), null)
        dialog.show()
    }
    private fun showStatePicker(title: String, property: KMutableProperty0<Boolean>)
    {
        var currentState = property.get()

        with (AlertDialog. Builder(context))
        {
            setTitle(title)
            setSingleChoiceItems(arrayOf(ResourceUtil.getString(R.string.common_show), ResourceUtil.getString(R.string.common_hide)), if (currentState) 0 else 1){ _, index: Int ->
                when (index)
                {
                    0 -> currentState = true
                    1 -> currentState = false
                }
            }
            setPositiveButton(ResourceUtil.getString(R.string.common_ok)) { _, _ ->
                property.set(currentState)
                initUI()
            }
            setNegativeButton(ResourceUtil.getString(R.string.common_cancel), null)
            show()
        }
    }
}