package com.example.snowflakes

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(SnowflakesView(this))
    }
}

class SnowflakesView(context: Context) : View(context) {
    private val snowflakes = mutableListOf<Snowflake>()
    private val random = Random
    private var animationTask: SnowflakeAnimationTask? = null

    init {
        post {
            repeat(100) {
                val x = random.nextFloat() * width
                val y = random.nextFloat() * height * 2 - height
                val size = random.nextFloat() * 20f + 10f
                val color = when (random.nextInt(7)) {
                    0 -> Color.rgb(255, 255, 255)
                    1 -> Color.rgb(200, 220, 255)
                    2 -> Color.rgb(180, 200, 255)
                    3 -> Color.rgb(220, 220, 255)
                    4 -> Color.rgb(240, 240, 255)
                    5 -> Color.rgb(230, 230, 250)
                    else -> Color.rgb(248, 248, 255)
                }
                snowflakes.add(Snowflake(x, y, size, color))
            }
            startAnimation()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.rgb(0, 0, 40))
        snowflakes.forEach { it.draw(canvas) }
        invalidate()
    }

    private fun startAnimation() {
        animationTask = SnowflakeAnimationTask()
        animationTask?.execute()
    }

    @SuppressLint("StaticFieldLeak")
    private inner class SnowflakeAnimationTask : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void?): Void? {
            while (!isCancelled) {
                snowflakes.forEach { snowflake ->
                    snowflake.move(3f)

                    if (snowflake.y > height + 50f) {
                        snowflake.y = -50f
                        snowflake.x = random.nextFloat() * width
                    }
                    if (snowflake.x > width + 100f) {
                        snowflake.x = -100f
                    }
                    if (snowflake.x < -100f) {
                        snowflake.x = width.toFloat() + 100f
                    }
                }
                postInvalidate()
                Thread.sleep(16)
            }
            return null
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animationTask?.cancel(true)
    }
} 