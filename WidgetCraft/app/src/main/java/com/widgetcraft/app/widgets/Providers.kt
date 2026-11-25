package com.widgetcraft.app.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import com.widgetcraft.app.data.Prefs

abstract class BaseWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { WidgetUpdater.updateWidget(context, appWidgetManager, it) }
        WidgetUpdater.ensureTickReceiver(context)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        appWidgetIds.forEach { Prefs.clear(context, it) }
    }
}

class PhotoWidgetProvider : BaseWidgetProvider()

class ClockWidgetProvider : BaseWidgetProvider()

class PhotoClockWidgetProvider : BaseWidgetProvider()
