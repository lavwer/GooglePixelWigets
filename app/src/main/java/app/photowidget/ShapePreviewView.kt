package app.photowidget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.google.android.material.color.MaterialColors
import kotlin.math.min

/** Схематичное превью виджета: нужные пропорции и форма, цвета берутся из темы Material 3. */
class ShapePreviewView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    var cols: Int = 1
        set(value) {
            field = value
            invalidate()
        }

    var rows: Int = 1
        set(value) {
            field = value
            invalidate()
        }

    var shape: Int = ShapeType.ROUNDED
        set(value) {
            field = value
            invalidate()
        }

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val hillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val sunPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rect = RectF()
    private val clip = Path()
    private val hills = Path()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val container = MaterialColors.getColor(this, com.google.android.material.R.attr.colorPrimaryContainer)
        val onContainer = MaterialColors.getColor(this, com.google.android.material.R.attr.colorOnPrimaryContainer)

        val pad = 4f * resources.displayMetrics.density
        val aw = width - 2f * pad
        val ah = height - 2f * pad
        if (aw <= 0f || ah <= 0f) return

        val ratio = cols.toFloat() / rows.toFloat()
        var w = aw
        var h = w / ratio
        if (h > ah) {
            h = ah
            w = h * ratio
        }
        if (shape == ShapeType.CIRCLE) {
            val d = min(w, h)
            w = d
            h = d
        }
        val l = (width - w) / 2f
        val t = (height - h) / 2f
        rect.set(l, t, l + w, t + h)

        val short = min(w, h)
        val r = when (shape) {
            ShapeType.CIRCLE, ShapeType.CAPSULE -> short / 2f
            else -> short * 0.2f
        }

        bgPaint.color = container
        canvas.drawRoundRect(rect, r, r, bgPaint)

        canvas.save()
        clip.reset()
        clip.addRoundRect(rect, r, r, Path.Direction.CW)
        canvas.clipPath(clip)

        hills.reset()
        hills.moveTo(l, t + h)
        hills.lineTo(l + w * 0.30f, t + h * 0.58f)
        hills.lineTo(l + w * 0.50f, t + h * 0.78f)
        hills.lineTo(l + w * 0.68f, t + h * 0.50f)
        hills.lineTo(l + w, t + h * 0.80f)
        hills.lineTo(l + w, t + h)
        hills.close()
        hillPaint.color = onContainer
        hillPaint.alpha = 90
        canvas.drawPath(hills, hillPaint)

        sunPaint.color = onContainer
        sunPaint.alpha = 140
        canvas.drawCircle(l + w * 0.70f, t + h * 0.30f, short * 0.09f, sunPaint)

        canvas.restore()
    }
}
