package com.example.dotloaderspack

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_lineardotsloader)

        supportActionBar?.title = "BounceLoader"

        containerLL = findViewById<LinearLayout>(R.id.container)

    }

    private fun initBounceLoader() {
        val bounceLoader = BounceLoader(context = this,
            ballRadius = 60,
            ballColor = ContextCompat.getColor(this, R.color.red),
            showShadow = true,
            shadowColor = ContextCompat.getColor(this, R.color.black))
            .apply {
                animDuration = 1000
            }

        containerLL.addView(bounceLoader)
    }

}
