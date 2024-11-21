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
            repeat(20) {
                val x = random.nextFloat() * width
                val y = random.nextFloat() * height
                val size = random.nextFloat() * 20f + 20f
                snowflakes.add(Snowflake(x, y, size, Color.argb(255, 255, 255, 255)))
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
                    snowflake.move(2f)

                    if (snowflake.y > height + 50f) {
                        snowflake.y = -50f
                        snowflake.x = random.nextFloat() * width
                    }
                    if (snowflake.x > width + 50f) {
                        snowflake.x = -50f
                    }
                    if (snowflake.x < -50f) {
                        snowflake.x = width.toFloat()
                    }
                }
                postInvalidate()
                Thread.sleep(32)
            }
            return null
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animationTask?.cancel(true)
    }
} 