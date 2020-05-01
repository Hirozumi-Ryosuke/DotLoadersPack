package com.example.dotloaderspack.dotsloader.loaders

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import com.example.dotloaderspack.R.styleable.*
import com.example.dotloaderspack.dotsloader.contracts.DotsLoaderBaseView
import com.example.dotloaderspack.dotsloader.utils.Utils.scanForActivity
import java.util.*

class LinearDotsLoader : DotsLoaderBaseView {

    var timer: Timer? = null

    var isSingleDir = true

    var diffRadius = 0
    var isFwdDir = true

    constructor(context: Context) : super(context) {
        initCordinates()
        initPaints()
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

        val typedArray = context.obtainStyledAttributes(attrs, LinearDotsLoader, 0, 0)

        noOfDots = typedArray.getInt(LinearDotsLoader_loader_noOfDots, 3)

        selRadius = typedArray.getDimensionPixelSize(LinearDotsLoader_loader_selectedRadius, radius + 10)

        dotsDistance = typedArray.getDimensionPixelSize(LinearDotsLoader_loader_dotsDist, 15)

        isSingleDir = typedArray.getBoolean(LinearDotsLoader_loader_isSingleDir, false)
        expandOnSelect = typedArray.getBoolean(LinearDotsLoader_loader_expandOnSelect, false)

        typedArray.recycle()
    }

    override fun initCordinates() {
        diffRadius = selRadius - radius

        dotsXCorArr = FloatArray(noOfDots)

        //init X cordinates for all dots
        for (i in 0 until noOfDots) {
            dotsXCorArr[i] = (i * dotsDistance + (i * 2 + 1) * radius).toFloat()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val calWidth: Int
        val calHeight: Int

        if (expandOnSelect) {
            calWidth = (2 * this.noOfDots * radius + (this.noOfDots - 1) * dotsDistance + 2 * diffRadius)
            calHeight = 2 * this.selRadius
        }
        else {
            calHeight = 2 * radius
            calWidth = (2 * this.noOfDots * radius + (this.noOfDots - 1) * dotsDistance)
        }
        setMeasuredDimension(calWidth, calHeight)
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)

        if (visibility != VISIBLE) {
            timer?.cancel()
        } else if (shouldAnimate) {
            scheduleTimer()
        }
    }

    private fun scheduleTimer() {
        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if (isSingleDir) {
                    selectedDotPos++
                    if (selectedDotPos > noOfDots) {
                        selectedDotPos = 1
                    }
                } else {
                    if (isFwdDir) {
                        selectedDotPos++
                        if (selectedDotPos == noOfDots) {
                            isFwdDir = !isFwdDir
                        }
                    } else {
                        selectedDotPos--
                        if (selectedDotPos == 1) {
                            isFwdDir = !isFwdDir
                        }
                    }
                }

                (scanForActivity(context))?.runOnUiThread { invalidate() }
            }
        }, 0, animDur.toLong())
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawCircle(canvas)
    }

    private fun drawCircle(canvas: Canvas) {
        for (i in 0 until noOfDots) {

            var xCor = dotsXCorArr[i]
            if (expandOnSelect) {
                if (i + 1 == selectedDotPos) {
                    xCor += diffRadius.toFloat()
                } else if (i + 1 > selectedDotPos) {
                    xCor += (2 * diffRadius).toFloat()
                }
            }

            var firstShadowPos: Int
            var secondShadowPos: Int

            if ((isFwdDir && selectedDotPos > 1) || selectedDotPos == noOfDots) {
                firstShadowPos = selectedDotPos - 1
                secondShadowPos = firstShadowPos - 1
            } else {
                firstShadowPos = selectedDotPos + 1
                secondShadowPos = firstShadowPos + 1
            }

            if (i + 1 == selectedDotPos) {
                canvas.drawCircle(
                    xCor,
                    (if (expandOnSelect) selRadius else radius).toFloat(),
                    (if (expandOnSelect) selRadius else radius).toFloat(),
                    selectedCirclePaint!!
                )
            } else if (showRunningShadow && i + 1 == firstShadowPos) {
                canvas.drawCircle(
                    xCor,
                    (if (expandOnSelect) selRadius else radius).toFloat(),
                    radius.toFloat(),
                    firstShadowPaint)
            } else if (showRunningShadow && i + 1 == secondShadowPos) {
                canvas.drawCircle(
                    xCor,
                    (if (expandOnSelect) selRadius else radius).toFloat(),
                    radius.toFloat(),
                    secondShadowPaint)
            } else {
                canvas.drawCircle(
                    xCor,
                    (if (expandOnSelect) selRadius else radius).toFloat(),
                    radius.toFloat(),
                    defaultCirclePaint!!
                )
            }

        }
    }

    var dotsDistance = 15
        set(value) {
            field = value
            initCordinates()
        }

    var noOfDots = 3
        set(noOfDots) {
            field = noOfDots
            initCordinates()
        }

    var selRadius = 38
        set(selRadius) {
            field = selRadius
            initCordinates()
        }

    var expandOnSelect = false
        set(expandOnSelect) {
            field = expandOnSelect
            initCordinates()
        }
}