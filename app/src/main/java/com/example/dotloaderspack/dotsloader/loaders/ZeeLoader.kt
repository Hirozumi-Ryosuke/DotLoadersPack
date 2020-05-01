package com.example.dotloaderspack.dotsloader.loaders

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.RelativeLayout.*
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.*
import com.example.dotloaderspack.R
import com.example.dotloaderspack.R.color.*
import com.example.dotloaderspack.R.styleable.*
import com.example.dotloaderspack.dotsloader.basicviews.CircleView
import com.example.dotloaderspack.dotsloader.contracts.LoaderContract

class ZeeLoader : LinearLayout, LoaderContract {

    var dotsRadius: Int = 50

    var distanceMultiplier: Int = 4
        set(value) {
            field = if (value < 1) 1
            else value
        }

    private var firsDotColor = getColor(context, loader_selected)
    private var secondDotColor = getColor(context, loader_selected)

    var animDuration = 500

    private var step = 0

    private var calWidthHeight = 0
    private lateinit var firstCircle: CircleView
    private lateinit var secondCircle: CircleView
    private lateinit var relativeLayout: RelativeLayout

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initAttributes(attrs)
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initAttributes(attrs)
        initView()
    }

    constructor(context: Context, dotsRadius: Int, distanceMultiplier: Int, firsDotColor: Int, secondDotColor: Int) : super(context) {
        this.dotsRadius = dotsRadius
        this.distanceMultiplier = distanceMultiplier
        this.firsDotColor = firsDotColor
        this.secondDotColor = secondDotColor
        initView()
    }


    override fun initAttributes(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, ZeeLoader, 0, 0)

        this.dotsRadius = typedArray.getDimensionPixelSize(ZeeLoader_zee_dotsRadius, 50)

        this.distanceMultiplier = typedArray.getInteger(ZeeLoader_zee_distanceMultiplier, 4)

        this.firsDotColor = typedArray.getColor(ZeeLoader_zee_firstDotsColor, getColor(context, loader_selected))

        this.secondDotColor = typedArray.getColor(ZeeLoader_zee_secondDotsColor, getColor(context, loader_selected))

        this.animDuration = typedArray.getInt(ZeeLoader_zee_animDuration, 500)

        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        when (calWidthHeight) {
            0 -> calWidthHeight = (2 * dotsRadius * distanceMultiplier)
        }

        setMeasuredDimension(calWidthHeight, calWidthHeight)
    }

    private fun initView() {
        removeAllViews()
        removeAllViewsInLayout()

        this.gravity = Gravity.CENTER_HORIZONTAL

        relativeLayout = RelativeLayout(context)
        relativeLayout.gravity = Gravity.CENTER_HORIZONTAL


        if (calWidthHeight == 0) {
            calWidthHeight = (2 * dotsRadius * distanceMultiplier)
        }

        firstCircle = CircleView(context, dotsRadius, firsDotColor)
        val firstParam = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        firstParam.addRule(ALIGN_PARENT_TOP, TRUE)
        firstParam.addRule(ALIGN_PARENT_LEFT, TRUE)

        relativeLayout.addView(firstCircle, firstParam)

        secondCircle = CircleView(context, dotsRadius, secondDotColor)
        val secondParam = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        secondParam.addRule(ALIGN_PARENT_BOTTOM, TRUE)
        secondParam.addRule(ALIGN_PARENT_RIGHT, TRUE)

        relativeLayout.addView(secondCircle, secondParam)

        val relParam = RelativeLayout.LayoutParams(calWidthHeight, calWidthHeight)
        this.addView(relativeLayout, relParam)


        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                startLoading()
                this@ZeeLoader.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    private fun startLoading() {

        val firstCircleAnim = getTranslateAnim(1)
        firstCircle.startAnimation(firstCircleAnim)

        val secondCircleAnim = getTranslateAnim(2)

        secondCircleAnim.setAnimationListener(object : Animation.AnimationListener {

            override fun onAnimationEnd(p0: Animation?) {
                step++
                if (step > 3) {
                    step = 0
                }
                startLoading()
            }

            override fun onAnimationRepeat(p0: Animation?) {
            }

            override fun onAnimationStart(p0: Animation?) {
            }

        })

        secondCircle.startAnimation(secondCircleAnim)
    }

    private fun getTranslateAnim(circleCount: Int): TranslateAnimation {

        val circleDiameter = 2 * dotsRadius
        val finalDistance = ((distanceMultiplier - 1) * circleDiameter).toFloat()


        var fromXPos = 0.0f
        var fromYPos = 0.0f

        var toXPos = 0.0f
        var toYPos = 0.0f

        when (step) {

            0 -> {
                if (circleCount == 1) {
                    toXPos = finalDistance
                } else {
                    toXPos = -1 * finalDistance
                }
            }

            1 -> {
                if (circleCount == 1) {
                    fromXPos = finalDistance
                    toYPos = finalDistance
                } else {
                    fromXPos = -1 * finalDistance
                    toYPos = -1 * finalDistance
                }
            }

            2 -> {
                if (circleCount == 1) {
                    toXPos = finalDistance
                    fromYPos = finalDistance
                    toYPos = fromYPos
                } else {
                    toXPos = -1 * finalDistance
                    fromYPos = -1 * finalDistance
                    toYPos = fromYPos
                }
            }

            3 -> {
                if (circleCount == 1) {
                    fromXPos = finalDistance
                    fromYPos = finalDistance
                } else {
                    fromXPos = -1 * finalDistance
                    fromYPos = -1 * finalDistance
                }
            }
        }

        val transAnim = TranslateAnimation(fromXPos, toXPos,
            fromYPos, toYPos)
        transAnim.duration = animDuration.toLong()
        transAnim.fillAfter = true
        transAnim.interpolator = AccelerateDecelerateInterpolator()
        transAnim.repeatCount = 0

        return transAnim
    }

}