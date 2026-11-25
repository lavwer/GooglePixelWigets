package com.widgetcraft.app.widgets

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import com.widgetcraft.app.data.WidgetTemplate
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/** Рисует аналоговый циферблат в Bitmap — так виджет может выглядеть любым образом. */
object ClockRenderer {

    private const val RENDER_SIZE = 480

    fun renderDial(
        template: WidgetTemplate,
        now: Long = System.currentTimeMillis()
    ): Bitmap {
        val size = RENDER_SIZE
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val cx = size / 2f
        val cy = size / 2f
        val radius = size / 2f
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Фон: радиальный градиент
        paint.shader = RadialGradient(
            cx, cy, radius,
            template.dialColorFrom, template.dialColorTo,
            Shader.TileMode.CLAMP
        )
        canvas.drawCircle(cx, cy, radius, paint)
        paint.shader = null

        // Внешнее кольцо
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = size * 0.028f
        paint.color = template.ringColor
        canvas.drawCircle(cx, cy, radius - paint.strokeWidth, paint)

        // Минутные и часовые метки
        val tickOuter = radius - size * 0.05f
        val minorLen = size * 0.038f
        val majorLen = size * 0.085f
        paint.strokeCap = Paint.Cap.ROUND
        for (i in 0 until 60) {
            val major = i % 5 == 0
            val angle = Math.toRadians(i * 6.0 - 90.0)
            val cosA = cos(angle).toFloat()
            val sinA = sin(angle).toFloat()
            paint.strokeWidth = if (major) size * 0.016f else size * 0.007f
            paint.color = if (major) template.tickMajorColor else template.tickMinorColor
            canvas.drawLine(
                cx + cosA * (tickOuter - if (major) majorLen else minorLen),
                cy + sinA * (tickOuter - if (major) majorLen else minorLen),
                cx + cosA * tickOuter,
                cy + sinA * tickOuter,
                paint
            )
        }

        // Время
        val calendar = Calendar.getInstance().apply { timeInMillis = now }
        val hours = calendar.get(Calendar.HOUR_OF_DAY) % 12
        val minutes = calendar.get(Calendar.MINUTE)
        val seconds = calendar.get(Calendar.SECOND)

        // Стрелки: часы, минуты, секунды
        drawHand(canvas, cx, cy, radius * 0.46f, hours * 30f + minutes * 0.5f,
            template.hourHandColor, size * 0.028f, paint)
        drawHand(canvas, cx, cy, radius * 0.68f, minutes * 6f + seconds * 0.1f,
            template.minuteHandColor, size * 0.020f, paint)
        if (template.showSeconds) {
            drawHand(canvas, cx, cy, radius * 0.76f, seconds * 6f,
                template.secondHandColor, size * 0.008f, paint)
        }

        // Центральная точка
        paint.style = Paint.Style.FILL
        paint.color = template.secondHandColor
        canvas.drawCircle(cx, cy, size * 0.022f, paint)

        return bitmap
    }

    private fun drawHand(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        length: Float,
        angleDeg: Float,
        color: Int,
        strokeWidth: Float,
        paint: Paint
    ) {
        val angle = Math.toRadians(angleDeg - 90.0)
        val cosA = cos(angle).toFloat()
        val sinA = sin(angle).toFloat()
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeWidth = strokeWidth
        paint.color = color
        // «Хвост» стрелки за центром — выглядит дороже
        canvas.drawLine(
            cx - cosA * length * 0.16f, cy - sinA * length * 0.16f,
            cx + cosA * length, cy + sinA * length,
            paint
        )
    }
}
