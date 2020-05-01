package com.example.dotloaderspack.dotsloader.basicviews

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.example.dotloaderspack.R
import com.example.dotloaderspack.dotsloader.contracts.AbstractLinearLayout

abstract class ThreeDotsBaseView : AbstractLinearLayout {

    var firstDotColor = ContextCompat.getColor(context, R.color.loader_defalut)

    var secondDotColor = ContextCompat.getColor(context, R.color.loader_defalut)

    var thirdDotColor = ContextCompat.getColor(context, R.color.loader_defalut)

    protected lateinit var firstCircle: CircleView
    protected lateinit var secondCircle: CircleView
    protected lateinit var thirdCircle: CircleView

    constructor(context: Context, dotsRadius: Int, dotsDist: Int,
                firstDotColor: Int, secondDotColor: Int, thirdDotColor: Int) : super(context) {
        this.dotsRadius = dotsRadius
        this.dotsDist = dotsDist
        this.firstDotColor = firstDotColor
        this.secondDotColor = secondDotColor
        this.thirdDotColor = thirdDotColor
    }

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
}
