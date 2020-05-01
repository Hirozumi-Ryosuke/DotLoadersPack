package com.example.dotloaderspack.dotsloader.loaders

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.*
import android.view.ViewTreeObserver
import android.view.animation.*
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.RelativeLayout.*
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.*
import com.example.dotloaderspack.R
import com.example.dotloaderspack.dotsloader.basicviews.CircleView
import com.example.dotloaderspack.dotsloader.contracts.LoaderContract

class BounceLoader : LinearLayout, LoaderContract {

    var ballRadius: Int = 60
    var ballColor: Int = getColor(context, android.R.color.holo_red_dark)

    var showShadow: Boolean = true
    var shadowColor: Int = getColor(context, android.R.color.black)

    var animDuration: Int = 1500
        set(value) {
            field = if (value <= 0) 1000 else value
        }

    private var relativeLayout: RelativeLayout? = null

    private var ballCircleView: CircleView? = null
    private var ballShadowView: CircleView? = null

    private var calWidth = 0
    private var calHeight = 0

    private val STATE_GOINGDOWN = 0
    private val STATE_SQUEEZING = 1
    private val STATE_RESIZING = 2
    private val STATE_COMINGUP = 3

    private var state = STATE_GOINGDOWN

    constructor(context: Context?) : super(context) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet) : super(context, attrs) {
        initAttributes(attrs)
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initAttributes(attrs)
        initView()
    }

    constructor(context: Context?, ballRadius: Int, ballColor: Int, showShadow: Boolean, shadowColor: Int = 0) : super(context) {
        this.ballRadius = ballRadius
        this.ballColor = ballColor
        this.shadowColor = shadowColor
        initView()
    }

    override fun initAttributes(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BounceLoader, 0, 0)

        this.ballRadius = typedArray.getDimensionPixelSize(R.styleable.BounceLoader_bounce_ballRadius, 60)
        this.ballColor = typedArray.getColor(
            R.styleable.BounceLoader_bounce_ballColor,
            getColor(context, android.R.color.holo_red_dark)
        )

        this.shadowColor = typedArray.getColor(R.styleable.BounceLoader_bounce_shadowColor,
            getColor(context, android.R.color.black)
        )

        this.showShadow = typedArray.getBoolean(R.styleable.BounceLoader_bounce_showShadow, true)
        this.animDuration = typedArray.getInt(R.styleable.BounceLoader_bounce_animDuration, 1500)

        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (calWidth == 0 || calHeight == 0) {
            calWidth = 5 * ballRadius
            calHeight = 8 * ballRadius
        }

        setMeasuredDimension(calWidth, calHeight)
    }

    private fun initView() {
        removeAllViews()
        removeAllViewsInLayout()

        if (calWidth == 0 || calHeight == 0) {
            calWidth = 5 * ballRadius
            calHeight = 8 * ballRadius
        }

        relativeLayout = RelativeLayout(context)

        if (showShadow) {
            ballShadowView = CircleView(
                context = context,
                circleRadius = ballRadius,
                circleColor = shadowColor,
                isAntiAlias = false)

            val shadowParam = RelativeLayout.LayoutParams(
                WRAP_CONTENT,
                WRAP_CONTENT
            )
            shadowParam.addRule(CENTER_HORIZONTAL, TRUE)
            shadowParam.addRule(ALIGN_PARENT_BOTTOM, TRUE)
            relativeLayout?.addView(ballShadowView, shadowParam)
        }

        ballCircleView = CircleView(
            context = context,
            circleRadius = ballRadius,
            circleColor = ballColor
        )

        val ballParam = RelativeLayout.LayoutParams(
            WRAP_CONTENT,
            WRAP_CONTENT
        )

        ballParam.addRule(CENTER_HORIZONTAL, TRUE)
        ballParam.addRule(ALIGN_PARENT_BOTTOM, TRUE)
        relativeLayout?.addView(ballCircleView, ballParam)

        val relParam = RelativeLayout.LayoutParams(calWidth, calHeight)
        this.addView(relativeLayout, relParam)

        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                startLoading()
                this@BounceLoader.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

    }

    private fun startLoading() {
        val ballAnim = getBallAnimation()

        ballAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(anim: Animation?) {
                state = (state + 1) % 4
                startLoading()
            }


            override fun onAnimationRepeat(p0: Animation?) {
            }

            override fun onAnimationStart(p0: Animation?) {
            }

        })

        if (showShadow) {
            if (state == STATE_SQUEEZING || state == STATE_RESIZING) {
                ballShadowView?.clearAnimation()
                ballShadowView?.visibility = View.GONE
            } else {
                ballShadowView?.visibility = View.VISIBLE
                val shadowAnim = getShadowAnimation()
                ballShadowView?.startAnimation(shadowAnim)
            }
        }

        ballCircleView?.startAnimation(ballAnim)
    }

    private fun getBallAnimation(): Animation {
        return when (state) {
            STATE_GOINGDOWN -> {
                TranslateAnimation(0.0f, 0.0f,
                    (-6 * ballRadius).toFloat(), 0.0f)
                    .apply {
                        duration = animDuration.toLong()
                        interpolator = AccelerateInterpolator()
                    }
            }

            STATE_SQUEEZING -> {
                ScaleAnimation(1.0f, 1.0f, 1.0f, 0.85f
                    , ballRadius.toFloat(), (2 * ballRadius).toFloat())
                    .apply {
                        duration = (animDuration / 20).toLong()
                        interpolator = AccelerateInterpolator()
                    }
            }

            STATE_RESIZING -> {
                ScaleAnimation(1.0f, 1.0f, 0.85f, 1.0f
                    , ballRadius.toFloat(), (2 * ballRadius).toFloat())
                    .apply {
                        duration = (animDuration / 20).toLong()
                        interpolator = DecelerateInterpolator()
                    }
            }

            else -> {
                TranslateAnimation(0.0f, 0.0f,
                    0.0f, (-6 * ballRadius).toFloat())
                    .apply {
                        duration = animDuration.toLong()
                        interpolator = DecelerateInterpolator()
                    }
            }
        }.apply {
            fillAfter = true
            repeatCount = 0
        }
    }

    private fun getShadowAnimation(): AnimationSet {

        val transAnim: Animation
        val scaleAnim: Animation
        val alphaAnim: AlphaAnimation

        val set = AnimationSet(true)

        when (state) {
            STATE_COMINGUP -> {
                transAnim = TranslateAnimation(0.0f, (-4 * ballRadius).toFloat(),
                    0.0f, (-3 * ballRadius).toFloat())

                scaleAnim = ScaleAnimation(0.9f, 0.5f, 0.9f, 0.5f,
                    ballRadius.toFloat(), ballRadius.toFloat())

                alphaAnim = AlphaAnimation(0.6f, 0.2f)

                set.interpolator = DecelerateInterpolator()
            }
            else -> {
                transAnim = TranslateAnimation((-4 * ballRadius).toFloat(), 0.0f,
                    (-3 * ballRadius).toFloat(), 0.0f)

                scaleAnim = ScaleAnimation(0.5f, 0.9f, 0.5f, 0.9f,
                    ballRadius.toFloat(), ballRadius.toFloat())

                alphaAnim = AlphaAnimation(0.2f, 0.6f)

                set.interpolator = AccelerateInterpolator()
            }
        }

        set.addAnimation(transAnim)
        set.addAnimation(scaleAnim)
        set.addAnimation(alphaAnim)

        set.apply {
            duration = animDuration.toLong()
            fillAfter = true
            repeatCount = 0
        }

        return set
    }
}