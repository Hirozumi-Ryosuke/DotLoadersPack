package com.example.dotloaderspack.dotsloader.loaders

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import com.example.dotloaderspack.R
import com.example.dotloaderspack.R.styleable
import com.example.dotloaderspack.R.styleable.*
import com.example.dotloaderspack.dotsloader.contracts.CircularAbstractView
import com.example.dotloaderspack.dotsloader.utils.Utils
import java.util.*

class CircularDotsLoader : CircularAbstractView {

    private var timer: Timer? = null

    constructor(context: Context) : super(context) {
        initCordinates()
        initPaints()
        initShadowPaints()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initAttributes(attrs)
        initCordinates()
        initPaints()
        initShadowPaints()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initAttributes(attrs)
        initCordinates()
        initPaints()
        initShadowPaints()
    }

    override fun initAttributes(attrs: AttributeSet) {
        super.initAttributes(attrs)

        val typedArray = context.obtainStyledAttributes(attrs, CircularDotsLoader, 0, 0)

        this.bigCircleRadius = typedArray.getDimensionPixelSize(CircularDotsLoader_loader_bigCircleRadius, 60)

        typedArray.recycle()
    }


    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)

        when {
            visibility != VISIBLE -> timer?.cancel()
            shouldAnimate -> scheduleTimer()
        }
    }

    private fun scheduleTimer() {
        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                selectedDotPos++

                if (selectedDotPos > noOfDots) selectedDotPos = 1

                Utils.scanForActivity(context)?.runOnUiThread { invalidate() }
            }
        }, 0, animDur.toLong())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawCircle(canvas)
    }

    private fun drawCircle(canvas: Canvas) {
        val firstShadowPos = if (selectedDotPos == 1) 8 else selectedDotPos - 1
        val secondShadowPos = if (firstShadowPos == 1) 8 else firstShadowPos - 1

        for (i in 0 until noOfDots) {

            when {
                i + 1 == selectedDotPos -> {
                    canvas.drawCircle(
                        dotsXCorArr[i],
                        dotsYCorArr[i],
                        radius.toFloat(),
                        selectedCirclePaint!!
                    )
                }
                this.showRunningShadow && i + 1 == firstShadowPos -> {
                    canvas.drawCircle(
                        dotsXCorArr[i],
                        dotsYCorArr[i],
                        radius.toFloat(),
                        firstShadowPaint
                    )
                }
                this.showRunningShadow && i + 1 == secondShadowPos -> {
                    canvas.drawCircle(
                        dotsXCorArr[i],
                        dotsYCorArr[i],
                        radius.toFloat(),
                        secondShadowPaint
                    )
                }
                else -> {
                    canvas.drawCircle(
                        dotsXCorArr[i],
                        dotsYCorArr[i],
                        radius.toFloat(),
                        defaultCirclePaint!!
                    )
                }
            }

        }
    }
}