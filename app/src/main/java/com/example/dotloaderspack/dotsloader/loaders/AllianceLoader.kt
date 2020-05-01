package com.example.dotloaderspack.dotsloader.loaders

import android.R.color.*
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.RelativeLayout.*
import android.widget.RelativeLayout.LayoutParams.*
import androidx.core.content.ContextCompat.*
import com.example.dotloaderspack.R.color.*
import com.example.dotloaderspack.R.styleable.*
import com.example.dotloaderspack.dotsloader.basicviews.CircleView
import com.example.dotloaderspack.dotsloader.contracts.LoaderContract

class AllianceLoader : LinearLayout, LoaderContract {


    var dotsRadius = 50
    var strokeWidth = 0

    var drawOnlyStroke = false

    var distanceMultiplier = 4
        set(value) {
            field = if (value < 1) 1 else value
        }

    private var firsDotColor = getColor(context, holo_red_dark)
    private var secondDotColor = getColor(context, holo_green_dark)
    private var thirdDotColor = getColor(context, loader_selected)

    var animDuration = 500

    private var step = 0

    private var calWidthHeight = 0
    private lateinit var firstCircle: CircleView
    private lateinit var secondCircle: CircleView
    private lateinit var thirdCircle: CircleView
    private lateinit var relativeLayout: RelativeLayout


    private var posArrayList: ArrayList<ArrayList<Pair<Float, Float>>> = ArrayList()

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

    constructor(
        context: Context?,
        dotsRadius: Int,
        distanceMultiplier: Int,
        drawOnlyStroke: Boolean,
        strokeWidth: Int,
        firsDotColor: Int,
        secondDotColor: Int,
        thirdDotColor: Int
    ) : super(context) {
        this.dotsRadius = dotsRadius
        this.distanceMultiplier = distanceMultiplier
        this.drawOnlyStroke = drawOnlyStroke
        this.strokeWidth = strokeWidth
        this.firsDotColor = firsDotColor
        this.secondDotColor = secondDotColor
        this.thirdDotColor = thirdDotColor

        initView()
    }

    override fun initAttributes(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, AllianceLoader, 0, 0)

        dotsRadius = typedArray.getDimensionPixelSize(AllianceLoader_alliance_dotsRadius, 50)

        distanceMultiplier = typedArray.getInteger(AllianceLoader_alliance_distanceMultiplier, 4)

        firsDotColor = typedArray.getColor(AllianceLoader_alliance_firstDotsColor, getColor(context, loader_selected))

        secondDotColor = typedArray.getColor(AllianceLoader_alliance_secondDotsColor, getColor(context, loader_selected))

        thirdDotColor = typedArray.getColor(AllianceLoader_alliance_thirdDotsColor, getColor(context, loader_selected))

        drawOnlyStroke = typedArray.getBoolean(AllianceLoader_alliance_drawOnlyStroke, false)

        if (drawOnlyStroke) {
            strokeWidth = typedArray.getDimensionPixelSize(AllianceLoader_alliance_strokeWidth, 20)
        }

        animDuration = typedArray.getInt(AllianceLoader_alliance_animDuration, 500)

        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (calWidthHeight == 0) {
            calWidthHeight = (2 * dotsRadius * distanceMultiplier) + strokeWidth
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
            calWidthHeight = (2 * dotsRadius * distanceMultiplier) + strokeWidth
        }

        firstCircle = CircleView(context, dotsRadius, firsDotColor, drawOnlyStroke, strokeWidth)
        val firstParam = RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        firstParam.addRule(ALIGN_PARENT_TOP, TRUE)
        firstParam.addRule(CENTER_HORIZONTAL, TRUE)

        relativeLayout.addView(firstCircle, firstParam)

        secondCircle = CircleView(context, dotsRadius, secondDotColor, drawOnlyStroke, strokeWidth)
        val secondParam = RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        secondParam.addRule(ALIGN_PARENT_BOTTOM, TRUE)
        secondParam.addRule(ALIGN_PARENT_RIGHT, TRUE)

        relativeLayout.addView(secondCircle, secondParam)

        thirdCircle = CircleView(context, dotsRadius, thirdDotColor, drawOnlyStroke, strokeWidth)
        val thirdParam = RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        thirdParam.addRule(ALIGN_PARENT_BOTTOM, TRUE)
        thirdParam.addRule(ALIGN_PARENT_LEFT, TRUE)

        relativeLayout.addView(thirdCircle, thirdParam)

        val relParam = RelativeLayout.LayoutParams(calWidthHeight, calWidthHeight)
        this.addView(relativeLayout, relParam)

        initInitialValues()


        val loaderView = this


        viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                startLoading()

                val vto = loaderView.viewTreeObserver
                vto.removeOnGlobalLayoutListener(this)
            }
        })
    }

    private fun initInitialValues() {

        val fullDistance = (calWidthHeight - ((2 * dotsRadius) + strokeWidth)).toFloat()
        val halfDistance = fullDistance / 2

        val firstPosArray = arrayListOf<Pair<Float, Float>>()

        firstPosArray.add(Pair(0.0f, 0.0f))
        firstPosArray.add(Pair(halfDistance, fullDistance))
        firstPosArray.add(Pair(-halfDistance, fullDistance))

        posArrayList.add(firstPosArray)

        val secondPosArray = ArrayList<Pair<Float, Float>>()

        secondPosArray.add(Pair(0.0f, 0.0f))
        secondPosArray.add(Pair(-fullDistance, 0.0f))
        secondPosArray.add(Pair(-halfDistance, -fullDistance))

        posArrayList.add(secondPosArray)

        val thirdPosArray = ArrayList<Pair<Float, Float>>()

        thirdPosArray.add(Pair(0.0f, 0.0f))
        thirdPosArray.add(Pair(halfDistance, -fullDistance))
        thirdPosArray.add(Pair(fullDistance, 0.0f))

        posArrayList.add(thirdPosArray)
    }

    private fun startLoading() {

        val firstCircleAnim = getTranslateAnim(1)
        firstCircle.startAnimation(firstCircleAnim)

        val secondCircleAnim = getTranslateAnim(2)
        secondCircle.startAnimation(secondCircleAnim)

        val thirdCircleAnim = getTranslateAnim(3)

        thirdCircleAnim.setAnimationListener(object : Animation.AnimationListener {

            override fun onAnimationEnd(p0: Animation?) {
                step++
                if (step > 2) step = 0
                startLoading()
            }

            override fun onAnimationRepeat(p0: Animation?) = Unit

            override fun onAnimationStart(p0: Animation?) = Unit
        })

        thirdCircle.startAnimation(thirdCircleAnim)
    }

    private fun getTranslateAnim(circleCount: Int): TranslateAnimation {

        var nextStep = step + 1
        if (nextStep > 2) {
            nextStep = 0
        }

        val fromXPos = posArrayList[circleCount - 1][step].first
        val fromYPos = posArrayList[circleCount - 1][step].second

        val toXPos = posArrayList[circleCount - 1][nextStep].first
        val toYPos = posArrayList[circleCount - 1][nextStep].second

        val transAnim = TranslateAnimation(fromXPos, toXPos, fromYPos, toYPos)
        transAnim.duration = animDuration.toLong()
        transAnim.fillAfter = true
        transAnim.interpolator = AccelerateDecelerateInterpolator()
        transAnim.repeatCount = 0

        return transAnim
    }
}