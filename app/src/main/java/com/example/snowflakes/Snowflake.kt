package com.example.snowflakes

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
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
    
    private val branchCount = (5 + (Math.random() * 3).toInt())
    private val branchLengthVariation = 0.7f + Math.random().toFloat() * 0.6f
    private val subBranchCount = (2 + (Math.random() * 4).toInt())
    private val subBranchLengths = List(subBranchCount) { 0.2f + Math.random().toFloat() * 0.3f }
    private val subBranchAngles = List(subBranchCount) { 20f + Math.random().toFloat() * 40f }
    private val subBranchPositions = List(subBranchCount) { 0.2f + Math.random().toFloat() * 0.6f }
    
    private val hasSpikes = Math.random() > 0.5
    private val spikeCount = if (hasSpikes) (3 + (Math.random() * 4).toInt()) else 0
    private val spikePositions = List(spikeCount) { 0.1f + Math.random().toFloat() * 0.8f }
    private val spikeLengths = List(spikeCount) { 0.05f + Math.random().toFloat() * 0.1f }
    
    private val hasCrystals = Math.random() > 0.6
    private val crystalSize = 0.1f + Math.random().toFloat() * 0.15f
    private val crystalCount = if (hasCrystals) (2 + (Math.random() * 3).toInt()) else 0
    private val crystalPositions = List(crystalCount) { 0.3f + Math.random().toFloat() * 0.5f }
    
    private val centerSize = 2f + Math.random().toFloat() * 2f
    private val hasExtraDetails = Math.random() > 0.3
    
    private val baseAlpha = (180 + Math.random() * 75).toInt()
    
    private val paint = Paint().apply {
        color = this@Snowflake.color
        style = Paint.Style.STROKE
        strokeWidth = 1f + (Math.random().toFloat() * 1.5f)
        isAntiAlias = true
        alpha = baseAlpha
    }

    private val windSensitivity = 0.8f + Math.random().toFloat() * 0.4f
    private val turbulenceFrequency = 0.5f + Math.random().toFloat() * 1.5f
    private val turbulenceStrength = 0.2f + Math.random().toFloat() * 0.4f

    private val depth = Math.random().toFloat()
    private val scale = 0.5f + depth * 0.8f

    private var melting = false
    private var meltProgress = 0f
    
    private var velocityX = 0f
    private var velocityY = 0f
    private var isStuck = false
    
    fun move(speed: Float, windStrength: Float, time: Float) {
        if (isStuck) return
        
        val depthSpeed = 0.3f + depth * 0.7f
        val slowdown = 1 - (y / 10000f)
        
        x += velocityX
        y += velocityY + (speed * fallSpeed * depthSpeed * slowdown)
        
        velocityX *= 0.95f
        velocityY *= 0.95f
        
        angle += frequency
        val windEffect = windStrength * windSensitivity
        val turbulence = Math.sin(time * turbulenceFrequency + x * 0.1).toFloat() * turbulenceStrength
        
        x += ((Math.sin(angle.toDouble() + phaseShift) * amplitude + 
               Math.sin(angle.toDouble() * 0.5 + phaseShift) * (amplitude * 0.3) + 
               windEffect + turbulence) * slowdown).toFloat()

        rotationAngle += rotationFrequency
        val windRotationEffect = Math.abs(windStrength) * 0.5f
        rotation += (rotationSpeed * (1 + Math.sin(rotationAngle.toDouble()) * rotationAmplitude) + 
                    windRotationEffect).toFloat()
    }

    fun draw(canvas: Canvas) {
        val currentAlpha = if (melting) {
            (baseAlpha * depth * (1f - meltProgress)).toInt()
        } else {
            (baseAlpha * depth).toInt()
        }
        paint.alpha = currentAlpha
        
        canvas.save()
        canvas.translate(x, y)
        
        val currentScale = if (melting) {
            scale * (1f - meltProgress)
        } else {
            scale
        }
        canvas.scale(currentScale, currentScale)
        
        canvas.rotate(rotation)

        val angleStep = 360f / branchCount
        repeat(branchCount) { i ->
            canvas.save()
            canvas.rotate(angleStep * i)
            
            canvas.drawLine(0f, 0f, 0f, size * branchLengthVariation, paint)
            
            subBranchPositions.forEachIndexed { index, pos ->
                val y = size * branchLengthVariation * pos
                val length = size * subBranchLengths[index]
                val angle = subBranchAngles[index]
                
                canvas.save()
                canvas.translate(0f, y)
                
                canvas.save()
                canvas.rotate(angle)
                canvas.drawLine(0f, 0f, length, 0f, paint)
                canvas.restore()
                
                canvas.save()
                canvas.rotate(-angle)
                canvas.drawLine(0f, 0f, -length, 0f, paint)
                canvas.restore()
                
                canvas.restore()
            }
            
            if (hasSpikes) {
                spikePositions.forEachIndexed { index, pos ->
                    val y = size * branchLengthVariation * pos
                    val spikeLength = size * spikeLengths[index]
                    
                    canvas.save()
                    canvas.translate(0f, y)
                    canvas.rotate(45f)
                    canvas.drawLine(0f, 0f, spikeLength, 0f, paint)
                    canvas.restore()
                }
            }
            
            if (hasCrystals) {
                paint.style = Paint.Style.STROKE
                crystalPositions.forEach { pos ->
                    val y = size * branchLengthVariation * pos
                    val crystalRadius = size * crystalSize
                    
                    canvas.save()
                    canvas.translate(0f, y)
                    
                    val path = android.graphics.Path().apply {
                        moveTo(0f, -crystalRadius)
                        lineTo(crystalRadius * 0.7f, 0f)
                        lineTo(0f, crystalRadius)
                        lineTo(-crystalRadius * 0.7f, 0f)
                        close()
                    }
                    canvas.drawPath(path, paint)
                    
                    canvas.restore()
                }
            }
            
            canvas.restore()
        }

        paint.style = Paint.Style.FILL
        canvas.drawCircle(0f, 0f, centerSize, paint)
        
        if (Math.random() > 0.5) {
            paint.style = Paint.Style.STROKE
            canvas.drawCircle(0f, 0f, size * 0.12f, paint)
        }
        
        canvas.restore()
    }

    fun update(deltaTime: Float) {
        if (melting) {
            meltProgress += deltaTime * 0.5f
            if (meltProgress >= 1f) {
                reset()
            }
        }
    }

    private fun reset() {
        melting = false
        meltProgress = 0f
        y = -50f
        x = (Math.random() * 1000).toFloat()
        isStuck = false
        velocityX = 0f
        velocityY = 0f
    }

    fun handleCollision(obstacle: RectF) {
        if (!isStuck) {
            when ((Math.random() * 3).toInt()) {
                0 -> { // Скольжение по препятствию
                    val obstacleAngle = if (obstacle.width() > obstacle.height()) {
                        0f // Горизонтальное препятствие
                    } else {
                        90f // Вертикальное препятствие
                    }
                    
                    // Изменяем направление движения вдоль препятствия
                    velocityX = Math.cos(Math.toRadians(obstacleAngle.toDouble())).toFloat() * 2f
                    velocityY = Math.sin(Math.toRadians(obstacleAngle.toDouble())).toFloat() * 2f
                }
                1 -> { // Прилипание к препятствию
                    isStuck = true
                    velocityX = 0f
                    velocityY = 0f
                }
                2 -> { // Отскок
                    velocityX = (-2f + Math.random().toFloat() * 4f)
                    velocityY = -Math.random().toFloat() * 4f
                }
            }
        }
    }

    fun applyForce(forceX: Float, forceY: Float) {
        if (!isStuck) {
            velocityX += forceX
            velocityY += forceY
        }
    }
} 