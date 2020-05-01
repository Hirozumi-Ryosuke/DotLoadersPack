package com.example.dotloaderspack.dotsloader.loaders

import android.R.color.*
import android.content.Context
import android.util.AttributeSet
import android.view.ViewTreeObserver.*
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.Animation.*
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams.*
import androidx.core.content.ContextCompat.*
import com.example.dotloaderspack.R.styleable.*
import com.example.dotloaderspack.dotsloader.basicviews.CircleView
import com.example.dotloaderspack.dotsloader.contracts.LoaderContract
import com.example.dotloaderspack.dotsloader.utils.random

class LightsLoader : LinearLayout, LoaderContract {

    var noOfCircles = 3
        set(value) {
            field = if (value < 1) 1 else value
        }

    var circleRadius = 30
    var circleDistance = 10

    var circleColor = getColor(context, holo_purple)

    private var calWidthHeight = 0

    private lateinit var circlesList: ArrayList<CircleView>


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

    constructor(context: Context, noOfCircles: Int, circleRadius: Int, circleDistance: Int, circleColor: Int) : super(context) {
        this.noOfCircles = noOfCircles
        this.circleRadius = circleRadius
        this.circleDistance = circleDistance
        this.circleColor = circleColor

        initView()
    }

    override fun initAttributes(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, LightsLoader, 0, 0)

        noOfCircles = typedArray.getInteger(LightsLoader_lights_noOfCircles, 3)

        circleRadius = typedArray.getDimensionPixelSize(LightsLoader_lights_circleRadius, 30)
        circleDistance = typedArray.getDimensionPixelSize(LightsLoader_lights_circleDistance, 10)

        circleColor = typedArray.getColor(LightsLoader_lights_circleColor, getColor(context, holo_purple))

        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (calWidthHeight == 0) {
            calWidthHeight = (2 * circleRadius * noOfCircles) + ((noOfCircles - 1) * circleDistance)
        }

        setMeasuredDimension(calWidthHeight, calWidthHeight)
    }


    private fun initView() {
        removeAllViews()
        removeAllViewsInLayout()

        orientation = VERTICAL

        circlesList = ArrayList()

        if (calWidthHeight == 0) {
            calWidthHeight = (2 * circleRadius * noOfCircles) + ((noOfCircles - 1) * circleDistance)
        }

        for (countI in 0 until noOfCircles) {
            val linearLayout = LinearLayout(context)
            linearLayout.orientation = HORIZONTAL
            val params = LayoutParams(
                WRAP_CONTENT, WRAP_CONTENT
            )

            if (countI != 0) {
                params.topMargin = circleDistance
            }

            linearLayout.layoutParams = params

            for (countJ in 0 until noOfCircles) {
                val circleView = CircleView(context, circleRadius, circleColor)

                val innerParam = LayoutParams(
                    WRAP_CONTENT, WRAP_CONTENT
                )

                if (countJ != 0) innerParam.leftMargin = circleDistance

                linearLayout.addView(circleView, innerParam)
                circlesList.add(circleView)
            }

            addView(linearLayout)
        }

        viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                startLoading()
                this@LightsLoader.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    private fun startLoading() {
        for (count in 0 until noOfCircles) {
            for (item in circlesList) {
                item.startAnimation(getAlphaAnimation())
            }
        }
    }

    private fun getAlphaAnimation(): Animation {
        val fromAplha = (0.5f..1.0f).random()
        val toAplha = (0.1f..0.5f).random()

        return AlphaAnimation(fromAplha, toAplha)
            .apply {
                duration = (100..1000).random().toLong()
                repeatMode = REVERSE
                repeatCount = INFINITE
            }
    }
}