package com.example.dotloaderspack.dotsloader.loaders

import android.R.color.*
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.*
import android.view.animation.Animation.*
import android.widget.LinearLayout
import androidx.core.content.ContextCompat.*
import com.example.dotloaderspack.R
import com.example.dotloaderspack.dotsloader.basicviews.CircularLoaderBaseView
import com.example.dotloaderspack.dotsloader.contracts.LoaderContract

class PullInLoader : LinearLayout, LoaderContract {

    var dotsRadius = 30
    var bigCircleRadius = 90

    var useMultipleColors = false

    var dotsColor = getColor(context, darker_gray)
    var dotsColorsArray = IntArray(8) { getColor(context, darker_gray) }

    var animDuration = 3000

    lateinit var circularLoaderBaseView: CircularLoaderBaseView

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

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PullInLoader, 0, 0)

        dotsRadius = typedArray.getDimensionPixelSize(R.styleable.PullInLoader_pullin_dotsRadius, 30)

        useMultipleColors = typedArray.getBoolean(R.styleable.PullInLoader_pullin_useMultipleColors, false)

        if (useMultipleColors) {
            val dotsArrayId = typedArray.getResourceId(R.styleable.PullInLoader_pullin_colorsArray, 0)

            dotsColorsArray = validateColorsArray(dotsArrayId, getColor(context, darker_gray))

        } else {
            dotsColor = typedArray.getColor(R.styleable.PullInLoader_pullin_dotsColor,
                getColor(context, darker_gray)
            )
        }

        bigCircleRadius =
            typedArray.getDimensionPixelSize(R.styleable.PullInLoader_pullin_bigCircleRadius, 90)

        animDuration = typedArray.getInt(R.styleable.PullInLoader_pullin_animDur, 2000)

        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val calWidthHeight = 2 * this.bigCircleRadius + 2 * dotsRadius
        setMeasuredDimension(calWidthHeight, calWidthHeight)
    }

    private fun validateColorsArray(arrayId: Int, color: Int): IntArray {


        return if (arrayId != 0) {
            val colors = IntArray(8)
            val colorsArray = resources.getIntArray(arrayId)
            for (i in 0..7) {
                colors[i] = if (colorsArray.size > i) colorsArray[i] else color
            }

            colors
        } else {
            IntArray(8) { color }
        }
    }


    private fun initView() {
        removeAllViews()
        removeAllViewsInLayout()

        circularLoaderBaseView = if (useMultipleColors) {
            CircularLoaderBaseView(context, dotsRadius, bigCircleRadius, dotsColorsArray)
        } else {
            CircularLoaderBaseView(context, dotsRadius, bigCircleRadius, dotsColor)
        }

        addView(circularLoaderBaseView)

        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                startLoading()
                this@PullInLoader.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)

        if (visibility != VISIBLE) {
            initView()
        } else {
            circularLoaderBaseView.clearAnimation()
        }
    }

    private fun startLoading() {

        circularLoaderBaseView.clearAnimation()

        val rotationAnim = getRotateAnimation()
        rotationAnim.setListener {
            val scaleAnimation = getScaleAnimation()
            scaleAnimation.setListener {
                startLoading()
            }

            circularLoaderBaseView.startAnimation(scaleAnimation)
        }
        circularLoaderBaseView.startAnimation(rotationAnim)
    }

    private fun Animation.setListener(onEnd: () -> Unit) {
        this.setAnimationListener(object : AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                onEnd()
            }

            override fun onAnimationStart(animation: Animation?) {
            }

        })
    }

    private fun getRotateAnimation(): RotateAnimation {

        val transAnim = RotateAnimation(
            0f,
            360f,
            RELATIVE_TO_SELF,
            0.5f,
            RELATIVE_TO_SELF,
            0.5f
        )
        transAnim.duration = animDuration.toLong()
        transAnim.fillAfter = true
        transAnim.repeatCount = 0
        transAnim.interpolator = AccelerateDecelerateInterpolator()

        return transAnim
    }

    private fun getScaleAnimation(): AnimationSet {
        val scaleAnimation = ScaleAnimation(
            1.0f,
            0.5f,
            1.0f,
            0.5f,
            (circularLoaderBaseView.width / 2).toFloat(),
            (circularLoaderBaseView.height / 2).toFloat()
        )

        scaleAnimation.repeatCount = 1
        scaleAnimation.repeatMode = REVERSE


        val alphaAnimation = AlphaAnimation(1.0f, 0.0f)
        alphaAnimation.repeatCount = 1
        alphaAnimation.repeatMode = REVERSE

        val animSet = AnimationSet(true)
        animSet.addAnimation(scaleAnimation)
        animSet.addAnimation(alphaAnimation)
        animSet.repeatCount = 1
        animSet.repeatMode = REVERSE
        animSet.duration = if (animDuration > 0) (animDuration / 8).toLong() else 100
        animSet.interpolator = AccelerateInterpolator()

        return animSet

    }


}