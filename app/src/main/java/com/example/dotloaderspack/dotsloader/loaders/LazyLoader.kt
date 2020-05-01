package com.example.dotloaderspack.dotsloader.loaders

import android.R.anim.*
import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.Gravity
import android.view.Gravity.*
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.*
import android.view.animation.*
import android.view.animation.AnimationUtils.*
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.*
import com.example.dotloaderspack.R
import com.example.dotloaderspack.R.*
import com.example.dotloaderspack.R.color.*
import com.example.dotloaderspack.R.styleable.*
import com.example.dotloaderspack.dotsloader.basicviews.CircleView
import com.example.dotloaderspack.dotsloader.basicviews.ThreeDotsBaseView

class LazyLoader : ThreeDotsBaseView {

    var firstDelayDuration: Int = 100
    var secondDelayDuration: Int = 200

    constructor(context: Context, dotsRadius: Int, dotsDist: Int,
                firstDotColor: Int, secondDotColor: Int, thirdDotColor: Int)
            : super(context, dotsRadius, dotsDist, firstDotColor, secondDotColor, thirdDotColor){
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

        val typedArray = context.obtainStyledAttributes(attrs, LazyLoader, 0, 0)

        this.dotsRadius = typedArray.getDimensionPixelSize(LazyLoader_lazyloader_dotsRadius, 30)
        this.dotsDist = typedArray.getDimensionPixelSize(LazyLoader_lazyloader_dotsDist, 15)
        this.firstDotColor = typedArray.getColor(
            LazyLoader_lazyloader_firstDotColor,
            getColor(context, loader_selected)
        )
        this.secondDotColor = typedArray.getColor(
            LazyLoader_lazyloader_secondDotColor,
            getColor(context, loader_selected)
        )
        this.thirdDotColor = typedArray.getColor(
            LazyLoader_lazyloader_thirdDotColor,
            getColor(context, loader_selected)
        )

        this.animDuration = typedArray.getInt(LazyLoader_lazyloader_animDur, 500)

        this.interpolator = loadInterpolator(context,
            typedArray.getResourceId(
                LazyLoader_lazyloader_interpolator,
                linear_interpolator
            )) as AnticipateOvershootInterpolator

        this.firstDelayDuration = typedArray.getInt(LazyLoader_lazyloader_firstDelayDur, 100)
        this.secondDelayDuration = typedArray.getInt(LazyLoader_lazyloader_secondDelayDur, 200)

        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val calWidth = (6 * dotsRadius) + (2 * dotsDist)
        val calHeight = 6 * dotsRadius

        setMeasuredDimension(calWidth, calHeight)
    }

    override fun initView() {
        removeAllViews()
        removeAllViewsInLayout()

        firstCircle = CircleView(context, dotsRadius, firstDotColor)
        secondCircle = CircleView(context, dotsRadius, secondDotColor)
        thirdCircle = CircleView(context, dotsRadius, thirdDotColor)

        val params = LayoutParams((2 * dotsRadius), 2 * dotsRadius)
        params.leftMargin = dotsDist

        setVerticalGravity(BOTTOM)

        addView(firstCircle)
        addView(secondCircle, params)
        addView(thirdCircle, params)


        val loaderView = this

        viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                startLoading()

                val vto = loaderView.viewTreeObserver
                vto.removeOnGlobalLayoutListener(this)
            }
        })

    }

    private fun startLoading() {

        val trans1Anim = getTranslateAnim()
        firstCircle.startAnimation(trans1Anim)

        val trans2Anim = getTranslateAnim()

        Handler().postDelayed({
            secondCircle.startAnimation(trans2Anim)
        }, firstDelayDuration.toLong())


        val trans3Anim = getTranslateAnim()

        Handler().postDelayed({
            thirdCircle.startAnimation(trans3Anim)
        }, secondDelayDuration.toLong())

        trans3Anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                startLoading()
            }

            override fun onAnimationStart(animation: Animation) {
            }
        })
    }

    private fun getTranslateAnim(): TranslateAnimation {
        val transAnim = TranslateAnimation(0f, 0f, 0f, (-(4 * dotsRadius).toFloat()))
        transAnim.duration = animDuration.toLong()
        transAnim.fillAfter = true
        transAnim.repeatCount = 1
        transAnim.repeatMode = Animation.REVERSE
        transAnim.interpolator = interpolator

        return transAnim
    }
}