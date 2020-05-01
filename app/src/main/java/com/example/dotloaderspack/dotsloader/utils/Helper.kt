package com.example.dotloaderspack.dotsloader.utils

import android.graphics.Color.*
import java.util.*
import kotlin.math.roundToInt

object Helper {

    fun adjustAlpha(color: Int, factor: Float): Int {
        val alpha = (alpha(color) * factor).roundToInt()
        val red = red(color)
        val green = green(color)
        val blue = blue(color)
        return argb(alpha, red, green, blue)
    }
}

fun ClosedFloatingPointRange<Float>.random() =
    (Random().nextFloat() * (endInclusive - start)) + start

fun ClosedRange<Int>.random() =
    Random().nextInt((endInclusive + 1) - start) + start
