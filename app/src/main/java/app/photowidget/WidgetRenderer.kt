package app.photowidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.net.Uri
import android.view.View
import android.widget.RemoteViews
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.sqrt

object WidgetRenderer {

    // Ограничение на размер bitmap в RemoteViews (иначе launcher может отклонить обновление)
    private const val MAX_PIXELS = 900_000f

    /** Рисует фото в нужном размере: заполнить (обрезка) или вписать, со скруглёнными углами. */
    fun drawBitmap(src: Bitmap, w: Int, h: Int, fit: Boolean, radiusPx: Float): Bitmap {
        val out = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val sw = src.width.toFloat()
        val sh = src.height.toFloat()
        val scale = if (fit) minOf(w / sw, h / sh) else maxOf(w / sw, h / sh)
        val dw = sw * scale
        val dh = sh * scale
        val dx = (w - dw) / 2f
        val dy = (h - dh) / 2f

        val shader = BitmapShader(src, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        shader.setLocalMatrix(Matrix().apply {
            setScale(scale, scale)
            postTranslate(dx, dy)
        })
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).also { it.shader = shader }

        val dest = if (fit) RectF(dx, dy, dx + dw, dy + dh) else RectF(0f, 0f, w.toFloat(), h.toFloat())
        val r = minOf(radiusPx, dest.width() / 2f, dest.height() / 2f)
        Canvas(out).drawRoundRect(dest, r, r, paint)
        return out
    }

    fun update(context: Context, manager: AppWidgetManager, id: Int) {
        val views = RemoteViews(context.packageName, R.layout.widget_gallery)

        val rendered = try {
            renderWidget(context, manager, id)
        } catch (e: Throwable) {
            null
        }

        if (rendered != null) {
            views.setImageViewBitmap(R.id.widget_image, rendered)
            views.setViewVisibility(R.id.widget_image, View.VISIBLE)
            views.setViewVisibility(R.id.widget_empty, View.GONE)
        } else {
            views.setViewVisibility(R.id.widget_image, View.GONE)
            views.setViewVisibility(R.id.widget_empty, View.VISIBLE)
        }

        // Нажатие на виджет открывает экран настройки (смена фото и параметров)
        val intent = Intent(context, ConfigureActivity::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_CONFIGURE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id)
            data = Uri.parse("photowidget://widget/$id")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val pending = PendingIntent.getActivity(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_root, pending)

        manager.updateAppWidget(id, views)
    }

    private fun renderWidget(context: Context, manager: AppWidgetManager, id: Int): Bitmap? {
        val src = WidgetStore.decodeFile(WidgetStore.imageFile(context, id)) ?: return null

        val opts = manager.getAppWidgetOptions(id)
        val density = context.resources.displayMetrics.density
        val wDp = max(
            opts.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 0),
            opts.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, 0)
        ).takeIf { it > 0 } ?: 110
        val hDp = max(
            opts.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 0),
            opts.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, 0)
        ).takeIf { it > 0 } ?: 110

        var w = wDp * density
        var h = hDp * density
        val k = minOf(1f, sqrt(MAX_PIXELS / (w * h)))
        w *= k
        h *= k

        val settings = WidgetStore.load(context, id)
        val out = drawBitmap(
            src,
            max(1, w.roundToInt()),
            max(1, h.roundToInt()),
            settings.fit,
            settings.radiusDp * density * k
        )
        src.recycle()
        return out
    }
}
