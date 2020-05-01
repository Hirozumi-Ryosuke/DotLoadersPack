package com.example.dotloaderspack

import android.R.color.*
import android.os.Bundle
import android.view.animation.AnticipateOvershootInterpolator
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.*
import com.example.dotloaderspack.R.color.*
import com.example.dotloaderspack.R.id.*
import com.example.dotloaderspack.R.layout.*
import com.example.dotloaderspack.dotsloader.loaders.*


class MainActivity : AppCompatActivity() {

    lateinit var containerLL: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(main_lights)

        supportActionBar?.title = "LightsLoader"

        containerLL = findViewById(container)

        initLinearDotsLoader()
        initCircularDotsLoader()
        initLazyLoader()
        initTashieLoader()
        initSlidongLoader()
        initRotatingCircularDotsLoader()

        initTrailingCirculerDotsLoader()

        initZeeLoader()

        initAllianceLoader()
        initLightsLoader()
    }

    private fun initLightsLoader() {
        val lightsLoader = LightsLoader(
            this,
            5,
            30,
            10,
            getColor(this, red)
        )


        containerLL.addView(lightsLoader)
    }

    private fun initAllianceLoader() {
        val allianceLoader = AllianceLoader(
            this,
            40,
            6,
            true,
            10,
            getColor(this, red),
            getColor(this, amber),
            getColor(this, green)
        ).apply {
            animDuration = 5000
        }

        containerLL.addView(allianceLoader)
    }

    private fun initZeeLoader() {
        val zeeLoader = ZeeLoader(
            this,
            60,
            4,
            getColor(this, red),
            getColor(this, red)
        )
            .apply {
                animDuration = 200
            }

        containerLL.addView(zeeLoader)
    }

    private fun initTrailingCirculerDotsLoader() {
        val trailingCircularDotsLoader = TrailingCircularDotsLoader(
            this,
            24,
            getColor(this, holo_green_light),
            100,
            5)
            .apply {
                animDuration = 1200
                animDelay = 200
            }

        containerLL.addView(trailingCircularDotsLoader)
    }


    private fun initRotatingCircularDotsLoader() {
        val loader = RotatingCircularDotsLoader(this,
            20, 60, getColor(this, red)
        )
            .apply {
                animDuration = 10000
            }

        containerLL.addView(loader)
    }

    private fun initSlidongLoader() {
        val sliding = SlidingLoader(this, 20, 5,
            getColor(this, red),
            getColor(this, yellow),
            getColor(this, green)
        ).apply {
            animDuration = 1500
            distanceToMove = 12
        }

        containerLL.addView(sliding)
    }

    private fun initTashieLoader() {
        val tashie = TashieLoader(
            this,
            5,
            30,
            10,
            getColor(this, green)
        )
            .apply {
                animDuration = 500
                animDelay = 100
                interpolator = AnticipateOvershootInterpolator()
            }
        containerLL.addView(tashie)
    }

    private fun initLazyLoader() {
        val lazyLoader = LazyLoader(
            this,
            15,
            5,
            getColor(this, loader_selected),
            getColor(this, loader_selected),
            getColor(this, loader_selected)
        )
            .apply {
                animDuration = 500
                firstDelayDuration = 100
                secondDelayDuration = 200
                interpolator = AnticipateOvershootInterpolator()
            }

        /*val lazyLoader = LazyLoader(this).apply{
            animDuration = 500
            firstDelayDuration = 100
            secondDelayDuration = 200
        }
        lazyLoader.dotsRadius = 60
        lazyLoader.dotsDist = 60*/

        containerLL.addView(lazyLoader)
    }

    private fun initCircularDotsLoader() {
        val cirLoader = CircularDotsLoader(this@MainActivity)
        cirLoader.setPadding(20, 20, 20, 20)
        cirLoader.defaultColor = getColor(this, blue_delfault)
        cirLoader.selectedColor = getColor(this, blue_selected)
        cirLoader.bigCircleRadius = 116
        cirLoader.radius = 40
        cirLoader.animDur = 100
        cirLoader.firstShadowColor = getColor(this, pink_selected)
        cirLoader.secondShadowColor = getColor(this, purple_selected)
        cirLoader.showRunningShadow = true

        containerLL.addView(cirLoader)
    }

    private fun initLinearDotsLoader() {
        val loader = LinearDotsLoader(this)
        loader.defaultColor = getColor(this, loader_defalut)
        loader.selectedColor = getColor(this, loader_selected)
        loader.isSingleDir = false
        loader.noOfDots = 5
        loader.selRadius = 60
        loader.expandOnSelect = false
        loader.radius = 40
        loader.dotsDistance = 20
        loader.animDur = 1000
        loader.firstShadowColor = getColor(this, pink_selected)
        loader.secondShadowColor = getColor(this, purple_selected)
        loader.showRunningShadow = false
        containerLL.addView(loader)


    }
}
