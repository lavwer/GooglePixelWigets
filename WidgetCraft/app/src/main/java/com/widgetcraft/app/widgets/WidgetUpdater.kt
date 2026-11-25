package com.widgetcraft.app.widgets

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.TypedValue
import android.widget.RemoteViews
import com.widgetcraft.app.R
import com.widgetcraft.app.data.Prefs
import com.widgetcraft.app.data.TemplateStore

/** Обновляет все виды виджетов и раз в минуту переводит стрелки. */
object WidgetUpdater {

    const val KIND_PHOTO = "photo"
    const val KIND_CLOCK = "clock"
    const val KIND_PHOTO_CLOCK = "photo_clock"

    private var tickReceiver: BroadcastReceiver? = null

    fun updateWidget(context: Context, manager: AppWidgetManager, appWidgetId: Int) {
        val template = TemplateStore.byId(Prefs.templateId(context, appWidgetId))
        val views = when (Prefs.kind(context, appWidgetId)) {
            KIND_PHOTO -> buildPhoto(context, appWidgetId, template.scrimColor)
            KIND_PHOTO_CLOCK -> buildPhotoClock(context, appWidgetId, template)
            else -> buildClock(template)
        }
        manager.updateAppWidget(appWidgetId, views)
    }

    fun updateAll(context: Context) {
        val manager = AppWidgetManager.getInstance(context)
        listOf(
            PhotoWidgetProvider::class.java,
            ClockWidgetProvider::class.java,
            PhotoClockWidgetProvider::class.java
        ).forEach { clazz ->
            val ids = manager.getAppWidgetIds(ComponentName(context, clazz))
            ids.forEach { updateWidget(context, manager, it) }
        }
    }

    /** ACTION_TIME_TICK нельзя зарегистрировать в манифесте — только так. */
    fun ensureTickReceiver(context: Context) {
        if (tickReceiver != null) return
        tickReceiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                updateAll(ctx)
            }
        }
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_TIME_TICK)
            addAction(Intent.ACTION_TIMEZONE_CHANGED)
            addAction(Intent.ACTION_TIME_CHANGED)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(tickReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            context.registerReceiver(tickReceiver, filter)
        }
    }

    private fun buildClock(template: com.widgetcraft.app.data.WidgetTemplate): RemoteViews {
        val views = RemoteViews("com.widgetcraft.app", R.layout.widget_clock)
        views.setImageViewBitmap(R.id.dial, ClockRenderer.renderDial(template))
        return views
    }

    private fun buildPhoto(context: Context, appWidgetId: Int, scrimColor: Int): RemoteViews {
        val views = RemoteViews("com.widgetcraft.app", R.layout.widget_photo)
        val uri = Prefs.photoUri(context, appWidgetId)
        val bitmap = uri?.let { PhotoRenderer.render(context, it, withScrim = false) }
        if (bitmap != null) {
            views.setImageViewBitmap(R.id.photo, bitmap)
        } else {
            views.setImageViewResource(R.id.photo, android.R.drawable.ic_menu_gallery)
        }
        return views
    }

    private fun buildPhotoClock(
        context: Context,
        appWidgetId: Int,
        template: com.widgetcraft.app.data.WidgetTemplate
    ): RemoteViews {
        val views = RemoteViews("com.widgetcraft.app", R.layout.widget_photo_clock)
        val uri = Prefs.photoUri(context, appWidgetId)
        val bitmap = uri?.let {
            PhotoRenderer.render(context, it, withScrim = true, scrimColor = template.scrimColor)
        }
        if (bitmap != null) {
            views.setImageViewBitmap(R.id.photo, bitmap)
        } else {
            views.setImageViewResource(R.id.photo, android.R.drawable.ic_menu_gallery)
        }
        views.setTextColor(R.id.clock, template.digitalTextColor)
        views.setTextViewTextSize(R.id.clock, TypedValue.COMPLEX_UNIT_SP, 38f)
        return views
    }
}
