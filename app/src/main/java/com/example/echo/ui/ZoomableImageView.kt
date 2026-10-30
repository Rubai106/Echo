package com.example.echo.ui

import android.content.Context
import android.graphics.Matrix
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.min

class ZoomableImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {
    private val zoomMatrix = Matrix()
    private var baseScale = 1f
    private var currentScale = 1f

    private val scaleDetector = ScaleGestureDetector(
        context,
        object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                if (baseScale <= 0f) return false
                val targetScale = (currentScale * detector.scaleFactor)
                    .coerceIn(baseScale, baseScale * MAX_ZOOM)
                val scaleFactor = targetScale / currentScale
                zoomMatrix.postScale(scaleFactor, scaleFactor, detector.focusX, detector.focusY)
                currentScale = targetScale
                imageMatrix = zoomMatrix
                invalidate()
                return true
            }
        }
    )

    init {
        scaleType = ScaleType.MATRIX
        isClickable = true
    }

    fun fitToView() {
        val imageDrawable = drawable ?: return
        if (width <= 0 || height <= 0 || imageDrawable.intrinsicWidth <= 0 || imageDrawable.intrinsicHeight <= 0) {
            return
        }

        val imageWidth = imageDrawable.intrinsicWidth.toFloat()
        val imageHeight = imageDrawable.intrinsicHeight.toFloat()
        baseScale = min(width / imageWidth, height / imageHeight)
        currentScale = baseScale

        zoomMatrix.reset()
        zoomMatrix.setScale(baseScale, baseScale)
        val translateX = (width - imageWidth * baseScale) / 2f
        val translateY = (height - imageHeight * baseScale) / 2f
        zoomMatrix.postTranslate(translateX, translateY)
        imageMatrix = zoomMatrix
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        fitToView()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(event)
        return true
    }

    private companion object {
        const val MAX_ZOOM = 5f
    }
}
