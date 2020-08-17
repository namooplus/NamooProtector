package nm.security.namooprotector.widget

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import nm.security.namooprotector.R
import nm.security.namooprotector.util.ConvertUtil

class FloatingButton : CardView
{
    private var textView: TextView? = null

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
        inflater.inflate(R.layout.view_floating_button, this)

        textView = findViewById(R.id.view_floating_button_text)

        //기본 속성
        val value = TypedValue()
        context.theme.resolveAttribute(android.R.attr.selectableItemBackground, value, true)

        setCardBackgroundColor(resources.getColor(R.color.highlight_color, null))
        foreground = ContextCompat.getDrawable(context, value.resourceId)
        cardElevation = resources.getDimension(R.dimen.focus_elevation)
        radius = ConvertUtil.dpToPx(25f)
        isClickable = true
        isFocusable = true
    }

    private fun getAttrs(attrs: AttributeSet)
    {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.FloatingButton)
        updateView(typedArray)
    }
    private fun getAttrs(attrs: AttributeSet, defStyle: Int)
    {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.FloatingButton, defStyle, 0)
        updateView(typedArray)
    }
    private fun updateView(typedArray: TypedArray)
    {
        textView!!.text = typedArray.getString(R.styleable.FloatingButton_floating_text)
        typedArray.recycle()
    }

    //메소드
    fun setText(text: String)
    {
        textView!!.text = text
    }
    fun setText(textResource: Int)
    {
        textView!!.setText(textResource)
    }
}