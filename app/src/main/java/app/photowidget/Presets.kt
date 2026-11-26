package app.photowidget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context

object ShapeType {
    const val ROUNDED = 0
    const val CIRCLE = 1
    const val CAPSULE = 2
}

/** Размеры виджета на сетке лаунчера. Каждый размер — отдельный провайдер в списке виджетов. */
enum class SizePreset(
    val cols: Int,
    val rows: Int,
    val providerClass: Class<*>,
    val titleRes: Int,
    val descRes: Int
) {
    SMALL(2, 2, GalleryWidgetProvider::class.java, R.string.preset_small, R.string.preset_small_desc),
    WIDE(4, 2, WideWidgetProvider::class.java, R.string.preset_wide, R.string.preset_wide_desc),
    LARGE(4, 4, LargeWidgetProvider::class.java, R.string.preset_large, R.string.preset_large_desc);

    /** Номинальный размер ячеек: 70 * n - 30 dp */
    val nominalWidthDp: Int get() = 70 * cols - 30
    val nominalHeightDp: Int get() = 70 * rows - 30

    fun component(c: Context) = ComponentName(c, providerClass)

    companion object {
        fun forWidget(c: Context, id: Int): SizePreset {
            val name = AppWidgetManager.getInstance(c).getAppWidgetInfo(id)?.provider?.className
            for (p in values()) {
                if (p.providerClass.name == name) return p
            }
            return SMALL
        }

        fun totalWidgets(c: Context): Int {
            val manager = AppWidgetManager.getInstance(c)
            var total = 0
            for (p in values()) {
                total += manager.getAppWidgetIds(p.component(c)).size
            }
            return total
        }
    }
}
