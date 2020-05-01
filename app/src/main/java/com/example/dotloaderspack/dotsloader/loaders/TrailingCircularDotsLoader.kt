package com.example.dotloaderspack.dotsloader.loaders

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewTreeObserver
import android.view.animation.*
import android.view.animation.Animation.*
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.*
import com.example.dotloaderspack.R
import com.example.dotloaderspack.R.styleable.*
import com.example.dotloaderspack.dotsloader.basicviews.CircleView
import com.example.dotloaderspack.dotsloader.contracts.LoaderContract

class TrailingCircularDotsLoader : LinearLayout, LoaderContract {

    var dotsRadius = 50
    var bigCircleRadius = 200

    var circleColor = getColor(context, R.color.loader_selected)
    var noOfTrailingDots = 6

    var animDuration = 2000
    var animDelay = animDuration / 10

    private var calWidthHeight = 0
    private lateinit var mainCircle: CircleView
    private lateinit var relativeLayout: RelativeLayout
    private lateinit var trailingCirclesArray: Array<CircleView?>

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

    constructor(context: Context?, dotsRadius: Int, circleColor: Int, bigCircleRadius: Int, noOfTrailingDots: Int) : super(context) {
        this.dotsRadius = dotsRadius
        this.circleColor = circleColor
        this.bigCircleRadius = bigCircleRadius
        this.noOfTrailingDots = noOfTrailingDots
        initView()
    }


    override fun initAttributes(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, TrailingCircularDotsLoader, 0, 0)

        this.dotsRadius = typedArray.getDimensionPixelSize(
            TrailingCircularDotsLoader_trailingcircular_dotsRadius, 50)
        this.bigCircleRadius = typedArray.getDimensionPixelSize(
            TrailingCircularDotsLoader_trailingcircular_bigCircleRadius, 200)

        this.circleColor = typedArray.getColor(
            TrailingCircularDotsLoader_trailingcircular_dotsColor,
            getColor(context, R.color.loader_selected)
        )

        this.noOfTrailingDots = typedArray.getInt(
            TrailingCircularDotsLoader_trailingcircular_noOfTrailingDots, 6)


        this.animDuration = typedArray.getInt(
            TrailingCircularDotsLoader_trailingcircular_animDuration, 2000)
        this.animDelay = typedArray.getInt(TrailingCircularDotsLoader_trailingcircular_animDelay, animDuration / 10)

        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (calWidthHeight == 0) {
            calWidthHeight = (2 * bigCircleRadius) + (2 * dotsRadius)
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
            calWidthHeight = (2 * bigCircleRadius) + (2 * dotsRadius)
        }

        val relParam = RelativeLayout.LayoutParams(calWidthHeight, calWidthHeight)

        mainCircle = CircleView(context, dotsRadius, circleColor)
        relativeLayout.addView(mainCircle)

        this.addView(relativeLayout, relParam)


        trailingCirclesArray = arrayOfNulls(noOfTrailingDots)

        for (i in 0 until noOfTrailingDots) {
            val circle = CircleView(context, dotsRadius, circleColor)
            relativeLayout.addView(circle)
            trailingCirclesArray[i] = circle
        }

        val loaderView = this


        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                startLoading()

                val vto = loaderView.viewTreeObserver
                vto.removeOnGlobalLayoutListener(this)
            }
        })
    }

    private fun startLoading() {

        val mainCircleAnim = getRotateAnimation()
        mainCircle.startAnimation(mainCircleAnim)

        for (i in 1..noOfTrailingDots) {
            val animSet = getTrainlingAnim(i, ((animDuration * (2 + i)) / 20))
            trailingCirclesArray[i - 1]!!.startAnimation(animSet)

            if (i == noOfTrailingDots - 1) animSet.setAnimationListener(object : AnimationListener {
                override fun onAnimationEnd(p0: Animation?) {
                    Handler().postDelayed({
                        startLoading()
                    }, animDelay.toLong())
                }

                override fun onAnimationStart(p0: Animation?) {
                }

                override fun onAnimationRepeat(p0: Animation?) {
                }
            })
        }
    }

    private fun getRotateAnimation(): RotateAnimation {

        val rotateAnim = RotateAnimation(0f, 360f,
            RELATIVE_TO_SELF, 0.5f,
            RELATIVE_TO_PARENT, 0.5f)
        rotateAnim.duration = animDuration.toLong()
        rotateAnim.fillAfter = true
        rotateAnim.interpolator = AccelerateDecelerateInterpolator()
        rotateAnim.startOffset = (animDuration / 10).toLong()

        return rotateAnim
    }

    private fun getTrainlingAnim(count: Int, delay: Int): AnimationSet {
        val animSet = AnimationSet(true)

        val scaleFactor: Float = 1.00f - (count.toFloat() / 20)

        val scaleAnim = ScaleAnimation(
            scaleFactor,
            scaleFactor,
            scaleFactor,
            scaleFactor,
            RELATIVE_TO_SELF,
            0.5f,
            RELATIVE_TO_SELF,
            0.5f
        )
        animSet.addAnimation(scaleAnim)


        val rotateAnim = RotateAnimation(
            0f,
            360f,
            RELATIVE_TO_SELF,
            0.5f,
            RELATIVE_TO_PARENT,
            0.5f
        )
        rotateAnim.duration = animDuration.toLong()

        animSet.addAnimation(rotateAnim)
        animSet.duration = animDuration.toLong()
        animSet.fillAfter = false
        animSet.interpolator = AccelerateDecelerateInterpolator()
        animSet.startOffset = delay.toLong()

        return animSet
    }
}