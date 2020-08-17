package nm.security.namooprotector.util

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import android.view.View
import android.view.ViewAnimationUtils
import kotlin.math.max

object AnimationUtil
{
    const val DEFAULT_DURATION = 450
    const val SHORT_DURATION = 300
    const val LONG_DURATION = 600

    fun View.alpha(value: Float, duration: Int, delay: Int = 0): View
    {
        if (!this.isShown)
            this.visibility = View.VISIBLE

        this.animate()
            .alpha(value)
            .setInterpolator(FastOutSlowInInterpolator())
            .setStartDelay(delay.toLong())
            .setDuration(duration.toLong())
            .withLayer()
            .withEndAction{
                this.alpha = value

                if (value == 0f)
                    this.visibility = View.GONE
            }

        return this
    }
    fun View.scale(value: Float, duration: Int, delay: Int = 0): View
    {
        this.animate()
            .scaleX(value)
            .scaleY(value)
            .setInterpolator(FastOutSlowInInterpolator())
            .setStartDelay(delay.toLong())
            .setDuration(duration.toLong())
            .withLayer()
            .withEndAction {
                this.scaleX = value
                this.scaleY = value
            }

        return this
    }
    fun View.translateX(valueX: Float, duration: Int, delay: Int = 0): View
    {
        this.animate()
            .translationX(valueX)
            .setInterpolator(FastOutSlowInInterpolator())
            .setStartDelay(delay.toLong())
            .setDuration(duration.toLong())
            .withLayer()
            .withEndAction {
                this.translationX = valueX
            }

        return this
    }
    fun View.translateY(valueY: Float, duration: Int, delay: Int = 0): View
    {
        this.animate()
            .translationY(valueY)
            .setInterpolator(FastOutSlowInInterpolator())
            .setStartDelay(delay.toLong())
            .setDuration(duration.toLong())
            .withLayer()
            .withEndAction {
                this.translationY = valueY
            }

        return this
    }
    fun View.rotateX(valueX: Float, duration: Int, delay: Int = 0): View
    {
        this.animate()
            .rotationX(valueX)
            .setInterpolator(FastOutSlowInInterpolator())
            .setStartDelay(delay.toLong())
            .setDuration(duration.toLong())
            .withLayer()
            .withEndAction {
                this.rotationX = valueX
            }

        return this
    }
    fun View.rotateY(valueY: Float, duration: Int, delay: Int = 0): View
    {
        this.animate()
            .rotationY(valueY)
            .setInterpolator(FastOutSlowInInterpolator())
            .setStartDelay(delay.toLong())
            .setDuration(duration.toLong())
            .withLayer()
            .withEndAction {
                this.rotationY = valueY
            }

        return this
    }
    fun View.circularReveal(reveal: Boolean, duration: Int, delay: Int = 0): View
    {
        val rect = Rect()
        this.getDrawingRect(rect)

        val centerX = rect.centerX()
        val centerY = rect.centerY()
        val finalRadius = max(rect.width(), rect.height())

        val revealAnimator: Animator = if (reveal) ViewAnimationUtils.createCircularReveal(this, centerX, centerY, 0f, finalRadius.toFloat()) else ViewAnimationUtils.createCircularReveal(this, centerX, centerY, finalRadius.toFloat(), 0f)
        revealAnimator.interpolator = FastOutSlowInInterpolator()
        revealAnimator.duration = duration.toLong()
        revealAnimator.startDelay = delay.toLong()

        if (reveal)
            this.visibility = View.VISIBLE

        else
        {
            revealAnimator.addListener(object : Animator.AnimatorListener
            {
                override fun onAnimationStart(animator: Animator)
                {

                }
                override fun onAnimationEnd(animator: Animator)
                {
                    this@circularReveal.visibility = View.GONE
                }
                override fun onAnimationCancel(animator: Animator)
                {

                }
                override fun onAnimationRepeat(animator: Animator)
                {

                }
            })
        }

        revealAnimator.start()

        return this
    }
    fun View.changeColor(value: Int, duration: Int, delay: Int = 0): View
    {
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), (this.background as ColorDrawable).color, value)

        colorAnimation.duration = duration.toLong()
        colorAnimation.startDelay = delay.toLong()
        colorAnimation.addUpdateListener { animator -> this.setBackgroundColor(animator.animatedValue as Int) }
        colorAnimation.start()

        return this
    }
}