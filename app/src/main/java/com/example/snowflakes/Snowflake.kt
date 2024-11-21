package com.example.snowflakes

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import kotlin.math.sin

class Snowflake(
    var x: Float,
    var y: Float,
    private val size: Float,
    private val color: Int
) {
    private var angle: Float = 0f
    private val amplitude = 20f
    private val frequency = 0.01f
    private var rotation = 0f
    
    private val paint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 2f
        isAntiAlias = true
        alpha = 255
    }

    fun move(speed: Float) {
        val slowdown = 1 - (y / 8000f)
        y += speed * slowdown
        angle += frequency
        x += sin(angle) * amplitude * slowdown
        rotation += 0.5f
    }

    fun draw(canvas: Canvas) {
        canvas.save()
        
        canvas.translate(x, y)
        canvas.rotate(rotation)

        for (i in 0..5) {
            canvas.drawLine(0f, 0f, 0f, size, paint)
            canvas.drawLine(0f, size * 0.4f, size * 0.3f, size * 0.6f, paint)
            canvas.drawLine(0f, size * 0.4f, -size * 0.3f, size * 0.6f, paint)
            
            canvas.drawLine(0f, size * 0.7f, size * 0.2f, size * 0.8f, paint)
            canvas.drawLine(0f, size * 0.7f, -size * 0.2f, size * 0.8f, paint)
            
            paint.style = Paint.Style.STROKE
            canvas.drawLine(0f, size * 0.2f, size * 0.1f, size * 0.25f, paint)
            canvas.drawLine(0f, size * 0.2f, -size * 0.1f, size * 0.25f, paint)
            
            canvas.rotate(60f)
        }

        paint.style = Paint.Style.FILL
        canvas.drawCircle(0f, 0f, 3f, paint)
        paint.style = Paint.Style.STROKE
        canvas.drawCircle(0f, 0f, size * 0.15f, paint)

        canvas.restore()
    }
} 