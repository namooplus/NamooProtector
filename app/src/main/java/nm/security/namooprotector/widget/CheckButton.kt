package nm.security.namooprotector.widget

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Color
import androidx.core.content.ContextCompat
import android.widget.TextView
import android.view.LayoutInflater
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import nm.security.namooprotector.R

class CheckButton : LinearLayout
{
    var background: LinearLayout? = null
    var imageView: ImageView? = null
    var descriptionView: TextView? = null
    var titleView: TextView? = null

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

        background = findViewById(R.id.view_check_button_background)
        imageView = findViewById(R.id.view_check_button_image_view)
        descriptionView = findViewById(R.id.view_check_button_description_view)
        titleView = findViewById(R.id.view_check_button_title_view)

        setBackgroundColor(Color.parseColor("#ffffff"))
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
        //checked
        if (typedArray.getBoolean(R.styleable.CheckButton_checked, false))
        {
            background!!.setBackgroundColor(ContextCompat.getColor(context, R.color.highlight_color))
            imageView!!.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.text_bright_color))
            descriptionView!!.setTextColor(ContextCompat.getColor(context, R.color.text_bright_color))
            titleView!!.setTextColor(ContextCompat.getColor(context, R.color.text_bright_color))
        }
        else
        {
            background!!.setBackgroundColor(ContextCompat.getColor(context, R.color.card_color))
            imageView!!.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.text_dark_color))
            descriptionView!!.setTextColor(ContextCompat.getColor(context, R.color.text_dark_color))
            titleView!!.setTextColor(ContextCompat.getColor(context, R.color.text_dark_color))
        }

        //image
        if (typedArray.getResourceId(R.styleable.CheckButton_image, 0) == 0)
            imageView!!.visibility = View.GONE

        else
        {
            imageView!!.visibility = View.VISIBLE
            imageView!!.setImageResource(typedArray.getResourceId(R.styleable.CheckButton_image, R.drawable.icon_np_text))
        }

        //description
        if (typedArray.getString(R.styleable.CheckButton_description) == "")
            descriptionView!!.visibility = View.GONE

        else
        {
            descriptionView!!.visibility = View.VISIBLE
            descriptionView!!.text = typedArray.getString(R.styleable.CheckButton_description)
        }

        //title
        titleView!!.text = typedArray.getString(R.styleable.CheckButton_title)

        typedArray.recycle()
    }

    //메소드
    fun setChecked(checked: Boolean)
    {
        if (checked)
        {
            background!!.setBackgroundColor(ContextCompat.getColor(context, R.color.highlight_color))
            imageView!!.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.text_bright_color))
            descriptionView!!.setTextColor(ContextCompat.getColor(context, R.color.text_bright_color))
            titleView!!.setTextColor(ContextCompat.getColor(context, R.color.text_bright_color))
        }
        else
        {
            background!!.setBackgroundColor(ContextCompat.getColor(context, R.color.card_color))
            imageView!!.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.text_dark_color))
            descriptionView!!.setTextColor(ContextCompat.getColor(context, R.color.text_dark_color))
            titleView!!.setTextColor(ContextCompat.getColor(context, R.color.text_dark_color))
        }
    }
    fun setImage(imageResource: Int)
    {
        if (imageResource == 0)
            imageView!!.visibility = View.GONE

        else
        {
            imageView!!.visibility = View.VISIBLE
            imageView!!.setImageResource(imageResource)
        }
    }
    fun setTint(color: String)
    {
        imageView!!.imageTintList = ColorStateList.valueOf(Color.parseColor(color))
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
    fun setTitle(text: String)
    {
        titleView!!.text = text
    }
    fun setTitle(textResource: Int)
    {
        titleView!!.setText(textResource)
    }
}