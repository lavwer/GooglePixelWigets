package com.widgetcraft.app.data

import android.content.Context
import android.net.Uri

/** Хранение настроек каждого виджета: тип, шаблон, фото. */
object Prefs {

    private const val NAME = "widget_prefs"

    private fun prefs(context: Context) =
        context.getSharedPreferences(NAME, Context.MODE_PRIVATE)

    fun saveKind(context: Context, appWidgetId: Int, kind: String) {
        prefs(context).edit().putString("kind_$appWidgetId", kind).apply()
    }

    fun kind(context: Context, appWidgetId: Int): String =
        prefs(context).getString("kind_$appWidgetId", "clock") ?: "clock"

    fun saveTemplate(context: Context, appWidgetId: Int, templateId: String) {
        prefs(context).edit().putString("template_$appWidgetId", templateId).apply()
    }

    fun templateId(context: Context, appWidgetId: Int): String? =
        prefs(context).getString("template_$appWidgetId", null)

    fun savePhoto(context: Context, appWidgetId: Int, uri: Uri?) {
        val editor = prefs(context).edit()
        if (uri == null) editor.remove("photo_$appWidgetId")
        else editor.putString("photo_$appWidgetId", uri.toString())
        editor.apply()
    }

    fun photoUri(context: Context, appWidgetId: Int): Uri? =
        prefs(context).getString("photo_$appWidgetId", null)?.let(Uri::parse)

    fun clear(context: Context, appWidgetId: Int) {
        prefs(context).edit()
            .remove("kind_$appWidgetId")
            .remove("template_$appWidgetId")
            .remove("photo_$appWidgetId")
            .apply()
    }
}
