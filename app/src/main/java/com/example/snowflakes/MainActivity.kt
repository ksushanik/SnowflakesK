package com.example.snowflakes

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
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
    
    // Параметры метели
    private var windStrength = 0f
    private var targetWindStrength = 0f
    private var windChangeTimer = 0f
    private val windChangeInterval = 3f // Интервал изменения силы ветра в секундах
    private var time = 0f

    init {
        post {
            repeat(200) {
                val x = random.nextFloat() * width
                val y = random.nextFloat() * height * 2 - height
                val depth = random.nextFloat()
                val size = (8f + random.nextFloat() * 15f) * (0.5f + depth * 0.8f)
                
                val color = when (random.nextInt(10)) {
                    0 -> Color.rgb(255, 255, 255)
                    1 -> Color.rgb(200, 220, 255)
                    2 -> Color.rgb(180, 200, 255)
                    3 -> Color.rgb(220, 220, 255)
                    4 -> Color.rgb(240, 240, 255)
                    5 -> Color.rgb(230, 230, 250)
                    6 -> Color.rgb(248, 248, 255)
                    7 -> Color.rgb(245, 245, 250)
                    8 -> Color.rgb(235, 235, 255)
                    else -> Color.rgb(250, 250, 255)
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

    private fun updateWind(deltaTime: Float) {
        windChangeTimer += deltaTime
        if (windChangeTimer >= windChangeInterval) {
            windChangeTimer = 0f
            targetWindStrength = -2f + random.nextFloat() * 4f // Случайная сила ветра от -2 до 2
        }
        
        // Плавное изменение силы ветра
        windStrength += (targetWindStrength - windStrength) * deltaTime * 0.5f
        time += deltaTime
    }

    @SuppressLint("StaticFieldLeak")
    private inner class SnowflakeAnimationTask : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void?): Void? {
            var lastTime = System.nanoTime()
            
            while (!isCancelled) {
                val currentTime = System.nanoTime()
                val deltaTime = (currentTime - lastTime) / 1_000_000_000f // в секундах
                lastTime = currentTime

                updateWind(deltaTime)
                
                snowflakes.forEach { snowflake ->
                    snowflake.move(3f, windStrength, time)

                    if (snowflake.y > height + 50f) {
                        snowflake.y = -50f
                        snowflake.x = random.nextFloat() * width
                    }
                    // Изменим границы в зависимости от направления ветра
                    val margin = if (windStrength > 0) 200f else 100f
                    if (snowflake.x > width + margin) {
                        snowflake.x = -margin
                    }
                    if (snowflake.x < -margin) {
                        snowflake.x = width.toFloat() + margin
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