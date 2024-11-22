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
    private var angle = (Math.random() * Math.PI * 2).toFloat()
    private val amplitude = (2f + Math.random().toFloat() * 15f)
    private val frequency = (0.001f + Math.random().toFloat() * 0.008f)
    private var rotation = (Math.random() * 360).toFloat()
    private val rotationSpeed = (-0.5f + Math.random().toFloat() * 1f)
    private val fallSpeed = (1f + Math.random().toFloat() * 2f)
    private val windEffect = (-0.3f + Math.random().toFloat() * 0.6f)
    private val phaseShift = (Math.random() * Math.PI * 2).toFloat()
    
    private val paint = Paint().apply {
        color = this@Snowflake.color
        style = Paint.Style.STROKE
        strokeWidth = 1.5f + (Math.random().toFloat() * 1f)
        isAntiAlias = true
        alpha = (200 + Math.random() * 55).toInt()
    }

    fun move(speed: Float) {
        val slowdown = 1 - (y / 10000f)
        y += speed * fallSpeed * slowdown
        angle += frequency
        x += (Math.sin(angle.toDouble() + phaseShift) * amplitude + 
              Math.sin(angle.toDouble() * 0.5 + phaseShift) * (amplitude * 0.3) + 
              windEffect).toFloat() * slowdown
        rotation += rotationSpeed * (1 + Math.sin(angle.toDouble()) * 0.2).toFloat()
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