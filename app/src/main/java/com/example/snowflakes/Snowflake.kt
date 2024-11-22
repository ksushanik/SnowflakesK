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
    private val rotationSpeed = (-1.5f + Math.random().toFloat() * 3f)
    private val rotationAmplitude = (0.2f + Math.random().toFloat() * 0.8f)
    private val rotationFrequency = (0.001f + Math.random().toFloat() * 0.005f)
    private var rotationAngle = (Math.random() * Math.PI * 2).toFloat()
    private val fallSpeed = (1f + Math.random().toFloat() * 2f)
    private val windEffect = (-0.3f + Math.random().toFloat() * 0.6f)
    private val phaseShift = (Math.random() * Math.PI * 2).toFloat()
    
    private val branchLengthVariation = 0.8f + Math.random().toFloat() * 0.4f
    private val sideLength = (0.2f + Math.random().toFloat() * 0.2f)
    private val sideAngle = (25f + Math.random().toFloat() * 15f)
    private val centerSize = 2f + Math.random().toFloat() * 2f
    private val hasExtraDetails = Math.random() > 0.3
    
    private val paint = Paint().apply {
        color = this@Snowflake.color
        style = Paint.Style.STROKE
        strokeWidth = 1f + (Math.random().toFloat() * 1.5f)
        isAntiAlias = true
        alpha = (180 + Math.random() * 75).toInt()
    }

    fun move(speed: Float) {
        val slowdown = 1 - (y / 10000f)
        y += speed * fallSpeed * slowdown
        angle += frequency
        x += (Math.sin(angle.toDouble() + phaseShift) * amplitude + 
              Math.sin(angle.toDouble() * 0.5 + phaseShift) * (amplitude * 0.3) + 
              windEffect).toFloat() * slowdown
        rotationAngle += rotationFrequency
        rotation += rotationSpeed * (1 + Math.sin(rotationAngle.toDouble()) * rotationAmplitude).toFloat()
    }

    fun draw(canvas: Canvas) {
        canvas.save()
        
        canvas.translate(x, y)
        canvas.rotate(rotation)

        for (i in 0..5) {
            canvas.drawLine(0f, 0f, 0f, size * branchLengthVariation, paint)
            
            val sideX = size * sideLength
            val sideY = size * 0.4f
            canvas.save()
            canvas.rotate(sideAngle)
            canvas.drawLine(0f, sideY, sideX, sideY * 1.2f, paint)
            canvas.restore()
            
            canvas.save()
            canvas.rotate(-sideAngle)
            canvas.drawLine(0f, sideY, -sideX, sideY * 1.2f, paint)
            canvas.restore()

            if (hasExtraDetails) {
                canvas.drawLine(0f, size * 0.7f, size * 0.15f, size * 0.8f, paint)
                canvas.drawLine(0f, size * 0.7f, -size * 0.15f, size * 0.8f, paint)
                
                paint.strokeWidth = paint.strokeWidth * 0.5f
                canvas.drawLine(0f, size * 0.3f, size * 0.1f, size * 0.35f, paint)
                canvas.drawLine(0f, size * 0.3f, -size * 0.1f, size * 0.35f, paint)
                paint.strokeWidth = paint.strokeWidth * 2f
            }

            canvas.rotate(60f)
        }

        paint.style = Paint.Style.FILL
        canvas.drawCircle(0f, 0f, centerSize, paint)
        
        if (hasExtraDetails) {
            paint.style = Paint.Style.STROKE
            canvas.drawCircle(0f, 0f, size * 0.12f, paint)
        }

        canvas.restore()
    }
} 