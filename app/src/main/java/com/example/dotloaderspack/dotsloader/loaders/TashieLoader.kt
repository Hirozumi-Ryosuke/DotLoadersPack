package com.example.dotloaderspack.dotsloader.loaders

import android.R.anim.*
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity.*
import android.view.ViewTreeObserver.*
import android.view.animation.*
import android.view.animation.Animation.*
import androidx.core.content.ContextCompat.*
import com.example.dotloaderspack.R.color.*
import com.example.dotloaderspack.R.styleable.*
import com.example.dotloaderspack.dotsloader.basicviews.CircleView
import com.example.dotloaderspack.dotsloader.contracts.AbstractLinearLayout

class TashieLoader : AbstractLinearLayout {

    var noOfDots = 8
    var animDelay = 100

    private lateinit var dotsArray: Array<CircleView?>

    private var isDotsExpanding = true

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

    constructor(context: Context?, noOfDots: Int, dotsRadius: Int, dotsDist: Int, dotsColor: Int) : super(context) {
        this.noOfDots = noOfDots
        this.dotsRadius = dotsRadius
        this.dotsDist = dotsDist
        this.dotsColor = dotsColor

        initView()
    }


    override fun initAttributes(attrs: AttributeSet) {

        val typedArray = context.obtainStyledAttributes(attrs, TashieLoader, 0, 0)

        dotsRadius = typedArray.getDimensionPixelSize(TashieLoader_tashieloader_dotsRadius, 30)
        dotsDist = typedArray.getDimensionPixelSize(TashieLoader_tashieloader_dotsDist, 15)
        dotsColor = typedArray.getColor(TashieLoader_tashieloader_dotsColor, getColor(context, loader_selected))

        animDuration = typedArray.getInt(TashieLoader_tashieloader_animDur, 500)

        interpolator = AnimationUtils.loadInterpolator(context,
            typedArray.getResourceId(
                TashieLoader_tashieloader_interpolator, linear_interpolator)
        ) as AnticipateOvershootInterpolator

        noOfDots = typedArray.getInt(TashieLoader_tashieloader_noOfDots, 8)
        animDelay = typedArray.getInt(TashieLoader_tashieloader_animDelay, 100)

        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val calHeight = 2 * dotsRadius
        val calWidth = ((2 * noOfDots * dotsRadius) + ((noOfDots - 1) * dotsDist))

        setMeasuredDimension(calWidth, calHeight)
    }

    override fun initView() {
        removeAllViews()
        removeAllViewsInLayout()
        setVerticalGravity(BOTTOM)

        dotsArray = arrayOfNulls<CircleView?>(noOfDots)
        for (iCount in 0 until noOfDots) {
            val circle = CircleView(context, dotsRadius, dotsColor)

            val params = LayoutParams(2 * dotsRadius, 2 * dotsRadius)

            if (iCount != noOfDots - 1) {
                params.rightMargin = dotsDist
            }

            addView(circle, params)
            dotsArray[iCount] = circle
        }

        viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                this@TashieLoader.viewTreeObserver.removeOnGlobalLayoutListener(this)
                startLoading()
            }
        })
    }

    fun startLoading() {

        for (iCount in 0 until noOfDots) {
            val anim = getScaleAnimation(isDotsExpanding, iCount)
            dotsArray[iCount]!!.startAnimation(anim)

            setAnimationListener(anim, iCount)
        }
        isDotsExpanding = !isDotsExpanding
    }

    private fun getScaleAnimation(isExpanding: Boolean, delay: Int): AnimationSet {
        val anim = AnimationSet(true)

        val scaleAnim: ScaleAnimation = when (isExpanding) {
            true -> {
                ScaleAnimation(0f, 1f, 0f, 1f,
                    RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f)
            }

            false -> {
                ScaleAnimation(1f, 0f, 1f, 0f,
                    RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f)
            }
        }

        scaleAnim.duration = animDuration.toLong()
        scaleAnim.fillAfter = true
        scaleAnim.repeatCount = 0
        scaleAnim.startOffset = (animDelay * delay).toLong()
        anim.addAnimation(scaleAnim)

        anim.interpolator = interpolator
        return anim
    }

    private fun setAnimationListener(anim: AnimationSet, dotPosition: Int) =
        when (dotPosition) {
            noOfDots - 1 -> anim.setAnimationListener(object : AnimationListener {
                override fun onAnimationRepeat(p0: Animation?) = Unit

                override fun onAnimationEnd(p0: Animation?) = startLoading()

                override fun onAnimationStart(p0: Animation?) = Unit
            })
            else -> anim.setAnimationListener(object : AnimationListener {
                override fun onAnimationRepeat(p0: Animation?) = Unit

                override fun onAnimationEnd(p0: Animation?) {
                    when {
                        !isDotsExpanding -> dotsArray[dotPosition]!!.visibility = VISIBLE
                        isDotsExpanding -> dotsArray[dotPosition]!!.visibility = INVISIBLE
                    }
                }

                override fun onAnimationStart(p0: Animation?) = Unit

            })
        }
}