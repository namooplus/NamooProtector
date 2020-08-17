package nm.security.namooprotector.widget

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import nm.security.namooprotector.R

class CheckButton : LinearLayout
{
    private var backgroundView: LinearLayout? = null
    private var iconView: ImageView? = null
    private var titleView: TextView? = null
    private var descriptionView: TextView? = null

    private var checked = false
    private var tintMode = true

    constructor(context: Context) : super(context)
    {
        initView()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    {
        initView()
        getAttrs(attrs)
    }
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs)
    {
        initView()
        getAttrs(attrs, defStyle)
    }

    //설정
    private fun initView()
    {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.view_check_button, this)

        backgroundView = findViewById(R.id.view_check_button_background)
        iconView = findViewById(R.id.view_check_button_icon)
        titleView = findViewById(R.id.view_check_button_title)
        descriptionView = findViewById(R.id.view_check_button_description)

        //기본 속성
        val value = TypedValue()
        context.theme.resolveAttribute(android.R.attr.selectableItemBackground, value, true)

        setBackgroundColor(Color.parseColor("#ffffff"))
        foreground = ContextCompat.getDrawable(context, value.resourceId)
        elevation = 10f
        isClickable = true
        isFocusable = true
    }

    private fun getAttrs(attrs: AttributeSet)
    {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CheckButton)
        updateView(typedArray)
    }
    private fun getAttrs(attrs: AttributeSet, defStyle: Int)
    {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CheckButton, defStyle, 0)
        updateView(typedArray)
    }
    private fun updateView(typedArray: TypedArray)
    {
        //tint mode
        tintMode = typedArray.getBoolean(R.styleable.CheckButton_check_tint_mode, true)

        //checked
        checked = typedArray.getBoolean(R.styleable.CheckButton_check_checked, false)

        if (checked)
        {
            backgroundView!!.setBackgroundColor(ContextCompat.getColor(context, R.color.highlight_color))
            iconView!!.imageTintList = if (tintMode) ColorStateList.valueOf(ContextCompat.getColor(context, R.color.text_bright_color)) else null
            titleView!!.setTextColor(ContextCompat.getColor(context, R.color.text_bright_color))
            descriptionView!!.setTextColor(ContextCompat.getColor(context, R.color.text_bright_color))
        }
        else
        {
            backgroundView!!.setBackgroundColor(ContextCompat.getColor(context, R.color.card_color))
            iconView!!.imageTintList = if (tintMode) ColorStateList.valueOf(ContextCompat.getColor(context, R.color.text_dark_color)) else null
            titleView!!.setTextColor(ContextCompat.getColor(context, R.color.text_dark_color))
            descriptionView!!.setTextColor(ContextCompat.getColor(context, R.color.text_dark_color))
        }

        //icon
        if (typedArray.getResourceId(R.styleable.CheckButton_check_icon, 0) == 0)
            iconView!!.visibility = View.GONE

        else
        {
            iconView!!.visibility = View.VISIBLE
            iconView!!.setImageResource(typedArray.getResourceId(R.styleable.CheckButton_check_icon, R.drawable.ic_launcher))
        }

        //title
        if (typedArray.getString(R.styleable.CheckButton_check_title) == "")
            titleView!!.visibility = View.GONE

        else
        {
            titleView!!.visibility = View.VISIBLE
            titleView!!.text = typedArray.getString(R.styleable.CheckButton_check_title)
        }

        //description
        if (typedArray.getString(R.styleable.CheckButton_check_description) == "")
            descriptionView!!.visibility = View.GONE

        else
        {
            descriptionView!!.visibility = View.VISIBLE
            descriptionView!!.text = typedArray.getString(R.styleable.CheckButton_check_description)
        }

        typedArray.recycle()
    }

    //메소드
    var isChecked: Boolean
        set(value)
        {
            if (value)
            {
                checked = true

                backgroundView!!.setBackgroundColor(ContextCompat.getColor(context, R.color.highlight_color))
                iconView!!.imageTintList = if (tintMode) ColorStateList.valueOf(ContextCompat.getColor(context, R.color.text_bright_color)) else null
                titleView!!.setTextColor(ContextCompat.getColor(context, R.color.text_bright_color))
                descriptionView!!.setTextColor(ContextCompat.getColor(context, R.color.text_bright_color))
            }
            else
            {
                checked = false

                backgroundView!!.setBackgroundColor(ContextCompat.getColor(context, R.color.card_color))
                iconView!!.imageTintList = if (tintMode) ColorStateList.valueOf(ContextCompat.getColor(context, R.color.text_dark_color)) else null
                titleView!!.setTextColor(ContextCompat.getColor(context, R.color.text_dark_color))
                descriptionView!!.setTextColor(ContextCompat.getColor(context, R.color.text_dark_color))
            }
        }
        get() = checked

    fun setIcon(imageResource: Int)
    {
        if (imageResource == 0)
            iconView!!.visibility = View.GONE

        else
        {
            iconView!!.visibility = View.VISIBLE
            iconView!!.setImageResource(imageResource)
        }
    }
    fun setIcon(imageDrawable: Drawable?)
    {
        if (imageDrawable == null)
            iconView!!.visibility = View.GONE

        else
        {
            iconView!!.visibility = View.VISIBLE
            iconView!!.setImageDrawable(imageDrawable)
        }
    }
    fun setTitle(text: String)
    {
        if (text == "")
            titleView!!.visibility = View.GONE

        else
        {
            titleView!!.visibility = View.VISIBLE
            titleView!!.text = text
        }
    }
    fun setTitle(textResource: Int)
    {
        if (textResource == 0)
            titleView!!.visibility = View.GONE

        else
        {
            titleView!!.visibility = View.VISIBLE
            titleView!!.setText(textResource)
        }
    }
    fun setDescription(text: String)
    {
        if (text == "")
            descriptionView!!.visibility = View.GONE

        else
        {
            descriptionView!!.visibility = View.VISIBLE
            descriptionView!!.text = text
        }
    }
    fun setDescription(textResource: Int)
    {
        if (textResource == 0)
            descriptionView!!.visibility = View.GONE

        else
        {
            descriptionView!!.visibility = View.VISIBLE
            descriptionView!!.setText(textResource)
        }
    }
    fun setTintMode(on: Boolean)
    {
        tintMode = on

        //재로드
        isChecked = checked
    }
}