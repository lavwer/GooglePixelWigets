package app.photowidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.os.Bundle

/** Виджет 2×2 (базовый класс для остальных размеров). */
open class GalleryWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (id in appWidgetIds) {
            WidgetRenderer.update(context, appWidgetManager, id)
        }
    }

    // Вызывается при изменении размера виджета на рабочем столе — перекадрируем фото
    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle
    ) {
        WidgetRenderer.update(context, appWidgetManager, appWidgetId)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        for (id in appWidgetIds) {
            WidgetStore.delete(context, id)
        }
    }
}

/** Виджет 4×2. */
class WideWidgetProvider : GalleryWidgetProvider()

/** Виджет 4×4. */
class LargeWidgetProvider : GalleryWidgetProvider()
