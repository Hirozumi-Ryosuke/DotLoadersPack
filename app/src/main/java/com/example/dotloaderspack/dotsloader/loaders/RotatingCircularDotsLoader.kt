package com.example.dotloaderspack.dotsloader.loaders

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.*
import android.view.animation.Animation
import android.view.animation.Animation.*
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.example.dotloaderspack.R
import com.example.dotloaderspack.R.color.*
import com.example.dotloaderspack.R.styleable.*
import com.example.dotloaderspack.dotsloader.basicviews.CircularLoaderBaseView
import com.example.dotloaderspack.dotsloader.contracts.LoaderContract

class RotatingCircularDotsLoader : LinearLayout, LoaderContract {

    var dotsRadius: Int = 30
    var dotsColor: Int = ContextCompat.getColor(context, loader_selected)
    var bigCircleRadius: Int = 90

    var animDuration: Int = 5000

    private lateinit var circularLoaderBaseView: CircularLoaderBaseView


    constructor(context: Context?) : super(context) {
        initView()
    }

    constructor(context: Context, dotsRadius: Int, bigCircleRadius: Int, dotsColor: Int) : super(context) {
        this.dotsRadius = dotsRadius
        this.bigCircleRadius = bigCircleRadius
        this.dotsColor = dotsColor
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

        val typedArray = context.obtainStyledAttributes(attrs, RotatingCircularDotsLoader, 0, 0)

        this.dotsRadius = typedArray.getDimensionPixelSize(
            RotatingCircularDotsLoader_rotatingcircular_dotsRadius, 30)

        this.dotsColor = typedArray.getColor(
            RotatingCircularDotsLoader_rotatingcircular_dotsColor,
            ContextCompat.getColor(context, loader_selected))

        this.bigCircleRadius = typedArray.getDimensionPixelSize(
            RotatingCircularDotsLoader_rotatingcircular_bigCircleRadius, 90)

        this.animDuration = typedArray.getInt(RotatingCircularDotsLoader_rotatingcircular_animDur, 5000)

        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val calWidth = 2 * this.bigCircleRadius + 2 * dotsRadius
        val calHeight = calWidth

        setMeasuredDimension(calWidth, calHeight)
    }

    private fun initView() {
        removeAllViews()
        removeAllViewsInLayout()

        circularLoaderBaseView = CircularLoaderBaseView(context, dotsRadius, bigCircleRadius, dotsColor)

        addView(circularLoaderBaseView)

        viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                startLoading()
                this@RotatingCircularDotsLoader.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)

        when {
            visibility != VISIBLE -> initView()
            else -> circularLoaderBaseView.clearAnimation()
        }
    }

    private fun startLoading() {

        val rotationAnim = getRotateAnimation()
        circularLoaderBaseView.startAnimation(rotationAnim)
    }

    private fun getRotateAnimation(): RotateAnimation {

        val transAnim = RotateAnimation(0f, 360f,
            RELATIVE_TO_SELF, 0.5f,
            RELATIVE_TO_SELF, 0.5f)
        transAnim.duration = animDuration.toLong()
        transAnim.fillAfter = true
        transAnim.repeatCount = INFINITE
        transAnim.repeatMode = RESTART
        transAnim.interpolator = LinearInterpolator()

        return transAnim
    }


}