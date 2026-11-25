package com.widgetcraft.app.widgets

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.net.Uri
import kotlin.math.min

/** Обрезает фото под виджет: центральный кроп, скруглённые углы, тёмная подложка под часы. */
object PhotoRenderer {

    private const val RENDER_SIZE = 720

    fun render(
        context: Context,
        uri: Uri,
        cornerRadius: Float = 36f,
        withScrim: Boolean = false,
        scrimColor: Int = 0x99000000.toInt()
    ): Bitmap? {
        val src = context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it)
        } ?: return null

        val size = RENDER_SIZE
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Центральный кроп в квадрат
        val side = min(src.width, src.height)
        val left = (src.width - side) / 2
        val top = (src.height - side) / 2
        val srcRect = android.graphics.Rect(left, top, left + side, top + side)
        val dstRect = RectF(0f, 0f, size.toFloat(), size.toFloat())
        canvas.drawBitmap(src, srcRect, dstRect, paint)

        // Затемнение снизу, чтобы часы читались поверх фото
        if (withScrim) {
            paint.shader = LinearGradient(
                0f, size * 0.45f, 0f, size.toFloat(),
                0x00000000, scrimColor,
                Shader.TileMode.CLAMP
            )
            canvas.drawRect(0f, size * 0.45f, size.toFloat(), size.toFloat(), paint)
            paint.shader = null
        }

        // Скруглённые углы через маску
        val rounded = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val rc = Canvas(rounded)
        val path = Path().apply {
            addRoundRect(RectF(0f, 0f, size.toFloat(), size.toFloat()),
                cornerRadius, cornerRadius, Path.Direction.CW)
        }
        rc.clipPath(path)
        rc.drawBitmap(bitmap, 0f, 0f, paint)

        return rounded
    }
}
