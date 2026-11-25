package com.widgetcraft.app.config

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.widgetcraft.app.R
import com.widgetcraft.app.TemplateAdapter
import com.widgetcraft.app.data.Prefs
import com.widgetcraft.app.data.TemplateStore
import com.widgetcraft.app.widgets.ClockWidgetProvider
import com.widgetcraft.app.widgets.PhotoClockWidgetProvider
import com.widgetcraft.app.widgets.PhotoWidgetProvider
import com.widgetcraft.app.widgets.WidgetUpdater

/** Экран, который открывается при добавлении виджета: стиль + фото. */
class WidgetConfigActivity : AppCompatActivity() {

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private var pendingPhoto: Uri? = null

    private val pickPhoto = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            // Постоянное разрешение — фото не пропадёт после перезагрузки.
            contentResolver.takePersistableUriPermission(
                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            pendingPhoto = uri
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Если пользователь закрыл экран без сохранения — отменяем добавление.
        setResult(RESULT_CANCELED)

        appWidgetId = intent?.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        setContentView(R.layout.activity_config)

        val adapter = TemplateAdapter(TemplateStore.all) {}
        val grid = findViewById<RecyclerView>(R.id.templatesGrid)
        grid.layoutManager = GridLayoutManager(this, 3)
        grid.adapter = adapter

        findViewById<MaterialButton>(R.id.btnPickPhoto).setOnClickListener {
            pickPhoto.launch(arrayOf("image/*"))
        }

        findViewById<MaterialButton>(R.id.btnAdd).setOnClickListener {
            saveWidget(adapter)
        }
    }

    private fun saveWidget(adapter: TemplateAdapter) {
        val template = adapter.selectedTemplate() ?: TemplateStore.default

        Prefs.saveTemplate(this, appWidgetId, template.id)
        Prefs.savePhoto(this, appWidgetId, pendingPhoto)
        Prefs.saveKind(this, appWidgetId, kindOf(appWidgetId))

        val manager = AppWidgetManager.getInstance(this)
        WidgetUpdater.updateWidget(this, manager, appWidgetId)
        WidgetUpdater.ensureTickReceiver(this)

        setResult(
            RESULT_OK,
            Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        )
        finish()
    }

    /** Определяем тип виджета по провайдеру, который его создал. */
    private fun kindOf(appWidgetId: Int): String {
        val provider = AppWidgetManager.getInstance(this)
            .getAppWidgetInfo(appWidgetId)?.provider?.className
        return when (provider) {
            PhotoWidgetProvider::class.java.name -> WidgetUpdater.KIND_PHOTO
            PhotoClockWidgetProvider::class.java.name -> WidgetUpdater.KIND_PHOTO_CLOCK
            ClockWidgetProvider::class.java.name -> WidgetUpdater.KIND_CLOCK
            else -> WidgetUpdater.KIND_CLOCK
        }
    }
}
