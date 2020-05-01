package com.example.dotloaderspack.dotsloader.loaders

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.ViewTreeObserver
import android.view.animation.*
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.example.dotloaderspack.R
import com.example.dotloaderspack.R.color.*
import com.example.dotloaderspack.R.styleable.*
import com.example.dotloaderspack.dotsloader.basicviews.CircleView
import com.example.dotloaderspack.dotsloader.basicviews.ThreeDotsBaseView

class SlidingLoader : ThreeDotsBaseView {

    override var animDuration = 500
        set(value) {
            field = value
            firstDelayDuration = value / 10
            secondDelayDuration = value / 5
        }

    override var interpolator = AnticipateOvershootInterpolator()

    var distanceToMove = 12

    var firstDelayDuration = 0
    var secondDelayDuration = 0

    constructor(
        context: Context,
        dotsRadius: Int,
        dotsDist: Int,
        firstDotColor: Int,
        secondDotColor: Int,
        thirdDotColor: Int
    ) : super(context, dotsRadius, dotsDist, firstDotColor, secondDotColor, thirdDotColor) {
        initView()
    }

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

    override fun initAttributes(attrs: AttributeSet) {

        val typedArray = context.obtainStyledAttributes(attrs, SlidingLoader, 0, 0)

        dotsRadius = typedArray.getDimensionPixelSize(SlidingLoader_slidingloader_dotsRadius, 30)
        dotsDist = typedArray.getDimensionPixelSize(SlidingLoader_slidingloader_dotsDist, 15)
        firstDotColor = typedArray.getColor(
            SlidingLoader_slidingloader_firstDotColor,
            ContextCompat.getColor(context, loader_selected))
        secondDotColor = typedArray.getColor(
            SlidingLoader_slidingloader_secondDotColor,
            ContextCompat.getColor(context, loader_selected))
        thirdDotColor = typedArray.getColor(
            SlidingLoader_slidingloader_thirdDotColor,
            ContextCompat.getColor(context, loader_selected))


        this.animDuration = typedArray.getInt(SlidingLoader_slidingloader_animDur, 500)

        this.distanceToMove = typedArray.getInteger(SlidingLoader_slidingloader_distanceToMove, 12)

        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val calWidth = (10 * dotsRadius) + (distanceToMove * dotsRadius) + (2 * dotsDist)
        val calHeight = 2 * dotsRadius

        setMeasuredDimension(calWidth, calHeight)
    }

    override fun initView() {
        removeAllViews()
        removeAllViewsInLayout()

        firstCircle = CircleView(context, dotsRadius, firstDotColor)
        secondCircle = CircleView(context, dotsRadius, secondDotColor)
        thirdCircle = CircleView(context, dotsRadius, thirdDotColor)

        val paramsFirstCircle = LayoutParams((2 * dotsRadius), 2 * dotsRadius)
        paramsFirstCircle.leftMargin = (2 * dotsRadius)

        val paramsSecondCircle = LayoutParams((2 * dotsRadius), 2 * dotsRadius)
        paramsSecondCircle.leftMargin = dotsDist

        val paramsThirdCircle = LayoutParams((2 * dotsRadius), 2 * dotsRadius)
        paramsThirdCircle.leftMargin = dotsDist
        paramsThirdCircle.rightMargin = (2 * dotsRadius)

        addView(firstCircle, paramsFirstCircle)
        addView(secondCircle, paramsSecondCircle)
        addView(thirdCircle, paramsThirdCircle)


        val loaderView = this

        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                startLoading(true)

                val vto = loaderView.viewTreeObserver
                vto.removeOnGlobalLayoutListener(this)
            }
        })

    }

    private fun startLoading(isForwardDir: Boolean) {

        val trans1Anim = getTranslateAnim(isForwardDir)
        if (isForwardDir) thirdCircle.startAnimation(trans1Anim) else firstCircle.startAnimation(trans1Anim)

        val trans2Anim = getTranslateAnim(isForwardDir)

        Handler().postDelayed({
            secondCircle.startAnimation(trans2Anim)
        }, firstDelayDuration.toLong())


        val trans3Anim = getTranslateAnim(isForwardDir)

        Handler().postDelayed({
            if (isForwardDir) firstCircle.startAnimation(trans3Anim) else thirdCircle.startAnimation(trans3Anim)
        }, secondDelayDuration.toLong())

        trans3Anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                startLoading(!isForwardDir)
            }

            override fun onAnimationStart(animation: Animation) {
            }
        })
    }


    private fun getTranslateAnim(isForwardDir: Boolean): TranslateAnimation {
        val transAnim = TranslateAnimation(
            if (isForwardDir) 0f
            else (distanceToMove * dotsRadius).toFloat(),
            if (isForwardDir) (distanceToMove * dotsRadius).toFloat()
            else 0f, 0f, 0f)

        transAnim.duration = animDuration.toLong()
        transAnim.fillAfter = true
        transAnim.interpolator = interpolator

        return transAnim
    }
}