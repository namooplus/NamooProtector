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
        lockscreen_base_layout.scale(0.6f, AnimationUtil.DEFAULT_DURATION)
        lockscreen_top_layout.translateX(80f, AnimationUtil.DEFAULT_DURATION, 300)
        lockscreen_top_layout.translateY(-80f, AnimationUtil.DEFAULT_DURATION, 300)
        lockscreen_bottom_layout.translateX(80f, AnimationUtil.DEFAULT_DURATION, 300)
        lockscreen_bottom_layout.translateY(80f, AnimationUtil.DEFAULT_DURATION, 300)
        theme_lockscreen_container.rotateY(10f, AnimationUtil.DEFAULT_DURATION, 300)
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
                lockscreen_base_layout.isSelected -> showColorPicker("바탕 색상", ThemeUtil::baseLayoutColor)
                lockscreen_top_layout.isSelected -> showColorPicker("윗 배경 색상", ThemeUtil::topLayoutColor)
                lockscreen_bottom_layout.isSelected -> showColorPicker("아랫 배경 색상", ThemeUtil::bottomLayoutColor)
                lockscreen_pin_indicator.isSelected -> showColorPicker("PIN 색상", ThemeUtil::pinIndicatorTextColor)
                lockscreen_key_container.isSelected -> showColorPicker("키 색상", ThemeUtil::keyTextColor)
                lockscreen_pattern_container.isSelected -> showColorPicker("패턴 색상", ThemeUtil::patternColor)
                else -> Toast.makeText(context, "잘못된 접근입니다.", Toast.LENGTH_SHORT).show()
            }
        }
        theme_menu_size.setOnClickListener {
            when
            {
                lockscreen_icon_container.isSelected -> showSizePicker("아이콘 크기", ThemeUtil::iconContainerSize, 120, 60)
                lockscreen_pin_indicator.isSelected -> showSizePicker("PIN 크기", ThemeUtil::pinIndicatorTextSize, 45, 15)
                lockscreen_key_container.isSelected -> showSizePicker("키 크기", ThemeUtil::keyTextSize, 40, 10)
                else -> Toast.makeText(context, "잘못된 접근입니다.", Toast.LENGTH_SHORT).show()
            }
        }
        theme_menu_visibility.setOnClickListener {
            when
            {
                lockscreen_icon_container.isSelected -> showStatePicker("아이콘 배경 표시 여부", ThemeUtil::iconContainerVisibility)
                lockscreen_pin_indicator.isSelected -> showStatePicker("PIN 표시 여부", ThemeUtil::pinIndicatorVisibility)
                else -> Toast.makeText(context, "잘못된 접근입니다.", Toast.LENGTH_SHORT).show()
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
                lockscreen_base_layout -> "바탕이 선택되었습니다."
                lockscreen_top_layout -> "윗 배경이 선택되었습니다."
                lockscreen_bottom_layout -> "아랫 배경이 선택되었습니다."
                lockscreen_icon_container -> "아이콘이 선택되었습니다."
                lockscreen_pin_indicator -> "PIN이 선택되었습니다."
                lockscreen_key_container -> "키가 선택되었습니다."
                lockscreen_pattern_container -> "패턴이 선택되었습니다."
                else -> "잘못된 접근입니다."
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
            setPositiveButton("선택", ColorEnvelopeListener { colorEnvelope: ColorEnvelope, _ ->
                property.set("#" + colorEnvelope.hexCode)
                initUI()
            })
            setNeutralButton("기본값") { _, _ ->
                property.set("")
                initUI()
            }
            setNegativeButton("취소", null)
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
        dialog.setPositiveButton("선택") { _, _ ->
            property.set((view.view_size_picker_seek_bar.progress + minValue).toFloat())
            initUI()
        }
        dialog.setNeutralButton("기본값") { _, _ ->
            property.set(0f)
            initUI()
        }
        dialog.setNegativeButton("취소", null)
        dialog.show()
    }
    private fun showStatePicker(title: String, property: KMutableProperty0<Boolean>)
    {
        var currentState = property.get()

        with (AlertDialog. Builder(context))
        {
            setTitle(title)
            setSingleChoiceItems(arrayOf("보이기", "숨기기"), if (currentState) 0 else 1){ _, index: Int ->
                when (index)
                {
                    0 -> currentState = true
                    1 -> currentState = false
                }
            }
            setPositiveButton("확인") { _, _ ->
                property.set(currentState)
                initUI()
            }
            setNegativeButton("취소", null)
            show()
        }
    }
}