package com.svbackend.natai.android.coil


import android.graphics.*
import androidx.core.graphics.createBitmap
import kotlin.math.max

// taken from https://github.com/Commit451/coil-transformations
// as it is not available on mavenCentral
class CropTransformation(
    private val cropType: CropType = CropType.CENTER
) {
    private val defaultPaint = Paint(Paint.DITHER_FLAG or Paint.FILTER_BITMAP_FLAG)
    enum class CropType {
        TOP,
        CENTER,
        BOTTOM
    }

    fun transform(input: Bitmap, w: Int, h: Int): Bitmap {
        val output = createBitmap(w, h, Bitmap.Config.ARGB_8888)

        output.setHasAlpha(true)

        val scaleX = w.toFloat() / input.width
        val scaleY = h.toFloat() / input.height
        val scale = max(scaleX, scaleY)

        val scaledWidth = scale * input.width
        val scaledHeight = scale * input.height
        val left = (w - scaledWidth) / 2
        val top = getTop(h.toFloat(), scaledHeight)
        val targetRect = RectF(left, top, left + scaledWidth, top + scaledHeight)

        val canvas = Canvas(output)
        canvas.drawBitmap(input, null, targetRect, null)

        return output
    }

    fun centerCrop(inBitmap: Bitmap, width: Int, height: Int): Bitmap {
        if (inBitmap.width == width && inBitmap.height == height) {
            return inBitmap
        }
        // From ImageView/Bitmap.createScaledBitmap.
        val scale: Float
        val dx: Float
        val dy: Float
        val m = Matrix()
        if (inBitmap.width * height > width * inBitmap.height) {
            scale = height.toFloat() / inBitmap.height.toFloat()
            dx = (width - inBitmap.width * scale) * 0.5f
            dy = 0f
        } else {
            scale = width.toFloat() / inBitmap.width.toFloat()
            dx = 0f
            dy = (height - inBitmap.height * scale) * 0.5f
        }

        m.setScale(scale, scale)
        m.postTranslate((dx + 0.5f).toInt().toFloat(), (dy + 0.5f).toInt().toFloat())

        val result = createBitmap(width, height, Bitmap.Config.ARGB_8888)
        // We don't add or remove alpha, so keep the alpha setting of the Bitmap we were given.
        setAlpha(inBitmap, result)

        applyMatrix(inBitmap, result, m)
        return result
    }

    private fun setAlpha(inBitmap: Bitmap, outBitmap: Bitmap) {
        outBitmap.setHasAlpha(inBitmap.hasAlpha())
    }

    private fun applyMatrix(inBitmap: Bitmap, targetBitmap: Bitmap, matrix: Matrix) {
        try {
            val canvas = Canvas(targetBitmap)
            canvas.drawBitmap(inBitmap, matrix, defaultPaint)
            clear(canvas)
        } finally {
        }
    }

    private fun clear(canvas: Canvas) {
        canvas.setBitmap(null)
    }

    private fun getTop(height: Float, scaledHeight: Float): Float {
        return when (cropType) {
            CropType.TOP -> 0f
            CropType.CENTER -> (height - scaledHeight) / 2
            CropType.BOTTOM -> height - scaledHeight
        }
    }
}