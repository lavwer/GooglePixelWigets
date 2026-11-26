package app.photowidget

import android.appwidget.AppWidgetManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import app.photowidget.databinding.ActivityMainBinding
import app.photowidget.databinding.ItemWidgetCardBinding

class MainActivity : AppCompatActivity() {

    private lateinit var b: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.shapeRounded.shape = ShapeType.ROUNDED
        b.shapeCircle.shape = ShapeType.CIRCLE
        b.shapeCapsule.cols = 2
        b.shapeCapsule.shape = ShapeType.CAPSULE

        for (preset in SizePreset.values()) {
            addPresetCard(preset)
        }
    }

    override fun onResume() {
        super.onResume()
        b.tvCount.text = getString(R.string.widgets_count, SizePreset.totalWidgets(this))
    }

    private fun addPresetCard(preset: SizePreset) {
        val card = ItemWidgetCardBinding.inflate(layoutInflater, b.containerPresets, false)
        card.preview.cols = preset.cols
        card.preview.rows = preset.rows
        card.preview.shape = ShapeType.ROUNDED
        card.tvTitle.setText(preset.titleRes)
        card.tvSize.text = getString(R.string.size_fmt, preset.cols, preset.rows)
        card.tvDesc.setText(preset.descRes)
        card.btnAdd.setOnClickListener { pin(preset) }
        b.containerPresets.addView(card.root)
    }

    private fun pin(preset: SizePreset) {
        val manager = AppWidgetManager.getInstance(this)
        if (!manager.isRequestPinAppWidgetSupported) {
            Toast.makeText(this, R.string.pin_unsupported, Toast.LENGTH_LONG).show()
            return
        }
        // Лаунчер сам покажет подтверждение и откроет экран настройки виджета
        val requested = manager.requestPinAppWidget(preset.component(this), null, null)
        if (!requested) {
            Toast.makeText(this, R.string.pin_unsupported, Toast.LENGTH_LONG).show()
        }
    }
}
