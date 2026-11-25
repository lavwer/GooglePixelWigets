package com.widgetcraft.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.widgetcraft.app.data.TemplateStore
import com.widgetcraft.app.widgets.WidgetUpdater
import com.widgetcraft.app.widgets.ClockRenderer

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val grid = findViewById<RecyclerView>(R.id.templatesGrid)
        grid.layoutManager = GridLayoutManager(this, 3)
        grid.adapter = TemplateAdapter(TemplateStore.all) { template ->
            // Тап по шаблону в галерее — просто предпросмотр стиля,
            // настройка происходит при добавлении самого виджета.
        }

        // Подстраховка: если виджеты уже стоят на столе — перерисовать их.
        WidgetUpdater.ensureTickReceiver(this)
        WidgetUpdater.updateAll(this)
    }
}
